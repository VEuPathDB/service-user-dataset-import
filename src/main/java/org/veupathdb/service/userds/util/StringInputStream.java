package org.veupathdb.service.userds.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class StringInputStream extends InputStream {

  private final byte[] bytes;

  private int pos = 0;


  public StringInputStream(String string) {
    this.bytes = string.getBytes(StandardCharsets.UTF_8);
  }

  @Override
  public int read() throws IOException {
    if (pos >= bytes.length)
      return -1;
    else
      return bytes[pos++];
  }
}
