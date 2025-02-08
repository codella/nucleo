package dk.codella.nucleo;

import io.vertx.core.Vertx;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class Nucleo {
  private final Weld weld;
  private WeldContainer container;

  // BEGIN -- Builder properties
  private final Set<Class<?>> beansToAdd = new HashSet<>();
  private boolean withHttpServer = false;
  // END -- Builder properties

  public Nucleo() {
    this.weld = new Weld();
    this.weld.disableDiscovery();
    this.weld.addBeanClass(VertxSupport.class);
  }

  public Nucleo withBeanClasses(Supplier<Set<Class<?>>> callback) {
    beansToAdd.addAll(callback.get());
    return this;
  }

  public Nucleo withHttpServer() {
    withHttpServer = true;
    return this;
  }

  public void start() {
    weld.addBeanClasses(beansToAdd.toArray(new Class<?>[0]));
    container = weld.initialize();

    if (withHttpServer) {
      Vertx vertx = container.select(Vertx.class).get();
      vertx.deployVerticle(HttpVerticle.class);
    }
  }

}
