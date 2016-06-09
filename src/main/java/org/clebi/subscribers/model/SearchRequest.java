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

package org.clebi.subscribers.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Data
public class SearchRequest {

  public static int DEFAULT_SIZE = 50;
  public static int DEFAULT_OFFSET = 0;

  private int size = DEFAULT_SIZE;
  private int offset = DEFAULT_OFFSET;

  private Boolean optin;
  private Boolean active;

  /**
   * Get the primary filters from optin and active fields.
   *
   * @return the list of filters
   */
  public List<SearchFilter> getPrimaryFilters() {
    List<SearchFilter> filters = new LinkedList<>();
    if (optin != null) {
      List<Object> values = new ArrayList<>();
      values.add(optin);
      filters.add(new SearchFilter("optin", FilterOperand.EQUAL, values));
    }
    if (active != null) {
      List<Object> values = new ArrayList<>();
      values.add(active);
      filters.add(new SearchFilter("active", FilterOperand.EQUAL, values));
    }
    return filters;
  }

}
