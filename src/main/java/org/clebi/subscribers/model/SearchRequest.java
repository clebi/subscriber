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
