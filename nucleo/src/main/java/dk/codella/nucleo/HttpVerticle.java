package dk.codella.nucleo;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class HttpVerticle extends AbstractVerticle {
  private final Router router;

  @Inject
  public HttpVerticle(Router router) {
    this.router = router;
  }

  @Override
  public void start(Promise<Void> startPromise) {
    HttpServer server = vertx.createHttpServer().requestHandler(router);

    // Now bind the server:
    server.listen(8080)
        .<Void>mapEmpty()
        .onComplete(startPromise);
  }

}
