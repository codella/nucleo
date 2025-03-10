package dk.codella.nucleo;

import com.google.common.collect.Sets;
import io.smallrye.config.inject.ConfigExtension;
import io.smallrye.faulttolerance.FaultToleranceExtension;
import io.smallrye.health.AsyncHealthCheckFactory;
import io.smallrye.health.SmallRyeHealthReporter;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import lombok.extern.flogger.Flogger;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

import java.util.HashSet;
import java.util.Set;

@Flogger
public class Nucleo {
  private final Weld weld;
  private Vertx vertx;

  // BEGIN -- Builder properties
  private final Set<Class<?>> beansToAdd = new HashSet<>();
  private boolean withResteasyHttpServer = false;
  // END -- Builder properties

  public Nucleo() {
    this.weld = new Weld();
    this.weld.disableDiscovery();
    this.weld.addBeanClass(VertxSupport.class);
  }

  public Nucleo withBeanClasses(Class<?>... classes) {
    beansToAdd.addAll(Sets.newHashSet(classes));
    return this;
  }

  public Nucleo withResteasyHttpServer() {
    withResteasyHttpServer = true;
    return this;
  }

  public Future<String> start() {
    log.atInfo().log("NUCLEO-000: Bootstrap started");

    weld.addBeanClasses(beansToAdd.toArray(new Class<?>[0]));

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

    WeldContainer container = weld.initialize();
    vertx = container.select(Vertx.class).get();

    // COMMENTARY:
    // This registers the default exception handler for exceptions not being caught by the exception handlers
    // being registered on the Context.
    vertx.exceptionHandler(t -> {
      log.atSevere().withCause(t).log("An unhandled error occurred");
    });

    return deployHealthVerticle(vertx, container)
        .compose(e -> deployResteasyHttpVerticleIfEnabled(vertx, container))
        .onSuccess(e -> log.atInfo().log("NUCLEO-001: Bootstrap completed"))
        .onFailure(this::logVerticleDeploymentFailureAndExit);
  }

  public Future<Void> shutdown() {
    Promise<Void> promise = Promise.promise();
    // TODO: also Weld must shutdown + other components if needed
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

  private void logVerticleDeploymentFailureAndExit(Throwable cause) {
    log.atSevere().withCause(cause).log("NUCLEO-002: Bootstrap failed");
    System.exit(-1);
  }

}
