package org.veupathdb.service.userds.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class MultipartInputStream extends InputStream {

  private static final String contentType = "Content-Type: application/octet-stream\n";

  private final InputStream concat;

  private final String boundary;

  public MultipartInputStream(String fileName, InputStream rawInput) {
    boundary = makeBoundary();

    concat = new ConcatenatedInputStream(
      new StringInputStream("--" + boundary + "\n"),
      new StringInputStream(makeDisposition(fileName)),
      new StringInputStream(contentType + "\n"),
      rawInput,
      new StringInputStream("\n--" + boundary)
    );
  }

  public String getBoundary() {
    return boundary;
  }

  @Override
  public int read() throws IOException {
    return concat.read();
  }

  private static String makeBoundary() {
    return String.format("----%s", UUID.randomUUID());
  }

  private static String makeDisposition(String fileName) {
    return String.format(
      "Content-Disposition: form-data; name=\"file\"; filename=\"%s\"\n",
      fileName
    );
  }

}
