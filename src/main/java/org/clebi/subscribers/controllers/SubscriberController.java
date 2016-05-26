package org.clebi.subscribers.controllers;

import static spark.Spark.exception;
import static spark.Spark.post;

import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import org.clebi.subscribers.daos.SubscriberDao;
import org.clebi.subscribers.daos.exceptions.ValidationException;
import org.clebi.subscribers.model.ErrorResponse;
import org.clebi.subscribers.model.Subscriber;
import org.clebi.subscribers.modules.DaoModule;
import org.clebi.subscribers.transformers.JsonResponseTransformer;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubscriberController {

  private static final Logger logger = LoggerFactory.getLogger(SubscriberController.class);

  /**
   * Initialize subscriber controller.
   */
  @Inject
  public SubscriberController() {
    Injector injector = Guice.createInjector(new DaoModule());
    final SubscriberDao subscriberDao = injector.getInstance(SubscriberDao.class);
    final Gson gson = new Gson();

    post("/add-user/", ((request, response) -> {
      Subscriber subscriber = gson.fromJson(request.body(), Subscriber.class);
      subscriberDao.addSubscriber(subscriber);
      return subscriber;
    }), new JsonResponseTransformer());

    exception(ValidationException.class, (exception, request, response) -> {
      logger.warn(exception.getMessage(), exception);
      response.status(500);
      response.body(gson.toJson(new ErrorResponse("error", exception.getMessage())));
    });

    exception(Exception.class, (exception, request, response) -> {
      logger.error(exception.getMessage(), exception);
      response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
      response.body(gson.toJson(new ErrorResponse("error", "unknown error")));
    });
  }

}
