package dk.codella.nucleo;

import com.google.common.collect.Sets;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Set;

public class RoutesHttpVerticle extends AbstractVerticle {
  private final Router router;
  private final Set<HttpRoutesProvider> routerResources;
  private final int port;

  @Inject
  public RoutesHttpVerticle(
      Router router,
      Instance<HttpRoutesProvider> routerResources,
      @ConfigProperty(name = "nucleo.http-server.routes-verticle.port", defaultValue = "8081") int port
  ) {
    this.router = router;
    this.routerResources = Sets.newHashSet(routerResources);
    this.port = port;
  }

  @Override
  public void start(Promise<Void> startPromise) {
    try {
      HttpServer server = vertx.createHttpServer();

      // COMMENTARY:
      // Running Runnable#run() on the routerResources will make them register routes
      routerResources.forEach(Runnable::run);
      server.requestHandler(router);

      server
        .listen(port)
        .<Void>mapEmpty()
        .onComplete(startPromise);
    } catch (Exception t) {
      startPromise.fail(t);
    }
  }

}
