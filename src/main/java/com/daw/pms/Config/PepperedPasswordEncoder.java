package com.daw.pms.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Pepper password encoder.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/18/23
 */
public class PepperedPasswordEncoder implements PasswordEncoder {
  @Value("${pms.pepper}")
  private String pepper;

  private final PasswordEncoder argon2PasswordEncoder = new Argon2PasswordEncoder();

  /** {@inheritDoc} */
  @Override
  public String encode(CharSequence rawPassword) {
    String passwordWithPepper = rawPassword + pepper;
    return argon2PasswordEncoder.encode(passwordWithPepper);
  }

  /** {@inheritDoc} */
  @Override
  public boolean matches(CharSequence rawPassword, String encodedPassword) {
    String passwordWithPepper = rawPassword + pepper;
    return argon2PasswordEncoder.matches(passwordWithPepper, encodedPassword);
  }
}
