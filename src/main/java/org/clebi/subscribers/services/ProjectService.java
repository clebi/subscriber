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

import org.clebi.subscribers.model.Project;
import org.clebi.subscribers.services.exceptions.ProjectServiceException;

public interface ProjectService {

  boolean isMember(String projectName, String token) throws ProjectServiceException;

  Project getProject(String projectName, String token) throws ProjectServiceException;

}
