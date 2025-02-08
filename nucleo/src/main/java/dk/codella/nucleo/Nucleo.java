package dk.codella.nucleo;

import com.google.common.collect.Sets;
import io.vertx.core.Vertx;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

import java.util.HashSet;
import java.util.Set;

public class Nucleo {
  private final Weld weld;

  // BEGIN -- Builder properties
  private final Set<Class<?>> beansToAdd = new HashSet<>();
  private boolean withHttpServer = false;
  // END -- Builder properties

  public Nucleo() {
    this.weld = new Weld();
    this.weld.disableDiscovery();
    this.weld.addBeanClass(VertxSupport.class);
  }

  public Nucleo withBeanClasses(Class<?> ...classes) {
    beansToAdd.addAll(Sets.newHashSet(classes));
    return this;
  }

  public Nucleo withHttpServer() {
    withHttpServer = true;
    return this;
  }

  public void start() {
    weld.addBeanClasses(beansToAdd.toArray(new Class<?>[0]));

    if (withHttpServer) {
      weld.addBeanClass(HttpVerticle.class);
    }

    WeldContainer container = weld.initialize();

    Vertx vertx = container.select(Vertx.class).get();

    if (withHttpServer) {
      for (var provider : container.select(HttpRoutesProvider.class)) {
        provider.run();
      }

      var httpVerticle = container.select(HttpVerticle.class).get();
      vertx.deployVerticle(httpVerticle);
    }
  }

}
