package dk.codella.nucleo;

import io.vertx.core.*;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import jakarta.enterprise.context.ApplicationScoped;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(VertxExtension.class)
public class HttpRoutesIntegrationTest {

  private static Nucleo nucleo;

  @BeforeAll
  public static void beforeAll(Vertx vertx, VertxTestContext testContext) {
    nucleo = new Nucleo();
    nucleo
        .withVerticleBeanClass(HttpRoutesVerticle.class, new DeploymentOptions())
        .start()
        .onComplete(testContext.succeedingThenComplete());
  }

  @AfterAll
  public static void afterAll() {
    nucleo.shutdown();
  }

  @Test
  public void testResteasyResourceRespondsHttp200(Vertx vertx, VertxTestContext testContext) {
    HttpClient client = vertx.createHttpClient();

    client.request(HttpMethod.GET, 8080, "127.0.0.1", "/test")
        .compose(HttpClientRequest::send)
        .onComplete(testContext.succeeding(h -> {
          assertThat(h.statusCode()).isEqualTo(200);
          testContext.completeNow();
        }));
  }

  @ApplicationScoped
  public static class HttpRoutesVerticle extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) {
      try {
        Router router = Router.router(vertx);

        router
            .get("/test")
            .respond(ctx -> Future.succeededFuture("OK"));

        HttpServer server = vertx.createHttpServer();
        server.requestHandler(router);

        server
            .listen(8080)
            .<Void>mapEmpty()
            .onComplete(startPromise);
      } catch (Exception t) {
        startPromise.fail(t);
      }
    }
  }
}
