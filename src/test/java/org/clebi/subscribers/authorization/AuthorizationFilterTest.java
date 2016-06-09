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

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;

import org.apache.cxf.common.i18n.UncheckedException;
import org.clebi.subscribers.configuration.AuthServer;
import org.clebi.subscribers.configuration.GlobalConfig;
import org.clebi.subscribers.configuration.Projecter;
import org.clebi.subscribers.configuration.YamlGlobalConfig;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import spark.HaltException;
import spark.Request;
import spark.Response;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

public class AuthorizationFilterTest {

  private static String URL_WS_TEST = "http://test.com/token";
  private static String WS_USERNAME = "username";
  private static String WS_PASSORD = "password";

  private static final String TEST_BEARER = "Bearer test_token";
  private static final String TEST_BAD_BEARER = "Bearer test_bad_token";

  private static final boolean AUTH_VALID = true;
  private static final boolean AUTH_INVALID = false;
  private static final String AUTH_USERNAME = "auth_username";
  private static final String AUTH_CLIENT_ID = "auth_client_id";
  private static final String AUTH_TOKEN_TYPE = "token_type";
  private static final String AUTH_TOKEN_EXPIRES = "2016/12/01T12:45:36";

  private static final String HALT_UNAUTHORIZED_STATUS_CODE_FIELD = "statusCode";
  private static final int HALT_UNAUTHORIZED_STATUS_CODE_VALUE = 401;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @SuppressWarnings("unchecked")
  private Invocation.Builder getAuthorizedResponseBuilder(AuthResponse resp) {
    Invocation.Builder stubBuilder = Mockito.mock(Invocation.Builder.class);
    Mockito.when(stubBuilder.post(Mockito.any(Entity.class), Mockito.any(Class.class)))
        .thenReturn(resp);
    return stubBuilder;
  }

  @SuppressWarnings("unchecked")
  private Invocation.Builder getUnauthorizedResponseBuilder() {
    Invocation.Builder stubBuilder = Mockito.mock(Invocation.Builder.class);
    Mockito.when(stubBuilder.post(Mockito.any(Entity.class), Mockito.any(Class.class)))
        .thenThrow(new NotAuthorizedException("token is invalid"));
    return stubBuilder;
  }

  private GlobalConfig getGlobalConfig() {
    return new YamlGlobalConfig(new AuthServer(URL_WS_TEST, WS_USERNAME, WS_PASSORD), null);
  }

  private Client getClientStub(Invocation.Builder stubBuilder, String url) {
    Client stubClient = Mockito.mock(Client.class);
    WebTarget stubTarget = Mockito.mock(WebTarget.class);
    Mockito.when(stubTarget.request(MediaType.APPLICATION_JSON_TYPE)).thenReturn(stubBuilder);
    Mockito.when(stubBuilder.header(Mockito.any(), Mockito.any())).thenReturn(stubBuilder);
    Mockito.when(stubClient.target(url)).thenReturn(stubTarget);
    return stubClient;
  }

  @Test
  public void testWithMissingToken() throws Exception {
    expectedException.expect(HaltException.class);
    expectedException.expect(hasProperty(HALT_UNAUTHORIZED_STATUS_CODE_FIELD, is(HALT_UNAUTHORIZED_STATUS_CODE_VALUE)));
    Request stubRequest = Mockito.mock(Request.class);
    Response stubResponse = Mockito.mock(Response.class);
    Mockito.when(stubRequest.headers(AuthorizationFilter.AUTHORIZATION_HEADER)).thenReturn(null);

    AuthorizationFilter filter = new AuthorizationFilter(
        null,
        this::getGlobalConfig);
    filter.handle(stubRequest, stubResponse);
  }

  @Test
  public void testWithBadToken() throws Exception {
    expectedException.expect(HaltException.class);
    expectedException.expect(hasProperty(HALT_UNAUTHORIZED_STATUS_CODE_FIELD, is(HALT_UNAUTHORIZED_STATUS_CODE_VALUE)));
    Request stubRequest = Mockito.mock(Request.class);
    Response stubResponse = Mockito.mock(Response.class);
    Mockito.when(stubRequest.headers(AuthorizationFilter.AUTHORIZATION_HEADER)).thenReturn(TEST_BAD_BEARER);

    AuthorizationFilter filter = new AuthorizationFilter(
        getClientStub(
            getUnauthorizedResponseBuilder(),
            URL_WS_TEST
        ),
        this::getGlobalConfig);
    filter.handle(stubRequest, stubResponse);
  }

  @Test
  public void testWithNonValidToken() throws Exception {
    expectedException.expect(HaltException.class);
    expectedException.expect(hasProperty(HALT_UNAUTHORIZED_STATUS_CODE_FIELD, is(HALT_UNAUTHORIZED_STATUS_CODE_VALUE)));
    Request stubRequest = Mockito.mock(Request.class);
    Response stubResponse = Mockito.mock(Response.class);
    Mockito.when(stubRequest.headers(AuthorizationFilter.AUTHORIZATION_HEADER)).thenReturn(TEST_BEARER);

    AuthorizationFilter filter = new AuthorizationFilter(
        getClientStub(
            getAuthorizedResponseBuilder(
                new AuthResponse(
                    AUTH_INVALID,
                    AUTH_USERNAME,
                    AUTH_CLIENT_ID,
                    AUTH_TOKEN_TYPE,
                    AUTH_TOKEN_EXPIRES
                )
            ),
            URL_WS_TEST
        ),
        this::getGlobalConfig);
    filter.handle(stubRequest, stubResponse);
  }

  @Test
  public void testWithValidToken() throws Exception {
    Request stubRequest = Mockito.mock(Request.class);
    Response stubResponse = Mockito.mock(Response.class);
    Mockito.when(stubRequest.headers(AuthorizationFilter.AUTHORIZATION_HEADER)).thenReturn(TEST_BEARER);

    AuthorizationFilter filter = new AuthorizationFilter(
        getClientStub(
            getAuthorizedResponseBuilder(
                new AuthResponse(
                    AUTH_VALID,
                    AUTH_USERNAME,
                    AUTH_CLIENT_ID,
                    AUTH_TOKEN_TYPE,
                    AUTH_TOKEN_EXPIRES
                )
            ),
            URL_WS_TEST
        ),
        this::getGlobalConfig);
    filter.handle(stubRequest, stubResponse);
  }

}
