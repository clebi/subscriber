// Copyright 2016 Clément Bizeau
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

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
