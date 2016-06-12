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

import org.clebi.subscribers.configuration.GlobalConfig;
import org.clebi.subscribers.model.Project;
import org.clebi.subscribers.modules.exceptions.ConfigurationException;
import org.clebi.subscribers.modules.providers.ConfigCheckedProvider;
import org.clebi.subscribers.services.exceptions.ProjectServiceException;

import java.util.HashMap;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;

public class ProjecterService implements ProjectService {

  private String url;
  private Client wsClient;

  /**
   * Create porject service.
   * @param wsClient a jaxrs webservice client
   * @param config configuration
   * @throws ConfigurationException an error happened during configuration loading
   */
  @Inject
  public ProjecterService(Client wsClient, ConfigCheckedProvider<GlobalConfig> config) throws ConfigurationException {
    this.url = config.get().getProjecter().getUrl();
    this.wsClient = wsClient;
  }

  @Override
  public boolean isMember(String projectName, String token) throws ProjectServiceException {
    try {
      HashMap resp = wsClient.target(url).path("project/" + projectName + "/is-member/")
          .request(MediaType.APPLICATION_JSON_TYPE)
          .header("Authorization", token)
          .get(HashMap.class);
      return (boolean) resp.get("isMember");
    } catch (NotFoundException exc) {
      throw new ProjectServiceException("unable to find project " + projectName);
    }
  }

  @Override
  public Project getProject(String projectName, String token) throws ProjectServiceException {
    Project project = wsClient.target(url).path("project/" + projectName + "/")
        .request(MediaType.APPLICATION_JSON_TYPE)
        .header("Authorization", token)
        .get(Project.class);
    return project;
  }
}
