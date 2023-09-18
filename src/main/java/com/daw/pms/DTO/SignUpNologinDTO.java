package com.daw.pms.DTO;

import com.daw.pms.Annotation.ValidPassword;
import com.daw.pms.Annotation.ValidPhoneNumber;
import java.io.Serializable;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * DTO used when signing up new user with email and password without needs to login first.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/18/23
 */
@Data
public class SignUpNologinDTO implements Serializable {
  /** User name. */
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

  /** Verification token got from the email above. */
  @NotBlank(message = "Please input verified token")
  @Length(min = 8, max = 8, message = "Token length must be 8")
  @Pattern(
      regexp = "^[a-zA-Z0-9]+$",
      message = "Only english characters or digit are valid in token")
  private String token;
}
