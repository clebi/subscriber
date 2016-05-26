package org.clebi.subscribers.daos;

import org.clebi.subscribers.daos.exceptions.ValidationException;
import org.clebi.subscribers.model.Subscriber;

public interface SubscriberDao {

  /**
   * Add subscriber.
   *
   * @param subscriber the subscriber to add
   * @throws ValidationException subscriber data are not valid
   */
  void addSubscriber(Subscriber subscriber) throws ValidationException;

}
