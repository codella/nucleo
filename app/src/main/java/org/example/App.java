package org.example;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import org.jboss.resteasy.plugins.server.vertx.VertxResteasyDeployment;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

public class App {

    public static void main(String[] args) {
        WeldContainer container = new Weld()
            .disableDiscovery()
            .initialize();

//        container.select(Entry.class).get().perform();
        container.shutdown();

        VertxResteasyDeployment deployment = new VertxResteasyDeployment();
        deployment.getRegistry().addPerInstanceResource(Resource.class);
    }

    public static class Resource {
        @GET
        @Path("/somepath")
        @Produces("text/plain")
        public String context() {
            return "the-response";
        }
    }
}
