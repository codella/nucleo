package dk.codella.nucleo;

import io.smallrye.config.inject.ConfigExtension;
import io.smallrye.faulttolerance.FaultToleranceExtension;
import io.smallrye.health.AsyncHealthCheckFactory;
import io.smallrye.health.SmallRyeHealthReporter;
import io.vertx.core.*;
import lombok.extern.flogger.Flogger;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Flogger
public class Nucleo {
  private final Weld weld;
  private Vertx vertx;

  // BEGIN -- Builder properties
  private boolean withResteasyHttpServer = false;
  private final Map<Class<? extends Verticle>, DeploymentOptions> verticles = new HashMap<>();
  // END -- Builder properties

  public Nucleo() {
    this(Vertx.vertx());
  }

  public Nucleo(Vertx vertx) {
    this.weld = new Weld();
    this.weld.disableDiscovery();
    this.weld.addExtension(new VertxExtension(vertx));
  }

  public Nucleo withWeld(Consumer<Weld> callback) {
    callback.accept(weld);
    return this;
  }

  public Nucleo withVerticleBeanClass(Class<? extends Verticle> verticle) {
    withVerticleBeanClass(verticle, new DeploymentOptions());
    return this;
  }

  public Nucleo withVerticleBeanClass(Class<? extends Verticle> verticle, DeploymentOptions options) {
    verticles.put(verticle, options);
    return this;
  }

  public Nucleo withResteasyHttpServer() {
    withResteasyHttpServer = true;
    return this;
  }

  public Future<String> start() {
    log.atInfo().log("NUCLEO-000: Bootstrap started");

    // COMMENTARY:
    // This makes SmallRye Config to work with Weld, and make the @ConfigProperty annotation work as intended
    weld.addExtension(new ConfigExtension());

    // COMMENTARY:
    // Adds the CDI extension needed for SmallRye Fault Tolerance to work
    weld.addExtension(new FaultToleranceExtension());

    // COMMENTARY:
    // Adds a bean necessary for SmallRye Health to work
    weld.addBeanClasses(SmallRyeHealthReporter.class, AsyncHealthCheckFactory.class);

    // COMMENTARY:
    // Adds the Health verticle in the container
    weld.addBeanClasses(HealthVerticle.class);

    if (withResteasyHttpServer) {
      weld.addBeanClasses(ResteasyHttpVerticle.class);
      weld.addExtension(new ResteasyExtension());
    }

    // COMMENTARY:
    // Adding custom verticles as beans so they can benefit from all the features provided by Nucleo
    verticles.keySet().forEach(weld::addBeanClass);

    final WeldContainer container = weld.initialize();
    vertx = container.select(Vertx.class).get();

    // COMMENTARY:
    // This registers the default exception handler for exceptions not being caught by the exception handlers
    // being registered on the Context.
    vertx.exceptionHandler(t -> {
      log.atSevere().withCause(t).log("An unhandled error occurred");
    });

    return deployHealthVerticle(vertx, container)
        .compose(e -> deployResteasyHttpVerticleIfEnabled(vertx, container))
        .compose(e -> deployVerticles(vertx, container))
        .onSuccess(e -> log.atInfo().log("NUCLEO-001: Bootstrap completed"))
        .onFailure(this::logVerticleDeploymentFailureAndExit);
  }

  public Future<Void> shutdown() {
    Promise<Void> promise = Promise.promise();
    // TODO: also Weld must shutdown + other components (SmallRye-* and such) if needed
    if (vertx != null) {
      vertx.close(ar -> {
        if (ar.succeeded()) {
          promise.complete();
        } else {
          promise.fail(ar.cause());
        }
      });
    }

    return promise.future();
  }

  private Future<String> deployHealthVerticle(Vertx vertx, WeldContainer container) {
    var verticle = container.select(HealthVerticle.class).get();
    return vertx.deployVerticle(verticle);
  }

  private Future<String> deployResteasyHttpVerticleIfEnabled(Vertx vertx, WeldContainer container) {
    if (withResteasyHttpServer) {
      var verticle = container.select(ResteasyHttpVerticle.class).get();
      return vertx.deployVerticle(verticle);
    } else {
      return Future.succeededFuture();
    }
  }

  private Future<String> deployVerticles(Vertx vertx, WeldContainer container) {
    var all = verticles.keySet().stream()
        .map(verticle -> vertx.
            deployVerticle(container.select(verticle).get(), verticles.get(verticle))
        )
        .toList();

    return Future.all(all).compose(e -> Future.succeededFuture("CHANGE ME"));
  }

  private void logVerticleDeploymentFailureAndExit(Throwable cause) {
    log.atSevere().withCause(cause).log("NUCLEO-002: Bootstrap failed");
    System.exit(-1);
  }

}
