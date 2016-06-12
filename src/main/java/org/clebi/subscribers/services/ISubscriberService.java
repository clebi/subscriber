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

package org.clebi.subscribers.services;

import org.clebi.subscribers.daos.exceptions.ValidationException;
import org.clebi.subscribers.model.Subscriber;
import org.clebi.subscribers.services.exceptions.ProjectServiceException;
import org.clebi.subscribers.services.exceptions.SubscriberServiceException;

public interface ISubscriberService {

  /**
   * Add subscriber to a project.
   *
   * @param projectName name of the project
   * @param subscriber  the subscriber to add
   * @param token       token of the caller
   * @throws ValidationException        unable to valid subscriber data
   * @throws ProjectServiceException    an error happened during project retrieval
   * @throws SubscriberServiceException an error happened during subscriber add
   */
  void addSubscriber(String projectName, Subscriber subscriber, String token)
      throws ValidationException, ProjectServiceException, SubscriberServiceException;
}
