package org.clebi.subscribers.daos.elasticsearch;

import com.google.gson.Gson;
import com.google.inject.Inject;
import org.clebi.subscribers.daos.SubscriberDao;
import org.clebi.subscribers.daos.exceptions.ValidationException;
import org.clebi.subscribers.model.Subscriber;
import org.clebi.subscribers.providers.EsCheckedProvider;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;

public class SubscriberDaoImpl implements SubscriberDao {

    Client client;
    Gson gson = new Gson();

    @Inject
    public SubscriberDaoImpl(EsCheckedProvider<Client> client) throws Exception {
        this.client = client.get();
    }

    @Override
    public void addSubscriber(Subscriber subscriber) throws ValidationException {
        if (!subscriber.isValid()) {
            throw new ValidationException("unable to validate subscriber model");
        }
        IndexResponse resp = client.prepareIndex("subscribers", "subscriber")
                .setSource(gson.toJson(subscriber))
                .setId(subscriber.getEmail())
                .get();
    }
}
