package com.daw.pms.DTO;

import lombok.Data;

/**
 * Login result.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/30/23
 */
@Data
public class LoginResult {
  /** The id of logged user in pms. */
  private Long id;

  /** Cookie of logged user in pms. */
  private String cookie;

  /** If the user exists in pms. */
  private Boolean userExists;
}
