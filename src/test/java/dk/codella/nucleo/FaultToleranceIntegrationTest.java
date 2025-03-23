package dk.codella.nucleo;

import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ExtendWith(VertxExtension.class)
public class FaultToleranceIntegrationTest {

  private static Nucleo nucleo;

  @BeforeAll
  public static void beforeAll(VertxTestContext testContext) {
    nucleo = new Nucleo();
    nucleo
        .withWeld(weld -> weld.addBeanClasses(Caller.class, SlowComponent.class))
        .start()
        .onComplete(testContext.succeedingThenComplete());
  }

  @AfterAll
  public static void afterAll() {
    nucleo.shutdown();
  }

  // COMMENTARY:
  // This is the only test because it's enough to validate that SmallRye Fault Tolerance is integrated with Weld
  @Test
  public void testTimeout() {
    var caller = CDI.current().select(Caller.class).get();
    assertThatExceptionOfType(TimeoutException.class).isThrownBy(caller::callExpensiveComputation);
  }
}

@ApplicationScoped
class Caller {
  private final SlowComponent slowComponent;

  @Inject
  public Caller(SlowComponent slowComponent) {
    this.slowComponent = slowComponent;
  }

  public void callExpensiveComputation() throws InterruptedException {
    slowComponent.expensiveComputation();
  }
}

@ApplicationScoped
class SlowComponent {

  @Timeout(value = 10, unit = ChronoUnit.MILLIS)
  public void expensiveComputation() throws InterruptedException {
    Thread.sleep(50);
  }
}
