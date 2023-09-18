package com.daw.pms.DTO;

import com.daw.pms.Annotation.ValidPassword;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * DTO when resetting your password.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/18/23
 */
@Data
public class ResetPassDTO implements Serializable {
  /** Your new password. */
  @NotBlank(message = "No blank password.")
  @ValidPassword
  private String password;

  /** Your new password again. */
  @NotBlank(message = "No blank repeated password.")
  @ValidPassword
  private String repeatedPassword;

  /** Verification token got from your bound email. */
  @NotBlank(message = "Please input verified token")
  @Length(min = 8, max = 8, message = "Token length must be 8")
  @Pattern(
      regexp = "^[a-zA-Z0-9]+$",
      message = "Only english characters or digit are valid in token")
  private String token;
}
