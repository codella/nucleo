package dk.codella.demo;

import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.SqlResult;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import lombok.extern.flogger.Flogger;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.concurrent.CompletionStage;

@Flogger
@Path("/")
public class DemoResteasyResource {

  private final String appName;
  private final SqlClient sqlClient;

  @Inject
  public DemoResteasyResource(
      @ConfigProperty(name = "app.name") String appName,
      SqlClient sqlClient) {
    this.appName = appName;
    this.sqlClient = sqlClient;
  }

  @GET
  @Produces("text/plain")
  @Path("/resteasy")
  public String context() {
    log.atInfo().log("request received");
    return String.format("Hello from a RESTEasy resource in %s!", appName);
  }

  @GET
  @Produces("text/plain")
  @Path("/sql-client")
  public CompletionStage<String> sqlClient() {
    log.atInfo().log("request received");
    return sqlClient
        .query("SELECT 1")
        .execute()
        .map(SqlResult::size)
        .map(Object::toString)
        .toCompletionStage();
  }

}
