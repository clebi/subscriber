package org.clebi.subscribers.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class SearchFilter {

  private String fieldName;
  private FilterOperand operand;
  private List<Object> values;

}
