package dk.codella.demo;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.flogger.Flogger;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import java.time.LocalDateTime;

@ApplicationScoped
@Flogger
public class DemoMessageProcessor {

    @Outgoing("demo-messages")
    public String generateMessage() {
        String message = "Hello from Nucleo Reactive Messaging at " + LocalDateTime.now();
        log.atInfo().log("Generated message: %s", message);
        return message;
    }

    @Incoming("demo-messages")
    @Outgoing("processed-messages")
    public String processMessage(String message) {
        String processed = "PROCESSED: " + message.toUpperCase();
        log.atInfo().log("Processed message: %s", processed);
        return processed;
    }

    @Incoming("processed-messages")
    public void consumeMessage(String message) {
        log.atInfo().log("Consumed final message: %s", message);
    }
}