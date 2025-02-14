package dk.codella.nucleo;

import com.google.common.collect.Sets;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import org.jboss.resteasy.plugins.server.vertx.VertxRequestHandler;
import org.jboss.resteasy.plugins.server.vertx.VertxResteasyDeployment;

import java.util.Set;

// We need @Singleton to make sure exceptions are going to be visible on bean creation
@Singleton
public class ResteasyHttpVerticle extends AbstractVerticle {
  private final Set<ResteasyResource> resteasyResources;

  @Inject
  public ResteasyHttpVerticle(
      Instance<ResteasyResource> resteasyResources
  ) {
    this.resteasyResources = Sets.newHashSet(resteasyResources);
  }

  @Override
  public void start() { //Promise<Void> startPromise
    throw new RuntimeException("Arca mignotta");
//startPromise.fail("s'e' rotto.");
    //    try {
//      HttpServer server = vertx.createHttpServer();
//
//      VertxResteasyDeployment deployment = new VertxResteasyDeployment();
//      deployment.start();
//
//      for (var resource : resteasyResources) {
//        // TODO: check why exceptions thrown by (or because of) resteasy resources seem to be swallowed
//        deployment.getRegistry().addPerInstanceResource(TestResource.class);
//      }
//
//      server.requestHandler(new VertxRequestHandler(vertx, deployment));
//
//      server
//          .listen(8080)
//          .<Void>mapEmpty()
//          .onComplete(startPromise);
//    } catch (Throwable t) {
////      t.printStackTrace();
//      startPromise.fail(t);
//    }
  }

  //@Path("/test")
  public static class TestResource {

    @GET
    @Produces("text/plain")
    public String context() {
      return "test-resource";
    }

  }

}
