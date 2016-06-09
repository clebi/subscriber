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

package org.clebi.subscribers.model.serialize;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.clebi.subscribers.model.Email;

import java.time.ZonedDateTime;

public class JsonFactory {

  /**
   * Get gson instance.
   *
   * @return gson instance
   */
  public static Gson getGson() {
    return new GsonBuilder()
        .registerTypeAdapter(Email.class, new EmailDeserializer())
        .registerTypeAdapter(Email.class, new EmailSerializer())
        .registerTypeAdapter(ZonedDateTime.class, new ZoneDateTimeDeserializer())
        .registerTypeAdapter(ZonedDateTime.class, new ZoneDateTimeSerializer())
        .create();
  }

}
