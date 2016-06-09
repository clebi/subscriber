// Copyright 2016 Clément Bizeau
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

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
  private ZonedDateTime dateSubscriber;
  private Map<String, Object> fields = new HashMap<>();

  public Subscriber() {
    dateSubscriber = ZonedDateTime.now(ZoneOffset.UTC);
  }

  public boolean isValid() {
    return email != null && email.isValid();
  }
}
