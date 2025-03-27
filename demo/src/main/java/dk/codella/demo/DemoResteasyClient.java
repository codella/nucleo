package dk.codella.demo;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/")
@RegisterRestClient(baseUri = "https://www.google.com")
public interface DemoResteasyClient {
  @GET
  void get();
}
