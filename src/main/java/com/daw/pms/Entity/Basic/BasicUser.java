package com.daw.pms.Entity.Basic;

import lombok.Data;

/**
 * Basic user.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 7/11/23
 */
@Data
public class BasicUser {
  /** Your name. */
  private String name;

  /** Your head picture. */
  private String headPic;

  /** Your background picture. */
  private String bgPic;
}
