package com.daw.pms.Validator;

import java.util.HashMap;
import org.passay.*;

/**
 * Validator for phone number.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/18/23
 */
public class PhoneNumberValidator implements Rule {
  private static final String PHONE_NUMBER_REGEX = "^[+]?\\d{1,3}[-\\s]?\\d{1,14}$";

  @Override
  public RuleResult validate(final PasswordData passwordData) {
    final RuleResult result = new RuleResult();
    final String phoneNumber = passwordData.getPassword();
    if (!phoneNumber.matches(PHONE_NUMBER_REGEX)) {
      HashMap<String, Object> map = new HashMap<>();
      map.put("result", "Invalid phone number.");
      result.addError("INVALID_PHONE_NUMBER", map);
    }
    return result;
  }
}
