package com.daw.pms.DTO;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for handling pms user info.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/18/23
 */
@Data
public class UserDTO implements Serializable {
  /** User's id in pms. */
  private Long id;

  /** User's name in pms. */
  @NotBlank private String name;

  /** User's password in pms. */
  @NotBlank private String pass;

  /** User's role in pms. */
  @NotBlank private String role;

  /** User's email in pms. */
  private String email;

  /** User's phone number in pms. */
  private String phone;

  /**
   * Indicates whether the user is enabled or disabled. <code>true</code> if the user is enabled,
   * <code>false</code> otherwise
   */
  @NotBlank private Boolean enabled;

  /** Login type of this user, 0 for email &amp; password, 1 for GitHub, 2 for Google. */
  @NotBlank private Integer loginType;

  /** User's introduction in pms. */
  private String intro;

  /** User's avatar in pms. */
  private String avatar;

  /** User's background picture. */
  private String bgPic;

  /** User's id in QQ Music platform. */
  private Long qqmusicId;

  /** User's cookie in QQ Music platform. */
  private String qqmusicCookie;

  /** User's id in Netease Cloud Music platform. */
  private Long ncmId;

  /** User's cookie in Netease Cloud Music platform. */
  private String ncmCookie;

  /** User's id in BiliBili platform. */
  private Long bilibiliId;

  /** User's cookie in BiliBili platform. */
  private String biliCookie;
}
