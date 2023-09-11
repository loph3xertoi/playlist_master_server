package com.daw.pms.DTO;

import java.io.Serializable;
import lombok.Data;

/**
 * Basic pms user info dto.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/11/23
 */
@Data
public class BasicPMSUserInfoDTO implements Serializable {
  private Long id;
  private String name;
  private String role;
  private String email;
  private String phone;
}
