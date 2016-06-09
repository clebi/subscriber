package org.clebi.subscribers.authorization;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class AuthResponse {

  private boolean active;

  private String username;

  @JsonProperty("client_id")
  private String clientId;

  @JsonProperty("token_type")
  private String tokenType;

  private String exp;

}
