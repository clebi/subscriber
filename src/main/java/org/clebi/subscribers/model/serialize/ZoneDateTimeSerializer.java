package org.clebi.subscribers.model.serialize;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ZoneDateTimeSerializer implements JsonSerializer<ZonedDateTime> {
  @Override
  public JsonElement serialize(
      ZonedDateTime src,
      Type typeOfSrc,
      JsonSerializationContext context) {
    return new JsonPrimitive(src.format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
  }
}
