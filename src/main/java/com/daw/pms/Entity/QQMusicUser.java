package com.daw.pms.Entity;

import lombok.Data;

/**
 * POJO for user info of qq music.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 6/1/23
 */
@Data
public class QQMusicUser {
  /** The username of your qq music. */
  private String name;

  /** The head picture of your account. */
  private String headPic;

  /** The icon for your qq music vip. */
  private String lvPic;

  /** The icon of your listen level. */
  private String listenPic;

  /** Background picture of your qq music homepage. */
  private String bgPic;

  /** The number of people visited your homepage. */
  private Integer visitorNum;

  /** The number of your qq music fans. */
  private Integer fansNum;

  /** The number of people you followed. */
  private Integer followNum;

  /** The number of qq friends. */
  private Integer friendsNum;
}
