package com.daw.pms.Entity.Bilibili;

import com.daw.pms.Entity.Basic.BasicUser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * User info in bilibili.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 7/30/23
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BilibiliUser extends BasicUser {
  /** User id in bilibili. */
  Long mid;

  /** Your gender. */
  String gender;

  /** Your sign. */
  String sign;

  /** Your account's level in bilibili. */
  Integer level;

  /** Current level exp. */
  Integer currentLevelExp;

  /** Next level exp, if your level is 6, this value will be string "--". */
  Integer nextLevelExp;

  /** Your coins' count. */
  Integer coins;

  /** Your bcoin's count. */
  Integer bcoin;

  /** Upper you are following. */
  Integer following;

  /** Your follower's count. */
  Integer follower;

  /** Your dynamic count, */
  Integer dynamicCount;

  /** Your moral in bilibili. */
  Integer moral;

  /** Whether bind email. */
  Boolean bindEmail;

  /** Whether bind phone. */
  Boolean bindPhone;

  /** Vip type, 0 means no vip, 1 means month vip, 2 means year vip. */
  Integer vipType;

  /** Vip state. */
  Boolean vipActive;

  /** Expire time of your vip. */
  Long vipExpireTime;

  /** The icon corresponding to your vip level. */
  String vipIcon;

  /** The pendant name. */
  String pendantName;

  /** The expired time of pendant. */
  Long pendantExpireTime;

  /** The pendant image. */
  String pendantImage;

  /** The dynamic pendant image. */
  String dynamicPendantImage;

  /** The name of nameplate. */
  String nameplateName;

  /** The image of nameplate. */
  String nameplateImage;

  /** The small image of nameplate. */
  String smallNameplateImage;

  /** The obtaining condition of nameplate. */
  String nameplateCondition;

  /** Your birthday. */
  String birthday;

  /** Whether wearing fans badge. */
  Boolean wearingFansBadge;

  /** The level of your fans badge. */
  Integer fansBadgeLevel;

  /** The text of your fans badge. */
  String fansBadgeText;

  /** The start color of your fans badge. */
  Long fansBadgeStartColor;

  /** The end color of your fans badge. */
  Long fansBadgeEndColor;

  /** The border color of your fans badge. */
  Long fansBadgeBorderColor;
}
