package org.clebi.subscribers.configuration;

import lombok.Data;

@Data
public class YamlGlobalConfig implements GlobalConfig {

  private AuthServer authServer;

  public AuthServer getAuthServer() {
    return this.authServer;
  }
}
