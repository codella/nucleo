package dk.codella.nucleo;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HttpVerticle extends AbstractVerticle {
  private HttpServer server;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    server = vertx.createHttpServer().requestHandler(req -> {
      req.response()
          .putHeader("content-type", "text/plain")
          .end("Hello from Vert.x!");
    });

    // Now bind the server:
    server.listen(8080)
        .<Void>mapEmpty()
        .onComplete(startPromise);
  }

}
