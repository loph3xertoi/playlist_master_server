package com.daw.pms.DTO;

import java.io.Serializable;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * DTO used when binding email.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/18/23
 */
@Data
public class BindEmailDTO implements Serializable {
  /** Email to bind. */
  @NotBlank(message = "No blank email.")
  @Email(message = "Email format error.")
  private String email;

  /** Verification token got from email. */
  @NotBlank(message = "Please input verified token")
  @Length(min = 8, max = 8, message = "Token length must be 8")
  @Pattern(
      regexp = "^[a-zA-Z0-9]+$",
      message = "Only english characters or digit are valid in token")
  private String token;
}
