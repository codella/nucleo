package dk.codella.nucleo;

import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.AfterDeploymentValidation;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.Extension;
import lombok.extern.flogger.Flogger;

/**
 * Minimal CDI extension that enables SmallRye Reactive Messaging integration with Nucleo.
 * 
 * This extension does minimal work since SmallRye Reactive Messaging provides its own
 * CDI extension (io.smallrye.reactive.messaging.providers.extension.ReactiveMessagingExtension)
 * that handles all the heavy lifting. We simply log when reactive messaging is available.
 */
@Flogger
public class ReactiveMessagingExtension implements Extension {

    void afterDeploymentValidation(@Observes AfterDeploymentValidation event, BeanManager beanManager) {
        log.atInfo().log("NUCLEO-200: SmallRye Reactive Messaging integration is active");
        
        // COMMENTARY:
        // SmallRye Reactive Messaging provides its own CDI extension that automatically:
        // 1. Discovers classes with @Incoming/@Outgoing methods  
        // 2. Registers producer methods for @Channel injections
        // 3. Manages connectors and message processing
        // 4. Handles initialization of the messaging system
        //
        // Our extension just confirms the integration is working. The real work
        // is done by io.smallrye.reactive.messaging.providers.extension.ReactiveMessagingExtension
        // which is automatically discovered via META-INF/services.
    }
}