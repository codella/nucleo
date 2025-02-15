package dk.codella.nucleo;

import com.google.common.collect.Sets;
import io.smallrye.config.inject.ConfigExtension;
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

  public void start() {
    log.atInfo().log("NUCLEO-000: Bootstrap started");

    weld.addBeanClasses(beansToAdd.toArray(new Class<?>[0]));

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

    // TODO: make start() return a Future, which is the combination of the following futures returned by deployVerticle
    if (withRoutesHttpServer) {
      var verticle = container.select(RoutesHttpVerticle.class).get();
      vertx.deployVerticle(verticle)
        .onFailure(this::logVerticleDeploymentFailureAndExit);
    }

    if (withResteasyHttpServer) {
      var verticle = container.select(ResteasyHttpVerticle.class).get();
      vertx.deployVerticle(verticle)
        .onFailure(this::logVerticleDeploymentFailureAndExit);
    }

    log.atInfo().log("NUCLEO-000: Bootstrap completed");
  }

  private void logVerticleDeploymentFailureAndExit(Throwable cause) {
    log.atSevere().withCause(cause).log("Verticle deployment failed.");
    System.exit(-1);
  }

}
