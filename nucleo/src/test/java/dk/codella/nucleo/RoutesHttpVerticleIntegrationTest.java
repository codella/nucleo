package dk.codella.nucleo;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(VertxExtension.class)
public class RoutesHttpVerticleIntegrationTest {

  private static Nucleo nucleo;

  @BeforeAll
  public static void beforeAll(VertxTestContext testContext) {
    nucleo = new Nucleo();
    nucleo
        .withBeanClasses(Resource.class)
        .withRoutesHttpServer()
        .start()
        .onComplete(testContext.succeedingThenComplete());
  }

  @AfterAll
  public static void afterAll() {
    nucleo.shutdown();
  }

  @Test
  public void testRouteRespondsHttp200(Vertx vertx, VertxTestContext testContext) {
    HttpClient client = vertx.createHttpClient();

    client.request(HttpMethod.GET, 8081, "127.0.0.1", "/test")
        .compose(HttpClientRequest::send)
        .onComplete(testContext.succeeding(h -> {
          assertThat(h.statusCode()).isEqualTo(200);
          testContext.completeNow();
        }));
  }

  public static class Resource implements HttpRoutesProvider {

    @Override
    public void accept(Router router) {
      router.get("/test").respond(ctx ->
          Future.succeededFuture(new JsonObject().put("hello", "world"))
      );
    }
  }
}
