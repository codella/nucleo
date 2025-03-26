package dk.codella.nucleo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.client.Client;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.eclipse.microprofile.rest.client.spi.RestClientBuilderResolver;

import java.util.ArrayList;
import java.util.List;

public class ResteasyExtension implements Extension {
  private final List<Class<?>> notedRestEasyClients = new ArrayList<>(30);

  <T> void handleResteasyResources(
      @Observes @WithAnnotations({Path.class}) ProcessAnnotatedType<T> event
  ) {
    var isInterface = event.getAnnotatedType().getJavaClass().isInterface();
    // RestEasy resources can only be classes
    if (isInterface) {
      return;
    }

    // COMMENTARY:
    // Marks the RestEasy Resource with @ResteasyResource, so that they can be injected into the RestEasy verticle.
    event.configureAnnotatedType().add(ResteasyResource.Literal.INSTANCE);
    // TODO: we need to decide if we should only add a scope if not present, or something
    event.configureAnnotatedType().add(ApplicationScoped.Literal.INSTANCE);
  }

  <T> void noteResteasyClients(
      @Observes @WithAnnotations({RegisterRestClient.class}) ProcessAnnotatedType<T> event, BeanManager bm
  ) {
    var isInterface = event.getAnnotatedType().getJavaClass().isInterface();
    // Resteasy Clients can only be interfaces
    if (!isInterface) {
      return;
    }

    // COMMENTARY:
    // The Resteasy Client interfaces annotated with @RegisterRestClient will be handles when the AfterBeanDiscovery
    // event is fired
    notedRestEasyClients.add(event.getAnnotatedType().getJavaClass());
  }

  /*
   * WHY WE DON'T USE RESTEASY-CDI:
   *
   * https://github.com/quarkusio/quarkus/blob/ea2e80abd4ccfb75ca0d032668e1551ae5915c0b/extensions/resteasy-classic/resteasy-client/runtime/src/main/java/io/quarkus/restclient/runtime/QuarkusRestClientBuilder.java#L95-L98
   */
  // https://github.com/quarkusio/quarkus/blob/ea2e80abd4ccfb75ca0d032668e1551ae5915c0b/extensions/resteasy-classic/resteasy-client/runtime/src/main/java/io/quarkus/restclient/runtime/ClassicRestClientBuilderFactory.java
  // https://github.com/quarkusio/quarkus/commit/6c4a5b6accfe4334f38b607b0c898ef0ce2bf261
  public void registerClientProducer(@Observes AfterBeanDiscovery event, BeanManager bm) {
    if (!notedRestEasyClients.isEmpty()) {
      RestClientBuilderResolver.setInstance(new BuilderResolver());
    }

    notedRestEasyClients.forEach(nrc -> {
      event.addBean().addTransitiveTypeClosure(nrc)
          .scope(ApplicationScoped.class)
          .produceWith(instance -> {
            RestClientBuilder restClientBuilder = RestClientBuilder.newBuilder();
            return (Client) restClientBuilder.build(nrc);
          })
          .disposeWith((client, instance) -> {
            client.close();
          });
    });
  }
}
