package org.clebi.subscribers.transformers;

import com.google.gson.Gson;

import org.clebi.subscribers.model.serialize.JsonFactory;
import spark.ResponseTransformer;

public class JsonResponseTransformer implements ResponseTransformer {

  private Gson gson = JsonFactory.getGson();

  @Override
  public String render(Object model) throws Exception {
    return gson.toJson(model);
  }
}
