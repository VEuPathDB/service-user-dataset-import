package org.veupathdb.service.userds;

import java.io.FileReader;
import java.util.logging.Level;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.veupathdb.lib.container.jaxrs.config.Options;
import org.veupathdb.lib.container.jaxrs.health.Dependency;
import org.veupathdb.lib.container.jaxrs.server.ContainerResources;
import org.veupathdb.lib.container.jaxrs.server.Server;
import org.veupathdb.service.userds.controller.UserDatasetController;
import org.veupathdb.service.userds.model.config.ExtOptions;
import org.veupathdb.service.userds.model.config.HandlerConfig;
import org.veupathdb.service.userds.repo.SelectOriginQuery;
import org.veupathdb.service.userds.repo.SelectProjectQuery;
import org.veupathdb.service.userds.repo.SelectStatusQuery;
import org.veupathdb.service.userds.service.Irods;
import org.veupathdb.service.userds.service.meta.Migrator;
import org.veupathdb.service.userds.util.DatabaseDependency;
import org.veupathdb.service.userds.util.DbMan;
import org.veupathdb.service.userds.util.HandlerDependency;
import org.veupathdb.service.userds.util.Format;

public class Main extends Server {
  private static final Logger LOG = LogManager.getLogger(Main.class);

  public static final ExtOptions options = new ExtOptions();

  private static DataSource    importDB;
  public static  HandlerConfig jsonConfig;

  public static void main(String[] args) {

    java.util.logging.Logger.getLogger("org.glassfish.grizzly").setLevel(Level.ALL);
    java.util.logging.Logger.getLogger("com.sun.jersey").setLevel(Level.ALL);

    var server = new Main();

    server.enableAccountDB();
    server.start(args);
  }

  @Override
  protected void postCliParse(Options opts) {
    LOG.info("Initializing iRODS library");
    Irods.initialize(options);

    LOG.info("Initializing super user ID, if provided");
    UserDatasetController.initialize(options);

    LOG.info("Initializing import datastore connection");
    importDB = DbMan.initImportDb();

    try {
      Migrator.run();

      LOG.info("Populating type caches");
      SelectStatusQuery.run();
      SelectProjectQuery.run();
      SelectOriginQuery.run();

      LOG.info("Parsing service config");
      jsonConfig = Format.Json.readerFor(HandlerConfig.class)
        .readValue(new FileReader("config.json"));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected ContainerResources newResourceConfig(Options options) {
    return new Resources(options);
  }

  @Override
  protected Dependency[] dependencies() {
    final var deps = jsonConfig.getServices()
      .stream()
      .map(svc -> new HandlerDependency(svc.getDsType(), svc.getName(), 80))
      .map(Dependency.class::cast)
      .collect(Collectors.toList());

    deps.add(new DatabaseDependency("import-db", "postgres", (short) 5432,
      importDB));

    return deps.toArray(Dependency[]::new);
  }

  @Override
  protected Options newOptions() {
    return options;
  }
}
