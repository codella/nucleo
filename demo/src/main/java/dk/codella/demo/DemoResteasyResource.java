package dk.codella.demo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import lombok.extern.flogger.Flogger;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
@Flogger
@Path("/resteasy")
public class DemoResteasyResource {

  private final String appName;

  @Inject
  public DemoResteasyResource(@ConfigProperty(name = "app.name") String appName) {
    this.appName = appName;
  }

  @GET
  @Produces("text/plain")
  public String context() {
    log.atInfo().log("request received");
    return String.format("Hello from a RESTEasy resource in %s!", appName);
  }

}
