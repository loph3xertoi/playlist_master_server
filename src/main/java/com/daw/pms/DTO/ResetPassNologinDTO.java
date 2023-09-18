package com.daw.pms.DTO;

import com.daw.pms.Annotation.ValidPassword;
import java.io.Serializable;
import javax.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * DTO used when resetting your password without needs to login first.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/18/23
 */
@Data
public class ResetPassNologinDTO implements Serializable {
  /** Your new password. */
  @NotBlank(message = "No blank password.")
  @ValidPassword
  private String password;

  /** Your new password again. */
  @NotBlank(message = "No blank repeated password.")
  @ValidPassword
  private String repeatedPassword;

  /** Your bound email. */
  @NotBlank(message = "No blank email.")
  @Email(message = "Email format error.")
  private String email;

  /** Verification token got from your bound email. */
  @NotBlank(message = "Please input verified token")
  @Length(min = 8, max = 8, message = "Token length must be 8")
  @Pattern(
      regexp = "^[a-zA-Z0-9]+$",
      message = "Only english characters or digit are valid in token")
  private String token;
}
