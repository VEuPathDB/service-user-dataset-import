package org.veupathdb.service.userds.service.metrics;

import io.prometheus.client.Counter;

public class ImportMetrics {

  private static final Counter COUNT_BY_TYPE = Counter.build()
      .name("dataset_upload")
      .help("Total successfully uploaded datasets.")
      .labelNames("type")
      .register();

  public static void emitSuccessfulUploadByType(int count, String type) {
    COUNT_BY_TYPE.labels(type).inc(count);
  }
}
