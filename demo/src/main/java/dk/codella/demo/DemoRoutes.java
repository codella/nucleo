package dk.codella.demo;

import dk.codella.nucleo.HttpRoutesProvider;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class DemoRoutes implements HttpRoutesProvider {

  private final Router router;

  @Inject
  public DemoRoutes(Router router) {
    this.router = router;
  }

  public void run() {
    router.get("/some/path").respond(ctx ->
        Future.succeededFuture(new JsonObject().put("hello", "world"))
    );
  }

}
