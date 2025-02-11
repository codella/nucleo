package dk.codella.nucleo;

import com.google.common.collect.Sets;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import java.util.Set;

@ApplicationScoped
public class RoutesHttpVerticle extends AbstractVerticle {
  private final Router router;
  private final Set<HttpRoutesProvider> routerResources;

  @Inject
  public RoutesHttpVerticle(
      Router router,
      Instance<HttpRoutesProvider> routerResources
  ) {
    this.router = router;
    this.routerResources = Sets.newHashSet(routerResources);
  }

  @Override
  public void start(Promise<Void> startPromise) {
    HttpServer server = vertx.createHttpServer();

    routerResources.forEach(Runnable::run);
    server.requestHandler(router);

    server.listen(8081);
  }

}
