package dk.codella.nucleo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.ProcessAnnotatedType;
import jakarta.enterprise.inject.spi.WithAnnotations;
import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.lang.annotation.Annotation;

public class ResteasyExtension implements Extension {

//  private static final Annotation registerRestClientLiteral = new AnnotationLiteral<RegisterRestClient>() {
//    private static final long serialVersionUID = 3737373728872347324L;
//  };

  <T> void addResteasyResourceQualifierToResteasyResources(
      @Observes @WithAnnotations({Path.class}) ProcessAnnotatedType<T> event
  ) {
    if (event.getAnnotatedType().getJavaClass().isInterface()) {
      if (event.getAnnotatedType().isAnnotationPresent(RegisterRestClient.class)) {
        System.out.println("ffff");
      }
    }
    else {
      event.configureAnnotatedType().add(ResteasyResource.Literal.INSTANCE);
      event.configureAnnotatedType().add(ApplicationScoped.Literal.INSTANCE);
    }
  }
}
