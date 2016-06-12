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

package org.clebi.subscribers.controllers;

import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.post;

import com.google.gson.Gson;
import com.google.inject.Inject;

import org.clebi.subscribers.daos.SubscriberDao;
import org.clebi.subscribers.daos.exceptions.DaoException;
import org.clebi.subscribers.model.ErrorResponse;
import org.clebi.subscribers.model.Project;
import org.clebi.subscribers.model.SearchFilter;
import org.clebi.subscribers.model.SearchRequest;
import org.clebi.subscribers.model.Subscriber;
import org.clebi.subscribers.model.serialize.JsonFactory;
import org.clebi.subscribers.modules.annotations.OauthFilter;
import org.clebi.subscribers.services.ISubscriberService;
import org.clebi.subscribers.services.ProjectService;
import org.clebi.subscribers.services.SubscriberService;
import org.clebi.subscribers.services.exceptions.ServiceException;
import org.clebi.subscribers.transformers.JsonResponseTransformer;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Filter;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SubscriberController {

  private static final Logger logger = LoggerFactory.getLogger(SubscriberController.class);

  private SubscriberDao subscriberDao;
  private ProjectService projectService;

  /**
   * Initialize subscriber controller.
   */
  @Inject
  public SubscriberController(
      SubscriberDao subscriberDao,
      @OauthFilter Filter filter,
      ProjectService projectService,
      ISubscriberService subscriberService) {
    this.subscriberDao = subscriberDao;
    this.projectService = projectService;
    final Gson gson = JsonFactory.getGson();

    before(filter);
    before("/:project/*", ((request, response) -> {
      String project = request.params(":project");
      boolean isMember = projectService.isMember(project, request.headers("Authorization"));
      if (!isMember) {
        halt(
            HttpStatus.FORBIDDEN_403,
            gson.toJson(new ErrorResponse("auth_error", "you are not a member of project " + project))
        );
      }
    }));

    get("/:project/:userEmail", (request, response) -> {
      String project = request.params(":project");
      Subscriber subscriber = subscriberDao.getSubscriber(project, request.params(":userEmail"));
      return subscriber;
    }, new JsonResponseTransformer());

    post("/:project/add/", ((request, response) -> {
      String projectName = request.params(":project");
      Subscriber subscriber = gson.fromJson(request.body(), Subscriber.class);
      subscriberService.addSubscriber(projectName, subscriber, request.headers("Authorization"));
      return subscriber;
    }), new JsonResponseTransformer());

    get("/:project/list/", ((request, response) -> {
      String project = request.params(":project");
      int size = Integer.parseInt(request.queryParams("size"));
      int offset = Integer.parseInt(request.queryParams("offset"));
      return subscriberDao.search(project, size, offset, new LinkedList<>());
    }), new JsonResponseTransformer());

    post("/:project/search/", (request, response) -> {
      String project = request.params(":project");
      SearchRequest searchRequest = gson.fromJson(request.body(), SearchRequest.class);
      System.out.println(searchRequest);
      List<SearchFilter> filters = searchRequest.getPrimaryFilters();
      return subscriberDao.search(project, searchRequest.getSize(), searchRequest.getOffset(), filters);
    }, new JsonResponseTransformer());

    exception(ServiceException.class, ((exception, request, response) -> {
      response.status(HttpStatus.BAD_REQUEST_400);
      response.body(gson.toJson(new ErrorResponse("error", exception.getMessage())));
    }));

    exception(NumberFormatException.class, (exception, request, response) -> {
      response.status(HttpStatus.BAD_REQUEST_400);
      response.body(gson.toJson(new ErrorResponse("error", "bad parameters")));
    });

    exception(DaoException.class, (exception, request, response) -> {
      logger.warn(exception.getMessage(), exception);
      response.status(HttpStatus.BAD_REQUEST_400);
      response.body(gson.toJson(new ErrorResponse("error", exception.getMessage())));
    });

    exception(Exception.class, (exception, request, response) -> {
      logger.error(exception.getMessage(), exception);
      response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
      response.body(gson.toJson(new ErrorResponse("error", "unknown error")));
    });

    after(((request, response) -> {
      response.type("application/json");
    }));
  }

}
