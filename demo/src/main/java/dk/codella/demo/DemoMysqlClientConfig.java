package dk.codella.demo;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "mysql")
public interface DemoMysqlClientConfig {

  int port();

  String host();

  String database();

  String user();

  String password();
}
