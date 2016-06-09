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
      AuthResponse resp = wsClient.target(urlAuthServer)
          .request(MediaType.APPLICATION_JSON_TYPE)
          .header(AUTHORIZATION_HEADER, basicAuthHeader)
          .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), AuthResponse.class);
      if (!resp.isActive()) {
        halt(401, gson.toJson(new ErrorResponse("auth_error", "token not active")));
      }
    } catch (NotAuthorizedException exc) {
      halt(401, gson.toJson(new ErrorResponse("auth_error", "bad token")));
    }
  }
}
