package org.veupathdb.service.userds.model.config;

import java.util.Optional;

import org.irods.jargon.core.connection.ClientServerNegotiationPolicy.SslNegotiationPolicy;
import org.veupathdb.lib.container.jaxrs.config.Options;
import picocli.CommandLine.Option;

public class ExtOptions extends Options
{
  @Option(
    names = "--irods-host",
    defaultValue = "${env:IRODS_HOST}",
    description = "env: IRODS_HOST",
    arity = "1")
  private String irodsHost;

  @Option(
    names = "--irods-port",
    defaultValue = "${env:IRODS_PORT}",
    description = "env: IRODS_PORT",
    arity = "1")
  private Integer irodsPort;

  @Option(
    names = "--irods-user",
    defaultValue = "${env:IRODS_USER}",
    description = "env: IRODS_USER",
    arity = "1")
  private String irodsUser;

  @Option(
    names = "--irods-zone",
    defaultValue = "${env:IRODS_ZONE}",
    description = "env: IRODS_ZONE",
    arity = "1")
  private String irodsZone;

  @Option(
    names = "--irods-pass",
    defaultValue = "${env:IRODS_PASS}",
    description = "env: IRODS_PASS",
    arity = "1")
  private String irodsPass;

  @Option(
    names = "--irods-resource",
    defaultValue = "${env:IRODS_RESOURCE}",
    description = "env: IRODS_RESOURCE",
    arity = "1")
  private String irodsResource;

  @Option(
    names = "--irods-ssl-mode",
    defaultValue = "${env:IRODS_SSL_MODE}",
    description = "env: IRODS_SSL_MODE",
    arity = "1")
  private SslNegotiationPolicy irodsSsl;

  @Option(
    names = "--datastore-host",
    defaultValue = "${env:DATASTORE_HOST}",
    description = "env: DATASTORE_HOST",
    arity = "1"
  )
  private String dsHost;

  @Option(
    names = "--datastore-port",
    defaultValue = "${env:DATASTORE_PORT}",
    description = "env: DATASTORE_PORT",
    arity = "1"
  )
  private Integer dsPort;

  @Option(
    names = "--datastore-user",
    defaultValue = "${env:DATASTORE_USER}",
    description = "env: DATASTORE_USER",
    arity = "1"
  )
  private String dsUser;

  @Option(
    names = "--datastore-pass",
    defaultValue = "${env:DATASTORE_PASS}",
    description = "env: DATASTORE_PASS",
    arity = "1"
  )
  private String dsPass;

  @Option(
      names = "--admin-user-id",
      defaultValue = "${env:ADMIN_USER_ID}",
      description = "env: ADMIN_USER_ID",
      arity = "1"
  )
  private Integer adminUserId;

  public Optional < String > getIrodsHost() {
    return Optional.ofNullable(irodsHost);
  }

  public Optional < Integer > getIrodsPort() {
    return Optional.ofNullable(irodsPort);
  }

  public Optional < String > getIrodsUser() {
    return Optional.ofNullable(irodsUser);
  }

  public Optional < String > getIrodsZone() {
    return Optional.ofNullable(irodsZone);
  }

  public Optional < String > getIrodsPass() {
    return Optional.ofNullable(irodsPass);
  }

  public Optional < String > getIrodsResource() {
    return Optional.ofNullable(irodsResource);
  }

  public Optional < SslNegotiationPolicy > getIrodsSsl() {
    return Optional.ofNullable(irodsSsl);
  }

  public Optional < String > getDsHost() {
    return Optional.ofNullable(dsHost);
  }

  public Optional < Integer > getDsPort() {
    return Optional.ofNullable(dsPort);
  }

  public Optional < String > getDsUser() {
    return Optional.ofNullable(dsUser);
  }

  public Optional < String > getDsPass() {
    return Optional.ofNullable(dsPass);
  }

  public Optional < Integer > getAdminUserId() {
    return Optional.ofNullable(adminUserId);
  }
}
