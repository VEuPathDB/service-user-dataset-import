package org.veupathdb.service.userds.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StringInputStream")
class StringInputStreamTest {

  @Test
  @DisplayName("Returns nothing more or less than the given string")
  public void test1() throws IOException {
    var tgt = new StringInputStream("hello world");

    assertEquals("hello world", new String(tgt.readAllBytes()));
  }

}