package com.daw.pms.Validator;

import java.util.HashMap;
import org.passay.*;

/**
 * Validator for registration code.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 10/6/23
 */
public class RegistrationCodeValidator implements Rule {
  private static final String REGISTRATION_CODE_REGEX = "^[2-9A-HJ-NP-Z]{6}$|^$";

  /** {@inheritDoc} */
  @Override
  public RuleResult validate(final PasswordData passwordData) {
    final RuleResult result = new RuleResult();
    final String registrationCode = passwordData.getPassword();
    if (!registrationCode.matches(REGISTRATION_CODE_REGEX)) {
      HashMap<String, Object> map = new HashMap<>();
      map.put(
          "result",
          "The length of registration code should be 6 and contain uppercase english letters of digits.");
      result.addError("INVALID_REGISTRATION_CODE_FORMAT", map);
    }
    return result;
  }
}
