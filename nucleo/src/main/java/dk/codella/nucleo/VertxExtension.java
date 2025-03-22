package dk.codella.nucleo;

import io.vertx.core.Vertx;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.AfterBeanDiscovery;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.inject.Singleton;
import lombok.extern.flogger.Flogger;

@Flogger
public class VertxExtension implements Extension {

  private final Vertx vertx;

  public VertxExtension(Vertx vertx) {
    this.vertx = vertx;
  }

  void afterBeanDiscovery(@Observes AfterBeanDiscovery event) {
    event.addBean()
        .scope(Singleton.class)
        .types(Vertx.class)
        .id("vertx")
        .produceWith(i -> vertx);
  }
}
