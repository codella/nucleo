package dk.codella.nucleo;

import com.google.common.collect.Sets;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import org.jboss.resteasy.plugins.server.vertx.VertxRequestHandler;
import org.jboss.resteasy.plugins.server.vertx.VertxResteasyDeployment;

import java.util.Set;

@ApplicationScoped
public class ResteasyHttpVerticle extends AbstractVerticle {
  private final Set<ResteasyResource> resteasyResources;

  @Inject
  public ResteasyHttpVerticle(
      Instance<ResteasyResource> resteasyResources
  ) {
    this.resteasyResources = Sets.newHashSet(resteasyResources);
  }

  @Override
  public void start(Promise<Void> startPromise) {
    HttpServer server = vertx.createHttpServer();

    VertxResteasyDeployment deployment = new VertxResteasyDeployment();
    deployment.start();

    for (var resource : resteasyResources) {
      deployment.getRegistry().addSingletonResource(resource);
    }

    server.requestHandler(new VertxRequestHandler(vertx, deployment));

    server.listen(8080);
  }

}
