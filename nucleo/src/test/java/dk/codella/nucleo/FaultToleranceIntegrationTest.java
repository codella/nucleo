package dk.codella.nucleo;

import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ExtendWith(VertxExtension.class)
public class FaultToleranceIntegrationTest {

  @BeforeAll
  public static void beforeAll(VertxTestContext testContext) {
    new Nucleo()
            .withBeanClasses(X.class, Y.class)
            .start()
            .onComplete(testContext.succeedingThenComplete());
  }

  @Test
  public void test() throws InterruptedException {
    var x = CDI.current().select(X.class).get();
    assertThatExceptionOfType(TimeoutException.class).isThrownBy(x::y);
  }

  @ApplicationScoped
  public static class X {
    private final Y y;

    @Inject
    public X(Y y) {
      this.y = y;
    }

    public void y() throws InterruptedException {
      y.timeout();
    }
  }

  @ApplicationScoped
  public static class Y {

    @Timeout(value = 100, unit = ChronoUnit.MILLIS)
    public void timeout() throws InterruptedException {
Thread.sleep(20000);
    }
  }
}
