package org.clebi.subscribers.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class Subscriber {
  private boolean optin;
  private boolean active;
  private Email email;
  private ZonedDateTime dateSubscriber = ZonedDateTime.now(ZoneOffset.UTC);
  private Map<String, Object> fields = new HashMap<>();

  public boolean isValid() {
    return email != null && email.isValid();
  }
}
