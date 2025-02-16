package dk.codella.nucleo;

import com.google.common.collect.Sets;
import io.smallrye.config.inject.ConfigExtension;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import lombok.extern.flogger.Flogger;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

import java.util.HashSet;
import java.util.Set;

@Flogger
public class Nucleo {
  private final Weld weld;

  // BEGIN -- Builder properties
  private final Set<Class<?>> beansToAdd = new HashSet<>();
  private boolean withRoutesHttpServer = false;
  private boolean withResteasyHttpServer = false;
  // END -- Builder properties

  public Nucleo() {
    this.weld = new Weld();
    this.weld.disableDiscovery();
    this.weld.addBeanClass(VertxSupport.class);
    // COMMENTARY:
    // This makes SmallRye Config to work with Weld, and make the @ConfigProperty annotation work as intended
    this.weld.addExtension(new ConfigExtension());
  }

  public Nucleo withBeanClasses(Class<?>... classes) {
    beansToAdd.addAll(Sets.newHashSet(classes));
    return this;
  }

  public Nucleo withRoutesHttpServer() {
    withRoutesHttpServer = true;
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
    // Adding verticles to the CDI container as beans, so they can benefit CDI
    if (withRoutesHttpServer) {
      weld.addBeanClass(RoutesHttpVerticle.class);
    }

    if (withResteasyHttpServer) {
      weld.addBeanClasses(ResteasyHttpVerticle.class);
    }

    WeldContainer container = weld.initialize();
    Vertx vertx = container.select(Vertx.class).get();

    // COMMENTARY:
    // This registers the default exception handler for exceptions not being caught by the exception handlers
    // being registered on the Context.
    vertx.exceptionHandler(t -> {
      log.atSevere().withCause(t).log("An unhandled error occurred");
    });

    return deployRoutesHttpVerticleIfEnabled(vertx, container)
        .compose(e -> deployResteasyHttpVerticleIfEnabled(vertx, container))
        .onSuccess(e -> log.atInfo().log("NUCLEO-001: Bootstrap completed"))
        .onFailure(this::logVerticleDeploymentFailureAndExit);
  }

  private Future<String> deployRoutesHttpVerticleIfEnabled(Vertx vertx, WeldContainer container) {
    if (withRoutesHttpServer) {
      var verticle = container.select(RoutesHttpVerticle.class).get();
      return vertx.deployVerticle(verticle);
    } else {
      return Future.succeededFuture();
    }
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
