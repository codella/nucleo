package dk.codella.nucleo;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.spi.RestClientBuilderResolver;

/**
 * Placeholder RestClientBuilder resolver.
 * TODO: Implement proper MicroProfile REST Client integration once dependencies are resolved.
 */
public class BuilderResolver extends RestClientBuilderResolver {
    @Override
    public RestClientBuilder newBuilder() {
        throw new UnsupportedOperationException(
            "RestClientBuilder is not yet implemented. " +
            "This is a placeholder to avoid compilation errors. " +
            "Full REST Client support requires proper MicroProfile REST Client dependencies."
        );
    }
}