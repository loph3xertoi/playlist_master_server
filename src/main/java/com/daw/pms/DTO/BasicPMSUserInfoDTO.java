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
  /** The id of pms user. */
  private Long id;

  /** User's name. */
  private String name;

  /** User's role, include ROLE_USER, ROLE_ADMIN. */
  private String role;

  /** User's email. */
  private String email;

  /** User's phone number. */
  private String phone;

  /** User's avatar. */
  private String avatar;

  /** Login type: 0 for email &amp; password, 1 for GitHub, 2 for Google. */
  private Integer loginType;

  /** User id in qqmusic. */
  private String qqMusicId;

  /** User cookie in qqmusic. */
  private String qqMusicCookie;

  /** User id in ncm. */
  private String ncmId;

  /** User cookie in ncm. */
  private String ncmCookie;

  /** User id in bilibili. */
  private String biliId;

  /** User cookie in bilibili. */
  private String biliCookie;
}
