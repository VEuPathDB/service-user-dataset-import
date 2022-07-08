package org.veupathdb.service.userds.util;

import jakarta.ws.rs.core.Request;

import org.veupathdb.lib.container.jaxrs.providers.RequestIdProvider;
import org.veupathdb.service.userds.generated.model.NotFoundError;
import org.veupathdb.service.userds.generated.model.NotFoundErrorImpl;
import org.veupathdb.service.userds.generated.model.ServerError;
import org.veupathdb.service.userds.generated.model.ServerErrorImpl;

public class ErrFac
{
  public static NotFoundError new404() {
    var out = new NotFoundErrorImpl();
    out.setMessage("requested resource could not be found");
    return out;
  }

  public static ServerError new500(Request req, Throwable t) {
    var out = new ServerErrorImpl();
    out.setMessage(t.getMessage());
    out.setRequestId(RequestIdProvider.getRequestId(req));
    return out;
  }
}
