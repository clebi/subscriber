package org.clebi.subscribers.model;

public enum FilterOperand {

  EQUAL("EQUAL");

  private final String operand;

  FilterOperand(String operand) {
    this.operand = operand;
  }

  public String getOperand() {
    return operand;
  }
}
