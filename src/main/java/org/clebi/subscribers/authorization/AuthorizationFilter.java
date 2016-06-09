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

package org.clebi.subscribers.authorization;

import static spark.Spark.halt;

import com.google.gson.Gson;
import com.google.inject.Inject;

import org.clebi.subscribers.configuration.GlobalConfig;
import org.clebi.subscribers.model.ErrorResponse;
import org.clebi.subscribers.model.serialize.JsonFactory;
import org.clebi.subscribers.modules.exceptions.ConfigurationException;
import org.clebi.subscribers.modules.providers.ConfigCheckedProvider;
import spark.Filter;
import spark.Request;
import spark.Response;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;

public class AuthorizationFilter implements Filter {

  public static final String AUTHORIZATION_HEADER = "Authorization";

  final Gson gson = JsonFactory.getGson();
  final Client wsClient;

  private final String urlAuthServer;
  private final String auth;

  /**
   * Create the oauth authorization filter.
   *
   * @param wsClient the client used for http request
   * @param config   the global config
   * @throws ConfigurationException an error happened during configuration loading
   */
  @Inject
  public AuthorizationFilter(Client wsClient, ConfigCheckedProvider<GlobalConfig> config)
      throws ConfigurationException {
    GlobalConfig current = config.get();
    this.urlAuthServer = current.getAuthServer().getUrl();
    this.auth = current.getAuthServer().getUsername() + ":" + current.getAuthServer().getPassword();
    this.wsClient = wsClient;
  }

  @Override
  public void handle(Request request, Response response) throws Exception {
    String authHeader = request.headers(AUTHORIZATION_HEADER);
    if (authHeader == null) {
      halt(401, gson.toJson(new ErrorResponse("auth_error", "missing token")));
    }
    String token = authHeader.replace("Bearer ", "");
    String basicAuthHeader = "Basic " + java.util.Base64.getEncoder().encodeToString(auth.getBytes());
    Form form = new Form();
    form.param("token", token);
    try {
      AuthResponse authResponse = wsClient.target(urlAuthServer)
          .request(MediaType.APPLICATION_JSON_TYPE)
          .header(AUTHORIZATION_HEADER, basicAuthHeader)
          .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), AuthResponse.class);
      request.attribute("authorization", authResponse);
      if (!authResponse.isActive()) {
        halt(401, gson.toJson(new ErrorResponse("auth_error", "token not active")));
      }
    } catch (NotAuthorizedException exc) {
      halt(401, gson.toJson(new ErrorResponse("auth_error", "bad token")));
    }
  }
}
