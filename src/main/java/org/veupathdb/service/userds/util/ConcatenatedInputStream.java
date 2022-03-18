package org.veupathdb.service.userds.util;

import java.io.IOException;
import java.io.InputStream;

public class ConcatenatedInputStream extends InputStream implements AutoCloseable {

  private final InputStream[] streams;

  private int currentStream = 0;

  private boolean closeOnConsume = false;

  public ConcatenatedInputStream(InputStream... streams) {
    if (streams.length == 0)
      throw new IllegalStateException("Must provide at least one stream.");

    this.streams = streams;
  }

  public void closeOnConsume(boolean closeOnConsume) {
    this.closeOnConsume = closeOnConsume;
  }

  @Override
  public int read() throws IOException {
    if (currentStream >= streams.length)
      return -1;

    final var out = streams[currentStream].read();

    if (out < 0 && nextStream())
      return read();

    return out;
  }

  @Override
  public void close() throws IOException {
    for (var s : streams)
      s.close();
  }

  private boolean nextStream() throws IOException {
    if (closeOnConsume)
      streams[currentStream].close();

    return ++currentStream < streams.length;
  }
}