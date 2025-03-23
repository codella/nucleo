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
    // REF: https://arjan-tijms.omnifaces.org/2017/08/dynamic-beans-in-cdi.html
    // REF: https://github.com/javaee-samples/javaee8-samples/blob/master/cdi/dynamic-bean/src/main/java/org/javaee8/cdi/dynamic/bean/CdiExtension.java
    event.addBean()
        .scope(Singleton.class)
        .types(Vertx.class)
        .id("vertx")
        .produceWith(i -> vertx);
  }
}
