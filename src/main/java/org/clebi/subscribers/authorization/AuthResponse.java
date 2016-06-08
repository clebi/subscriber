package org.clebi.subscribers.authorization;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AuthResponse {

  private boolean active;

  private String username;

  @JsonProperty("client_id")
  private String clientId;

  @JsonProperty("token_type")
  private String tokenType;

  private String exp;

}
