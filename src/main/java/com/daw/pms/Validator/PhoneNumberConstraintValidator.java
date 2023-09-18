package com.daw.pms.Validator;

import com.daw.pms.Annotation.ValidPhoneNumber;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.passay.*;

/**
 * Constraint validator for phone number.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/18/23
 */
public class PhoneNumberConstraintValidator
    implements ConstraintValidator<ValidPhoneNumber, String> {

  @Override
  public void initialize(ValidPhoneNumber arg0) {}

  @Override
  public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
    PasswordValidator passwordValidator = new PasswordValidator(new PhoneNumberValidator());

    RuleResult result = passwordValidator.validate(new PasswordData(phoneNumber));
    if (result.isValid()) {
      return true;
    }
    context.disableDefaultConstraintViolation();
    context
        .buildConstraintViolationWithTemplate(
            String.join(",", passwordValidator.getMessages(result)))
        .addConstraintViolation();
    return false;
  }
}
