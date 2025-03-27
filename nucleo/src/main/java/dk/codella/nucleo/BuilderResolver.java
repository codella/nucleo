package dk.codella.nucleo;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.spi.RestClientBuilderResolver;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;

public class BuilderResolver extends RestClientBuilderResolver {
    @Override
    public RestClientBuilder newBuilder() {
        return new ResteasyClientBuilderImpl();
    }
}