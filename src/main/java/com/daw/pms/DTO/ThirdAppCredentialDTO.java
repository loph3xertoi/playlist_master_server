package com.daw.pms.DTO;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Handle third app credential.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/8/23
 */
@Data
public class ThirdAppCredentialDTO implements Serializable {
  /** User id in third app. */
  @NotBlank(message = "Invalid third app id.")
  private String thirdId;

  /** Cookie in the third app. */
  @NotBlank(message = "Invalid third app cookie.")
  private String thirdCookie;
}
