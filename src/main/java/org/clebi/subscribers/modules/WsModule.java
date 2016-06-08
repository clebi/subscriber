package org.clebi.subscribers.modules;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

public class WsModule extends AbstractModule {
  @Override
  protected void configure() {
  }

  @Provides
  private Client jaxrsClient() {
    return ClientBuilder.newBuilder().newClient().register(JacksonJsonProvider.class);
  }
}
