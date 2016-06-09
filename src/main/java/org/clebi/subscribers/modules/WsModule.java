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

package org.clebi.subscribers.modules;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import org.clebi.subscribers.services.ProjectService;
import org.clebi.subscribers.services.ProjecterService;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

public class WsModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(ProjectService.class).to(ProjecterService.class);
  }

  @Provides
  private Client jaxrsClient() {
    return ClientBuilder.newBuilder().newClient().register(JacksonJsonProvider.class);
  }
}
