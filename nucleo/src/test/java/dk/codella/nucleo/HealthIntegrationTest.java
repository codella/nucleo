package dk.codella.nucleo;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.health.SmallRyeHealth;
import io.smallrye.health.SmallRyeHealthReporter;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import org.eclipse.microprofile.health.Readiness;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ExtendWith(VertxExtension.class)
public class HealthIntegrationTest {

  public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @BeforeAll
  public static void beforeAll(VertxTestContext testContext) {
    new Nucleo()
            .withBeanClasses(HealthRoute.class, MyCheck.class)
            .withRoutesHttpServer()
            .start()
            .onComplete(testContext.succeedingThenComplete());
  }

  @Test
  public void testHealth(Vertx vertx, VertxTestContext testContext) {
    HttpClient client = vertx.createHttpClient();

    var body = testContext.checkpoint();
    var status = testContext.checkpoint();

    client.request(HttpMethod.GET, 8081, "127.0.0.1", "/health")
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

@ApplicationScoped
class HealthRoute implements HttpRoutesProvider {

  private final SmallRyeHealthReporter reporter;

  @Inject
  public HealthRoute(SmallRyeHealthReporter reporter) {
    this.reporter = reporter;
  }

  @Override
  public void accept(Router router) {
    router
            .get("/health")
            .respond(ctx -> {
              var xxx = reporter.getHealthAsync().subscribeAsCompletionStage();
              return Future.fromCompletionStage(xxx, Vertx.currentContext());
            });
  }
}
