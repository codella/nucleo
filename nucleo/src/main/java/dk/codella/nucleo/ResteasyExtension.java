package dk.codella.nucleo;

import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.ProcessAnnotatedType;
import jakarta.enterprise.inject.spi.WithAnnotations;
import jakarta.ws.rs.Path;

public class ResteasyExtension implements Extension {
  <T> void addResteasyResourceQualifierToResteasyResources(
      @Observes @WithAnnotations({Path.class}) ProcessAnnotatedType<T> pat
  ) {
    pat.configureAnnotatedType().add(ResteasyResource.Literal.INSTANCE);
  }
}
