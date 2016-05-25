package org.clebi.subscribers.controllers;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.*;

public class SubscriberController {

    private static final Logger logger = LoggerFactory.getLogger(SubscriberController.class);

    @Inject
    public SubscriberController() {
        Injector injector = Guice.createInjector(new DaoModule());
        final SubscriberDao subscriberDao = injector.getInstance(SubscriberDao.class);
        final Gson gson = new Gson();

        post("/add-user/", ((request, response) -> {
            Subscriber subscriber = gson.fromJson(request.body(), Subscriber.class);
            System.out.println(subscriber);
            subscriberDao.addSubscriber(subscriber);
            return subscriber;
        }), new JsonResponseTransformer());

        exception(ValidationException.class, (exception, request, response) -> {
            logger.warn(exception.getMessage(), exception);
            response.status(500);
            response.body(gson.toJson(new ErrorResponse("error", exception.getMessage())));
        });
    }

}
