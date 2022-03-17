package org.veupathdb.service.userds.util.http;

import java.util.Optional;
import jakarta.ws.rs.core.HttpHeaders;

public interface Header
{
  String
    CONTENT_DISPOSITION = "Content-Disposition",
    CONTENT_TYPE        = "Content-Type";

  static Optional < String > getBoundaryString(HttpHeaders headers) {
    return Optional.ofNullable(headers.getHeaderString(CONTENT_TYPE))
      .map(s -> s.split("boundary="))
      .filter(a -> a.length == 2)
      .map(a -> a[1])
      .map(s -> s.split(";")[0]);
  }
}
