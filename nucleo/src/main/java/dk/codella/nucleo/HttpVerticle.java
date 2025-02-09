package dk.codella.nucleo;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Router;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import org.jboss.resteasy.plugins.server.vertx.VertxRequestHandler;
import org.jboss.resteasy.plugins.server.vertx.VertxResteasyDeployment;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class HttpVerticle extends AbstractVerticle {
  private final Router router;
  private final Set<HttpRoutesProvider> routerResources;
  private final Set<ResteasyResource> resteasyResources;

  @Inject
  public HttpVerticle(
      Router router,
      Instance<HttpRoutesProvider> routerResources,
      Instance<ResteasyResource> resteasyResources
  ) {
    this.router = router;
    this.routerResources = Sets.newHashSet(routerResources);
    this.resteasyResources = Sets.newHashSet(resteasyResources);
  }

  @Override
  public void start(Promise<Void> startPromise) {
    HttpServer server = vertx.createHttpServer();

    if (!routerResources.isEmpty() && !resteasyResources.isEmpty()) {
      throw new IllegalStateException("NUCLEO-001: HTTP server cannot be used with both Router and Resteasy.");
    }

    if (!routerResources.isEmpty()) {
      routerResources.forEach(Runnable::run);
      server.requestHandler(router);
    } else if (!resteasyResources.isEmpty()) {
      VertxResteasyDeployment deployment = new VertxResteasyDeployment();
      deployment.start();

      for (var resource : resteasyResources) {
        deployment.getRegistry().addSingletonResource(resource);
      }

      server.requestHandler(new VertxRequestHandler(vertx, deployment));
    }

    server.listen(8080);
  }

}
