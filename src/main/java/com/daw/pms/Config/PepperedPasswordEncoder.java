package com.daw.pms.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PepperedPasswordEncoder implements PasswordEncoder {
  @Value("${pms.pepper}")
  private String pepper;

  private final PasswordEncoder argon2PasswordEncoder = new Argon2PasswordEncoder();

  @Override
  public String encode(CharSequence rawPassword) {
    String passwordWithPepper = rawPassword + pepper;
    return argon2PasswordEncoder.encode(passwordWithPepper);
  }

  @Override
  public boolean matches(CharSequence rawPassword, String encodedPassword) {
    String passwordWithPepper = rawPassword + pepper;
    return argon2PasswordEncoder.matches(passwordWithPepper, encodedPassword);
  }
}
