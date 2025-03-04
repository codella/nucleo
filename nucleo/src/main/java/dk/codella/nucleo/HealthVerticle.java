package dk.codella.nucleo;

import io.smallrye.health.SmallRyeHealthReporter;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

public class HealthVerticle extends AbstractVerticle {

  private final SmallRyeHealthReporter reporter;
  private final int port;

  @Inject
  public HealthVerticle(
      SmallRyeHealthReporter reporter,
      @ConfigProperty(name = "nucleo.health-server.port", defaultValue = "3030") int port
  ) {
    this.reporter = reporter;
    this.port = port;
  }

  @Override
  public void start(Promise<Void> startPromise) {
    try {
      Router router = Router.router(vertx);

      router
          .get("/liveness")
          .respond(ctx -> {
            var completableFuture = reporter.getLivenessAsync().subscribeAsCompletionStage();
            return Future.fromCompletionStage(completableFuture, Vertx.currentContext());
          });

      router
          .get("/readiness")
          .respond(ctx -> {
            var completableFuture = reporter.getReadinessAsync().subscribeAsCompletionStage();
            return Future.fromCompletionStage(completableFuture, Vertx.currentContext());
          });

      HttpServer server = vertx.createHttpServer();
      server.requestHandler(router);

      server
        .listen(port)
        .<Void>mapEmpty()
        .onComplete(startPromise);
    } catch (Exception t) {
      startPromise.fail(t);
    }
  }

}
