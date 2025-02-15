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
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(VertxExtension.class)
public class RoutesHttpVerticleIntegrationTest {

  @BeforeAll
  public static void test(VertxTestContext testContext) {
    new Nucleo()
        .withBeanClasses(Resource.class)
        .withRoutesHttpServer()
        .start()
        .onComplete(testContext.succeedingThenComplete());
  }

  @Test
  public void testResteasyResourceRespondsHttp200(Vertx vertx, VertxTestContext testContext) {
    HttpClient client = vertx.createHttpClient();

    client.request(HttpMethod.GET, 8081, "127.0.0.1", "/test")
        .compose(HttpClientRequest::send)
        .onComplete(testContext.succeeding(h -> {
          assertThat(h.statusCode()).isEqualTo(200);
          testContext.completeNow();
        }));
  }

  public static class Resource implements HttpRoutesProvider {

    private final Router router;

    @Inject
    public Resource(Router router) {
      this.router = router;
    }

    @Override
    public void run() {
      router.get("/test").respond(ctx ->
          Future.succeededFuture(new JsonObject().put("hello", "world"))
      );
    }
  }
}
