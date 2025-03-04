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
    return Vertx.vertx();
  }
}
