package com.daw.pms.Validator;

import com.daw.pms.Annotation.ValidRegistrationCode;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.passay.*;

/**
 * Constraint validator for registration code.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 10/6/23
 */
public class RegistrationCodeConstraintValidator
    implements ConstraintValidator<ValidRegistrationCode, String> {

  /** {@inheritDoc} */
  @Override
  public void initialize(ValidRegistrationCode arg0) {}

  /** {@inheritDoc} */
  @Override
  public boolean isValid(String registrationCode, ConstraintValidatorContext context) {
    PasswordValidator passwordValidator = new PasswordValidator(new RegistrationCodeValidator());

    RuleResult result = passwordValidator.validate(new PasswordData(registrationCode));
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
