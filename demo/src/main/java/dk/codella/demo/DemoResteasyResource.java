package dk.codella.demo;

import dk.codella.nucleo.ResteasyResource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import lombok.extern.flogger.Flogger;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Flogger
@Path("/resteasy")
@Singleton
public class DemoResteasyResource implements ResteasyResource {

  @Inject
  @ConfigProperty(name = "app.name")
  public String appName;

  @GET
  @Produces("text/plain")
  public String context() {
    log.atInfo().log("request received");
    return String.format("Hello from a RESTEasy resource in %s !", appName);
  }

}
