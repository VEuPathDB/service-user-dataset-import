package org.veupathdb.service.userds;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.MultiPartMediaTypes;
import org.veupathdb.lib.container.jaxrs.config.Options;
import org.veupathdb.lib.container.jaxrs.server.ContainerResources;
import org.veupathdb.service.userds.controller.DebugController;
import org.veupathdb.service.userds.controller.ProjectController;
import org.veupathdb.service.userds.controller.UserDatasetController;

/**
 * Service Resource Registration.
 *
 * This is where all the individual service specific resources and middleware
 * should be registered.
 */
public class Resources extends ContainerResources {

  public Resources(Options opts) {
    super(opts);
    enableCors();
    enableAuth();
    enableJerseyTrace();
  }

  /**
   * Returns an array of JaxRS endpoints, providers, and contexts.
   *
   * Entries in the array can be either classes or instances.
   */
  @Override
  protected Object[] resources() {
    return new Object[] {
      ProjectController.class,
      UserDatasetController.class,
      DebugController.class,
      MultiPartFeature.class,
    };
  }
}
