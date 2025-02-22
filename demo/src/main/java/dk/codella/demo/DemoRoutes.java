package dk.codella.demo;

import dk.codella.nucleo.HttpRoutesProvider;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DemoRoutes implements HttpRoutesProvider {

  @Override
  public void accept(Router router) {
    router.get("/some/path").respond(ctx ->
        Future.succeededFuture(new JsonObject().put("hello", "world"))
    );
  }
}
