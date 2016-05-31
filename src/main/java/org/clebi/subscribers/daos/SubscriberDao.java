package org.clebi.subscribers.daos;

import org.clebi.subscribers.daos.exceptions.NotFoundException;
import org.clebi.subscribers.daos.exceptions.ValidationException;
import org.clebi.subscribers.model.Subscriber;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface SubscriberDao {

  /**
   * Add subscriber.
   *
   * @param subscriber the subscriber to add
   * @throws ValidationException subscriber data are not valid
   */
  void addSubscriber(Subscriber subscriber) throws ValidationException;

  /**
   * Get a subscriber by email.
   *
   * @param email email of the subscriber
   * @return the subscriber
   */
  Subscriber getSubscriber(String email) throws NotFoundException;

  List<Subscriber> listOptins(int size, int offset) throws ExecutionException, InterruptedException;

}
