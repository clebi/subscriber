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
