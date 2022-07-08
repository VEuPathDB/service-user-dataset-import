package org.veupathdb.service.userds.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MultipartInputStream")
class MultipartInputStreamTest {

  @Test
  public void test1() throws IOException {
    var tgt = new MultipartInputStream(
      "gravy",
      new StringInputStream("and mashed potatoes")
    );

    var bound = "--" + tgt.getBoundary();

    assertEquals(
      bound
        + "\nContent-Disposition: form-data; name=\"file\"; filename=\"gravy\"\n"
        + "Content-Type: application/octet-stream\n\n"
        + "and mashed potatoes\n"
        + bound,
      new String(tgt.readAllBytes())
    );
  }

}