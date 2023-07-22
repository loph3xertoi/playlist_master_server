package com.daw.pms.Entity.NeteaseCloudMusic;

import com.daw.pms.Entity.Basic.BasicUser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * User info for netease cloud music.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 7/21/23
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class NCMUser extends BasicUser {
  /** User id for netease cloud music. */
  private Long id;

  /** Your netease cloud music level. */
  private Short level;

  /** Total listened songs' number. */
  private Integer listenSongs;

  /** The number of people you are following. */
  private Integer follows;

  /** The number of your fans. */
  private Integer fans;

  /** The count of all your playlists. */
  private Integer playlistCount;

  /** User creating time. */
  private Long createTime;

  /** User vip type, 0 means no vip. */
  private Integer vipType;

  /** Your red vip level. */
  private Integer redVipLevel;

  /** Expire time of your red vip. */
  private Long redVipExpireTime;

  /** The icon corresponding to your red vip level. */
  private String redVipLevelIcon;

  /** Dynamic icon for your red vip. */
  private String redVipDynamicIconUrl;

  /** Dynamic icon2 for your red vip. */
  private String redVipDynamicIconUrl2;

  /** Your music package vip level. */
  private Integer musicPackageVipLevel;

  /** Expire time of your music package vip. */
  private Long musicPackageVipExpireTime;

  /** The icon corresponding to your music package vip level. */
  private String musicPackageVipLevelIcon;

  /** Your red plus vip level. */
  private Integer redPlusVipLevel;

  /** Expire time of your red plus vip. */
  private Long redPlusVipExpireTime;

  /** The icon corresponding to your red plus vip level. */
  private String redPlusVipLevelIcon;

  /** Your signature. */
  private String signature;

  /** Your birthday. */
  private Long birthday;

  /** Your gender, secret :0, male: 1, female: 2. */
  private Short gender;

  /** Province region code. */
  private Integer province;

  /** City region code. */
  private Integer city;

  /** Last login time. */
  private Long lastLoginTime;

  /** Last login IP. */
  private String lastLoginIP;
}
