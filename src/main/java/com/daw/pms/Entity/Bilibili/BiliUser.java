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
public class BiliUser extends BasicUser {
  /** User id in bilibili. */
  private Long mid;

  /** Your gender, 0 represents secret, 1 represents male, 2 represents female. */
  private int gender;

  /** Your sign. */
  private String sign;

  /** Your account's level in bilibili. */
  private int level;

  /** Current level exp. */
  private int currentLevelExp;

  /** Next level exp, if your level is 6, this value will be string "--". */
  private int nextLevelExp;

  /** Your coins' count. */
  private int coins;

  /** Your bcoin's count. */
  private int bcoin;

  /** Upper you are following. */
  private int following;

  /** Your follower's count. */
  private int follower;

  /** Your dynamic count, */
  private int dynamicCount;

  /** Your moral in bilibili. */
  private int moral;

  /** Whether bind email. */
  private Boolean bindEmail;

  /** Whether bind phone. */
  private Boolean bindPhone;

  /** Vip type, 0 means no vip, 1 means month vip, 2 means year vip. */
  private int vipType;

  /** Vip state. */
  private Boolean vipActive;

  /** Expire time of your vip. */
  private Long vipExpireTime;

  /** The icon corresponding to your vip level. */
  private String vipIcon;

  /** The pendant name. */
  private String pendantName;

  /** The expired time of pendant. */
  private Long pendantExpireTime;

  /** The pendant image. */
  private String pendantImage;

  /** The dynamic pendant image. */
  private String dynamicPendantImage;

  /** The name of nameplate. */
  private String nameplateName;

  /** The image of nameplate. */
  private String nameplateImage;

  /** The small image of nameplate. */
  private String smallNameplateImage;

  /** The obtaining condition of nameplate. */
  private String nameplateCondition;

  /** Your birthday. */
  private String birthday;

  /** Whether wearing fans badge. */
  private Boolean wearingFansBadge;

  /** The level of your fans badge. */
  private int fansBadgeLevel;

  /** The text of your fans badge. */
  private String fansBadgeText;

  /** The start color of your fans badge. */
  private Long fansBadgeStartColor;

  /** The end color of your fans badge. */
  private Long fansBadgeEndColor;

  /** The border color of your fans badge. */
  private Long fansBadgeBorderColor;

  /** The ip address. */
  private String ip;

  /** Your country. */
  private String country;

  /** Your province. */
  private String province;

  //  /** Your city. */
  //  private String city;

  /** Your ISP, 0 represents china mobile, 1 represents china telecom. */
  private int isp;

  /** Your latitude. */
  private Double latitude;

  /** Your longitude. */
  private Double longitude;

  /** Your country code. */
  private int countryCode;
}
