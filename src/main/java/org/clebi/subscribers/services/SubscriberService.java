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

import com.google.inject.Inject;

import org.clebi.subscribers.daos.SubscriberDao;
import org.clebi.subscribers.daos.exceptions.ValidationException;
import org.clebi.subscribers.model.Project;
import org.clebi.subscribers.model.Subscriber;
import org.clebi.subscribers.services.exceptions.ProjectServiceException;
import org.clebi.subscribers.services.exceptions.SubscriberServiceException;

import java.util.Map;

public class SubscriberService implements ISubscriberService {

  private static final String ERROR_FIELD_MISS_STR = "field %s is missing";
  private static final String ERROR_FIELD_TYPE_STR = "field %s has wrong type, it should be a %s";
  private static final String ERROR_TYPE_UNKNOWN = "unknown type in project";

  private final SubscriberDao subscriberDao;
  private final ProjectService projectService;

  @Inject
  public SubscriberService(SubscriberDao subscriberDao, ProjectService projectService) {
    this.subscriberDao = subscriberDao;
    this.projectService = projectService;
  }

  @Override
  public void addSubscriber(String projectName, Subscriber subscriber, String token)
      throws ValidationException, ProjectServiceException, SubscriberServiceException {
    Project project = projectService.getProject(projectName, token);
    for (Map.Entry<String, String> field : project.getFields().entrySet()) {
      String fieldName = field.getKey();
      Object subscriberFieldVal = subscriber.getFields().get(fieldName);
      if (subscriberFieldVal == null) {
        throw new SubscriberServiceException(String.format(ERROR_FIELD_MISS_STR, fieldName));
      }
      checkField(fieldName, subscriberFieldVal, field.getValue());
    }
    subscriberDao.addSubscriber(projectName, subscriber);
  }

  private void checkField(String fieldName, Object subscriberValue, String projectType)
      throws SubscriberServiceException {
    switch (projectType) {
      case "string":
        if (subscriberValue.getClass() != String.class) {
          throw new SubscriberServiceException(String.format(ERROR_FIELD_TYPE_STR, fieldName, projectType));
        }
        break;
      case "numeric":
        if (subscriberValue.getClass() != Double.class) {
          throw new SubscriberServiceException(String.format(ERROR_FIELD_TYPE_STR, fieldName, projectType));
        }
        break;
      default:
        throw new RuntimeException(ERROR_TYPE_UNKNOWN);
    }
  }

}
