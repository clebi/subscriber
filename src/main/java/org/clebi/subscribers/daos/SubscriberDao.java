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

  /**
   * Get the list of all subscribers.
   * @param size number of search results to get
   * @param offset from where to get the subscribers
   * @return the list of subscribers
   * @throws Exception an error happened during query
   */
  List<Subscriber> list(int size, int offset) throws Exception;

  /**
   * List all optins subscribers.
   * @param size number of search results to get
   * @param offset from where to get subscribers
   * @return the results of the search
   * @throws ExecutionException an error happened during query execution
   * @throws InterruptedException query was interrupted
   */
  List<Subscriber> listOptins(int size, int offset) throws ExecutionException, InterruptedException;

}
