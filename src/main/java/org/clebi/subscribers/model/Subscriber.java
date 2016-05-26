package org.clebi.subscribers.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Subscriber {
  private boolean optin;
  private String email;
  private Map<String, Object> fields = new HashMap<>();

  public boolean isValid() {
    return email != null;
  }
}
