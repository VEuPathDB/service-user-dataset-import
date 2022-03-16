package org.veupathdb.service.userds.util;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.postgresql.Driver;
import org.veupathdb.service.userds.Main;

public class DbMan
{
  private static final String
    DEFAULT_HOST = "postgres",
    DEFAULT_USER = "postgres";
  private static final int DEFAULT_PORT = 5432;

  private static DataSource importDb;

  public static DataSource initImportDb() {
    final var config = new HikariConfig();
    config.setDriverClassName(Driver.class.getName());
    config.setJdbcUrl(String.format(
      "jdbc:postgresql://%s:%d/postgres",
      Main.options.getDsHost().orElse(DEFAULT_HOST),
      Main.options.getDsPort().orElse(DEFAULT_PORT)
    ));
    config.setUsername(Main.options.getDsUser().orElse(DEFAULT_USER));
    config.setPassword(Main.options.getDsPass().orElseThrow());
    return importDb = new HikariDataSource(config);
  }

  public static DataSource getImportDb() {
    return importDb;
  }
}
