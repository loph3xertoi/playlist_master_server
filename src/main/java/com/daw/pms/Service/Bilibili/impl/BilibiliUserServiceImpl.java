package com.daw.pms.Service.Bilibili.impl;

import com.daw.pms.Config.BilibiliAPI;
import com.daw.pms.Entity.Bilibili.BilibiliUser;
import com.daw.pms.Service.Bilibili.BilibiliCookieService;
import com.daw.pms.Service.Bilibili.BilibiliUserService;
import com.daw.pms.Utils.HttpTools;
import com.daw.pms.Utils.WbiBiliBili;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class BilibiliUserServiceImpl implements BilibiliUserService {

  private final BilibiliCookieService bilibiliCookieService;
  private final HttpTools httpTools;

  public BilibiliUserServiceImpl(BilibiliCookieService bilibiliCookieService, HttpTools httpTools) {
    this.bilibiliCookieService = bilibiliCookieService;
    this.httpTools = httpTools;
  }

  /**
   * Return the user info for bilibili.
   *
   * @param cookie Your cookie for bilibili.
   * @return Your user info in bilibili.
   */
  @Override
  public BilibiliUser getUserInfo(String cookie) {
    Map<String, String> wbiKey = bilibiliCookieService.getWbiKey();
    String rawLoginInfo =
        httpTools.requestGetAPI(BilibiliAPI.GET_LOGIN_INFO, null, Optional.of(cookie));
    BilibiliUser user = extractLoginInfo(rawLoginInfo);
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
    return user;
  }

  private void assignUserState(BilibiliUser user, String rawUserState) {
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

  private void assignUserSpace(BilibiliUser user, String rawUserSpace) {
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
    user.setGender(dataNode.get("sex").textValue());
    user.setBgPic(dataNode.get("top_photo").textValue());
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
    user.setFansBadgeLevel(fansMedalNode.get("level").intValue());
    user.setFansBadgeText(fansMedalNode.get("medal_name").textValue());
    user.setFansBadgeStartColor(fansMedalNode.get("medal_color_start").longValue());
    user.setFansBadgeEndColor(fansMedalNode.get("medal_color_end").longValue());
    user.setFansBadgeBorderColor(fansMedalNode.get("medal_color_border").longValue());
  }

  private BilibiliUser extractLoginInfo(String rawLoginInfo) {
    BilibiliUser user = new BilibiliUser();
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
