package dk.codella.demo;

import dk.codella.nucleo.ResteasyResource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/resteasy")
public class DemoResteasyResource implements ResteasyResource {

  @GET
  @Produces("text/plain")
  public String context() {
    return "the-response";
  }

}
