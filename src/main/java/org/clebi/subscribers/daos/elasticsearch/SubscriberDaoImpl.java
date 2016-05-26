package org.clebi.subscribers.daos.elasticsearch;

import com.google.gson.Gson;
import com.google.inject.Inject;

import org.clebi.subscribers.daos.SubscriberDao;
import org.clebi.subscribers.daos.exceptions.NotFoundException;
import org.clebi.subscribers.daos.exceptions.ValidationException;
import org.clebi.subscribers.model.Subscriber;
import org.clebi.subscribers.model.serialize.JsonFactory;
import org.clebi.subscribers.providers.EsCheckedProvider;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;

public class SubscriberDaoImpl implements SubscriberDao {

  private static final String ERROR_GET_NOT_FOUND = "unable to find subscriber with email: %s";

  private final Client client;
  private final Gson gson = JsonFactory.getGson();

  @Inject
  public SubscriberDaoImpl(EsCheckedProvider<Client> client) throws Exception {
    this.client = client.get();
  }

  @Override
  public void addSubscriber(Subscriber subscriber) throws ValidationException {
    if (!subscriber.isValid()) {
      throw new ValidationException("unable to validate subscriber model");
    }
    client.prepareIndex("subscribers", "subscriber")
        .setSource(gson.toJson(subscriber))
        .setId(subscriber.getEmail().toString())
        .get();
  }

  @Override
  public Subscriber getSubscriber(String email) throws NotFoundException {
    GetResponse resp = client.prepareGet("subscribers", "subscriber", email).get();
    Subscriber subscriber = gson.fromJson(resp.getSourceAsString(), Subscriber.class);
    if (subscriber == null || !subscriber.isValid()) {
      throw new NotFoundException(String.format(ERROR_GET_NOT_FOUND, email));
    }
    return subscriber;
  }
}
