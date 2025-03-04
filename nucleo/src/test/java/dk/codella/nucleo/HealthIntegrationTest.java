package dk.codella.nucleo;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.health.SmallRyeHealthReporter;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import org.eclipse.microprofile.health.Readiness;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(VertxExtension.class)
public class HealthIntegrationTest {

  private static Nucleo nucleo;
  public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @BeforeAll
  public static void beforeAll(VertxTestContext testContext) {
    nucleo = new Nucleo();
    nucleo.withBeanClasses(MyCheck.class)
            .start()
            .onComplete(testContext.succeedingThenComplete());
  }

  @AfterAll
  public static void afterAll() {
    nucleo.shutdown();
  }

  @Test
  public void testHealth(Vertx vertx, VertxTestContext testContext) {
    HttpClient client = vertx.createHttpClient();

    var body = testContext.checkpoint();
    var status = testContext.checkpoint();

    client.request(HttpMethod.GET, 3030, "127.0.0.1", "/liveness")
            .compose(HttpClientRequest::send)
            .onComplete(testContext.succeeding(response -> testContext
                    .verify(() -> {
                      assertThat(response.statusCode()).isEqualTo(200);
                      status.flag();
                      response.body(testContext.succeeding(content -> testContext.verify(() -> {
                        var node = OBJECT_MAPPER.readTree(content.toString());
                        var checkName = node.get("payload").get("checks").get(0).get("name").get("string").asText();
                        assertThat(checkName).isEqualTo("successful-check");
                        body.flag();
                      })));
                    })));
  }
}

@ApplicationScoped
@Liveness
@Readiness
class MyCheck implements HealthCheck {

  public HealthCheckResponse call() {
    return HealthCheckResponse.up("successful-check");
  }
}
