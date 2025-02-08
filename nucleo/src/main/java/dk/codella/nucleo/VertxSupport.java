package dk.codella.nucleo;

import io.vertx.core.Vertx;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

public class VertxSupport {

  @Produces
  @Singleton
  public Vertx vertx() {
    return Vertx.vertx();
  }
}
