package dk.codella.nucleo;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import lombok.extern.flogger.Flogger;

@Flogger
public class VertxSupport {

  @Produces
  @Singleton
  public Vertx vertx() {
    var vertx = Vertx.vertx();

    vertx.exceptionHandler(t -> {
      log.atSevere().withCause(t).log("BOOM");
    });

    return vertx;
  }

  @Produces
  @Singleton
  public Router router(Vertx vertx) {
    return Router.router(vertx);
  }
}
