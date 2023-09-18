package com.daw.pms.DTO;

import com.daw.pms.Annotation.ValidPassword;
import com.daw.pms.Annotation.ValidPhoneNumber;
import java.io.Serializable;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO when registering new user with email and password.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/18/23
 */
@Data
public class RegisterFormDTO implements Serializable {
  /** Your user name. */
  @NotBlank(message = "No blank name.")
  private String name;

  /** Your email. */
  @NotBlank(message = "No blank email.")
  @Email(message = "Email format error.")
  private String email;

  /** Your phone number. */
  @ValidPhoneNumber private String phoneNumber;

  /** Your password. */
  @ValidPassword private String password;
}
