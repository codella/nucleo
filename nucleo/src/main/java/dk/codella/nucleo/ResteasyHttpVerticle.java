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
  private final Set<Object> resteasyResources;
  private final int port;

  @Inject
  public ResteasyHttpVerticle(
      @ResteasyResource Instance<Object> resteasyResources,
      @ConfigProperty(name = "nucleo.http-server.resteasy-verticle.port", defaultValue = "8080") int port
  ) {
    this.resteasyResources = Sets.newHashSet(resteasyResources);
    this.port = port;
  }

  @Override
  public void start(Promise<Void> startPromise) {
    try {
      HttpServer server = vertx.createHttpServer();

      // COMMENTARY:
      // This initializes and starts the core components of RESTEasy, in a way that is compatible with Vert.x
      VertxResteasyDeployment deployment = new VertxResteasyDeployment();
      deployment.start();

      // COMMENTARY:
      // This registers the RESTEasy resources in the VertxResteasyDeployment
      // TODO: find a way to use addPerInstanceResource() instead
      resteasyResources.forEach(resource -> deployment.getRegistry().addSingletonResource(resource));

      // COMMENTARY:
      // This adapts VertxResteasyDeployment to a Vert.x request handler
      server.requestHandler(new VertxRequestHandler(vertx, deployment));

      server
        .listen(port)
        .<Void>mapEmpty()
        .onComplete(startPromise);
    } catch (Exception t) {
      startPromise.fail(t);
    }
  }

}
