package dk.codella.nucleo;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@EnableAutoWeld
@AddExtensions(ResteasyExtension.class)
@AddBeanClasses(ResteasyClient.class)
@WireMockTest
public class ResteasyExtensionIntegrationTest {

  @Inject
  private ResteasyClient client;

  @Test
  public void test() {
    stubFor(get("/things")
        .withHost(equalTo("test.domain"))
        .willReturn(ok("1")));

    client.get();
  }

}

@RegisterRestClient
interface ResteasyClient {
  @GET
  @Path("/things")
  String get();
}
