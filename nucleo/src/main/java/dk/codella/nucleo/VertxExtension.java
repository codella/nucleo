package dk.codella.nucleo;

import io.vertx.core.Vertx;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.AfterBeanDiscovery;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.inject.Singleton;
import lombok.extern.flogger.Flogger;

import java.util.function.Supplier;

@Flogger
public class VertxExtension implements Extension {

  private final Supplier<Vertx> vertxSupplier;

  public VertxExtension(Supplier<Vertx> vertxSupplier) {
    this.vertxSupplier = vertxSupplier;
  }

  void afterBeanDiscovery(@Observes AfterBeanDiscovery event, BeanManager bm) {
    event.addBean()
        .scope(Singleton.class)
        .types(Vertx.class)
        .id("vertx")
        .produceWith(i -> vertxSupplier.get());
  }
}
