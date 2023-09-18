package com.daw.pms.DTO;

import java.io.Serializable;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO when login using email and password.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/18/23
 */
@Data
public class LoginFormDTO implements Serializable {
  /** User name also email. */
  @NotBlank(message = "No blank email.")
  @Email(message = "Email format error.")
  private String email;

  /** Your password. */
  @NotBlank(message = "No blank password.")
  private String password;
}
