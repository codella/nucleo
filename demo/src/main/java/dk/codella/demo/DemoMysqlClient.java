package dk.codella.demo;

import io.vertx.core.Vertx;
import io.vertx.mysqlclient.MySQLBuilder;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlClient;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

public class DemoMysqlClient {

  @Produces
  @Singleton
  // TODO: Add qualifier! USe if in the close method below!
  public SqlClient sqlClient(Vertx vertx, DemoMysqlClientConfig config) {
    MySQLConnectOptions connectOptions = new MySQLConnectOptions()
        .setPort(config.port())
        .setHost(config.host())
        .setDatabase(config.database())
        .setUser(config.user())
        .setPassword(config.password());

    PoolOptions poolOptions = new PoolOptions()
        .setMaxSize(5);

    return Pool.pool(vertx, connectOptions, poolOptions);
  }

  void closeSqlClient(@Disposes SqlClient sqlClient) {
    sqlClient.close();
  }
}
