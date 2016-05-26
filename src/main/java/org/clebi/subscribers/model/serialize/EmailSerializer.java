package org.clebi.subscribers.model.serialize;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.clebi.subscribers.model.Email;

import java.lang.reflect.Type;

public class EmailSerializer implements JsonSerializer<Email> {
  @Override
  public JsonElement serialize(Email src, Type typeOfSrc, JsonSerializationContext context) {
    return new JsonPrimitive(src.getEmail());
  }
}
