package org.clebi.subscribers.controllers;

import com.google.gson.Gson;
import org.clebi.subscribers.model.Subscriber;
import org.clebi.subscribers.transformers.JsonResponseTransformer;

import static spark.Spark.*;

public class SubscriberController {

    private Gson gson = new Gson();

    public SubscriberController() {
        post("/add-user/", ((request, response) -> {
            Subscriber subscriber = gson.fromJson(request.body(), Subscriber.class);
            System.out.println(subscriber);
            return subscriber;
        }), new JsonResponseTransformer());
    }

}
