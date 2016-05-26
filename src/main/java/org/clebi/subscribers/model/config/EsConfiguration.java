package org.clebi.subscribers.model.config;

import lombok.Data;

@Data
public class EsConfiguration {
  private String host;
  private int port;
  private String clusterName;
}
