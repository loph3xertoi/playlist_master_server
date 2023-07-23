package com.daw.pms.Entity.QQMusic;

import com.daw.pms.Entity.Basic.BasicUser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * User info of qq music.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 6/1/23
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class QQMusicUser extends BasicUser {
  /** QQ number. */
  private Long qqNumber;

  /** The icon for your qq music vip. */
  private String lvPic;

  /** The icon of your listen level. */
  private String listenPic;

  /** The number of people visited your homepage. */
  private Integer visitorNum;

  /** The number of your qq music fans. */
  private Integer fansNum;

  /** The number of people you are following. */
  private Integer followNum;

  /** The number of qq friends. */
  private Integer friendsNum;
}
