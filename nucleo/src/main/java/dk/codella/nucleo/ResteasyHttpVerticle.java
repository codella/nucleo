package dk.codella.nucleo;

import com.google.common.collect.Sets;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.plugins.server.vertx.VertxRequestHandler;
import org.jboss.resteasy.plugins.server.vertx.VertxResteasyDeployment;

import java.util.Set;

public class ResteasyHttpVerticle extends AbstractVerticle {
  private final Set<ResteasyResource> resteasyResources;
  private final int port;

  @Inject
  public ResteasyHttpVerticle(
      Instance<ResteasyResource> resteasyResources,
      @ConfigProperty(name = "app.http.resteasy-verticle.port", defaultValue = "8081") int port`
  ) {
    this.port = port;
    this.resteasyResources = Sets.newHashSet(resteasyResources);
  }

  @Override
  public void start(Promise<Void> startPromise) {
    try {
      HttpServer server = vertx.createHttpServer();

      VertxResteasyDeployment deployment = new VertxResteasyDeployment();
      deployment.start();

      for (var resource : resteasyResources) {
        deployment.getRegistry().addSingletonResource(resource);
      }

      server.requestHandler(new VertxRequestHandler(vertx, deployment));

      server
        .listen(port)
        .<Void>mapEmpty()
        .onComplete(startPromise);
    } catch (Throwable t) {
      startPromise.fail(t);
    }
  }

}
