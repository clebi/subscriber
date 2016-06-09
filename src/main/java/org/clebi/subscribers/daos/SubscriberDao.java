package org.clebi.subscribers.daos;

import org.clebi.subscribers.daos.exceptions.NotFoundException;
import org.clebi.subscribers.daos.exceptions.ValidationException;
import org.clebi.subscribers.model.SearchFilter;
import org.clebi.subscribers.model.Subscriber;

import java.util.List;

public interface SubscriberDao {

  /**
   * Add subscriber.
   *
   * @param subscriber the subscriber to add
   * @throws ValidationException subscriber data are not valid
   */
  void addSubscriber(String project, Subscriber subscriber) throws ValidationException;

  /**
   * Get a subscriber by email.
   *
   * @param email email of the subscriber
   * @return the subscriber
   */
  Subscriber getSubscriber(String project, String email) throws NotFoundException;

  /**
   * Search for subscribers.
   *
   * @param size    number of search results to get
   * @param offset  from where to get the subscribers
   * @param filters the list of filters for the search
   * @return the list of matched users
   */
  List<Subscriber> search(String project, int size, int offset, List<SearchFilter> filters);

}
