package dk.codella.demo;

import dk.codella.nucleo.ResteasyResource;
import jakarta.inject.Singleton;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import lombok.extern.flogger.Flogger;

@Flogger
@Singleton
@Path("/resteasy")
public class DemoResteasyResource implements ResteasyResource {

//  @Inject
//  @ConfigProperty(name = "app.name")

  @GET
  @Produces("text/plain")
  public String context() {
    log.atInfo().log("request received");
    return "the-response";
  }

}
