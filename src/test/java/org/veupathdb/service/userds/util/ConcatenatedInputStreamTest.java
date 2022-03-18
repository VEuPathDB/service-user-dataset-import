package org.veupathdb.service.userds.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ConcatenatedInputStream")
class ConcatenatedInputStreamTest {

  @Test
  @DisplayName("returns the concatenated value of all given input streams")
  public void test1() throws IOException {
    var tgt = new ConcatenatedInputStream(
      new StringInputStream("hello"),
      new StringInputStream(" "),
      new StringInputStream("world")
    );

    assertEquals("hello world", new String(tgt.readAllBytes()));
  }


}