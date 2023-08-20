package com.daw.pms.Service.Bilibili.impl;

import com.daw.pms.Config.BilibiliAPI;
import com.daw.pms.Entity.Bilibili.BiliUser;
import com.daw.pms.Service.Bilibili.BiliCookieService;
import com.daw.pms.Service.Bilibili.BiliUserService;
import com.daw.pms.Utils.HttpTools;
import com.daw.pms.Utils.WbiBiliBili;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class BiliUserServiceImpl implements BiliUserService {

  private final BiliCookieService biliCookieService;
  private final HttpTools httpTools;

  public BiliUserServiceImpl(BiliCookieService biliCookieService, HttpTools httpTools) {
    this.biliCookieService = biliCookieService;
    this.httpTools = httpTools;
  }

  /**
   * Return the user info for bilibili.
   *
   * @param cookie Your cookie for bilibili.
   * @return Your user info in bilibili.
   * @apiNote GET GET_LOGIN_INFO?mid={@code biliMid}
   */
  @Override
  public BiliUser getUserInfo(String cookie) {
    Map<String, String> wbiKey = biliCookieService.getWbiKey();
    String rawLoginInfo =
        httpTools.requestGetAPI(BilibiliAPI.GET_LOGIN_INFO, null, Optional.of(cookie));
    BiliUser user = extractLoginInfo(rawLoginInfo);
    Map<String, Object> params = new HashMap<>();
    params.put("mid", user.getMid());
    String signedUri =
        WbiBiliBili.wbiSignRequestParam(params, wbiKey.get("img_key"), wbiKey.get("sub_key"));
    String userSpaceUrl = BilibiliAPI.GET_USER_SPACE + "?" + signedUri;
    String rawUserSpace = httpTools.requestGetAPIByFinalUrl(userSpaceUrl, Optional.of(cookie));
    assignUserSpace(user, rawUserSpace);
    String rawUserState =
        httpTools.requestGetAPI(BilibiliAPI.GET_USER_STATE, null, Optional.of(cookie));
    assignUserState(user, rawUserState);
    String rawIpInfo = httpTools.requestGetAPI(BilibiliAPI.GET_IP_INFO, null, Optional.of(cookie));
    assignIpInfo(user, rawIpInfo);
    return user;
  }

  private void assignIpInfo(BiliUser user, String rawIpInfo) {
    JsonNode jsonNode;
    try {
      jsonNode = new ObjectMapper().readTree(rawIpInfo);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    JsonNode dataNode = jsonNode.get("data");
    user.setIp(dataNode.get("addr").textValue());
    user.setCountry(dataNode.get("country").textValue());
    user.setProvince(dataNode.get("province").textValue());
    //    user.setCity(dataNode.get("city").textValue());
    String isp = dataNode.get("isp").textValue();
    user.setIsp("移动".equals(isp) ? 0 : "电信".equals(isp) ? 1 : 2);
    user.setLatitude(dataNode.get("latitude").doubleValue());
    user.setLongitude(dataNode.get("longitude").doubleValue());
    user.setCountryCode(dataNode.get("country_code").intValue());
  }

  private void assignUserState(BiliUser user, String rawUserState) {
    JsonNode jsonNode;
    try {
      jsonNode = new ObjectMapper().readTree(rawUserState);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    JsonNode dataNode = jsonNode.get("data");
    user.setFollower(dataNode.get("follower").intValue());
    user.setFollowing(dataNode.get("following").intValue());
    user.setDynamicCount(dataNode.get("dynamic_count").intValue());
  }

  private void assignUserSpace(BiliUser user, String rawUserSpace) {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(rawUserSpace);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    JsonNode dataNode = jsonNode.get("data");
    JsonNode pendantNode = dataNode.get("pendant");
    JsonNode nameplateNode = dataNode.get("nameplate");
    JsonNode fansMedalNode = dataNode.get("fans_medal").get("medal");
    String gender = dataNode.get("sex").textValue();
    user.setGender("保密".equals(gender) ? 0 : "男".equals(gender) ? 1 : 2);
    //    user.setBgPic(dataNode.get("top_photo").textValue());
    user.setBgPic(dataNode.get("face").textValue());
    user.setSign(dataNode.get("sign").textValue());
    user.setPendantName(pendantNode.get("name").textValue());
    user.setPendantExpireTime(pendantNode.get("expire").longValue());
    user.setPendantImage(pendantNode.get("image").textValue());
    user.setDynamicPendantImage(pendantNode.get("image_enhance").textValue());
    user.setNameplateName(nameplateNode.get("name").textValue());
    user.setNameplateImage(nameplateNode.get("image").textValue());
    user.setSmallNameplateImage(nameplateNode.get("image_small").textValue());
    user.setNameplateCondition(nameplateNode.get("condition").textValue());
    user.setBirthday(dataNode.get("birthday").textValue());
    user.setWearingFansBadge(dataNode.get("fans_medal").get("wear").booleanValue());
    if (!fansMedalNode.isNull()) {
      user.setFansBadgeLevel(fansMedalNode.get("level").intValue());
      user.setFansBadgeText(fansMedalNode.get("medal_name").textValue());
      user.setFansBadgeStartColor(fansMedalNode.get("medal_color_start").longValue());
      user.setFansBadgeEndColor(fansMedalNode.get("medal_color_end").longValue());
      user.setFansBadgeBorderColor(fansMedalNode.get("medal_color_border").longValue());
    } else {
      user.setFansBadgeLevel(0);
      user.setFansBadgeText("");
      user.setFansBadgeStartColor(0L);
      user.setFansBadgeEndColor(0L);
      user.setFansBadgeBorderColor(0L);
    }
  }

  private BiliUser extractLoginInfo(String rawLoginInfo) {
    BiliUser user = new BiliUser();
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(rawLoginInfo);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    JsonNode dataNode = jsonNode.get("data");
    JsonNode levelNode = dataNode.get("level_info");
    user.setName(dataNode.get("uname").textValue());
    user.setHeadPic(dataNode.get("face").textValue());
    user.setMid(dataNode.get("mid").longValue());
    user.setLevel(levelNode.get("current_level").intValue());
    user.setCurrentLevelExp(levelNode.get("current_exp").intValue());
    user.setNextLevelExp(levelNode.get("next_exp").intValue());
    user.setCoins(dataNode.get("money").intValue());
    user.setBcoin(dataNode.get("wallet").get("bcoin_balance").intValue());
    user.setMoral(dataNode.get("moral").intValue());
    user.setBindEmail(dataNode.get("email_verified").intValue() == 1);
    user.setBindPhone(dataNode.get("mobile_verified").intValue() == 1);
    user.setVipType(dataNode.get("vipType").intValue());
    user.setVipActive(dataNode.get("vipStatus").intValue() == 1);
    user.setVipExpireTime(dataNode.get("vipDueDate").longValue());
    user.setVipIcon(dataNode.get("vip_label").get("img_label_uri_hans_static").textValue());
    return user;
  }
}
