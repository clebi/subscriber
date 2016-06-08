package org.clebi.subscribers.controllers;

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
import org.clebi.subscribers.model.SearchFilter;
import org.clebi.subscribers.model.SearchRequest;
import org.clebi.subscribers.model.Subscriber;
import org.clebi.subscribers.model.serialize.JsonFactory;
import org.clebi.subscribers.modules.annotations.OauthFilter;
import org.clebi.subscribers.transformers.JsonResponseTransformer;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Filter;

import java.util.LinkedList;
import java.util.List;

public class SubscriberController {

  private static final Logger logger = LoggerFactory.getLogger(SubscriberController.class);

  private SubscriberDao subscriberDao;

  /**
   * Initialize subscriber controller.
   */
  @Inject
  public SubscriberController(SubscriberDao subscriberDao, @OauthFilter Filter filter) {
    this.subscriberDao = subscriberDao;
    final Gson gson = JsonFactory.getGson();

    before(filter);

    get("/user/:userEmail", (request, response) -> {
      Subscriber subscriber = subscriberDao.getSubscriber(request.params(":userEmail"));
      return subscriber;
    }, new JsonResponseTransformer());

    post("/user/add/", ((request, response) -> {
      Subscriber subscriber = gson.fromJson(request.body(), Subscriber.class);
      subscriberDao.addSubscriber(subscriber);
      return subscriber;
    }), new JsonResponseTransformer());

    get("/user/list/", ((request, response) -> {
      int size = Integer.parseInt(request.queryParams("size"));
      int offset = Integer.parseInt(request.queryParams("offset"));
      return subscriberDao.search(size, offset, new LinkedList<>());
    }));

    post("/user/search/", (request, response) -> {
      SearchRequest searchRequest = gson.fromJson(request.body(), SearchRequest.class);
      System.out.println(searchRequest);
      List<SearchFilter> filters = searchRequest.getPrimaryFilters();
      return subscriberDao.search(searchRequest.getSize(), searchRequest.getOffset(), filters);
    });

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
  }

}
