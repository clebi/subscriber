package org.clebi.subscribers.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.validator.routines.EmailValidator;
import org.clebi.subscribers.validation.Validator;

@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class Email implements Validator, CharSequence {

  private String email;

  @Override
  public boolean isValid() {
    return EmailValidator.getInstance().isValid(email);
  }

  @Override
  public int length() {
    return email.length();
  }

  @Override
  public char charAt(int index) {
    return email.charAt(index);
  }

  @Override
  public CharSequence subSequence(int start, int end) {
    return email.subSequence(start, end);
  }

  @Override
  public String toString() {
    return email;
  }
}
