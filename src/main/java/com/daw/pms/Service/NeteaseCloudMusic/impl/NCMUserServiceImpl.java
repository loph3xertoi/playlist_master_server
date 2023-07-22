package com.daw.pms.Service.NeteaseCloudMusic.impl;

import com.daw.pms.Config.NCMAPI;
import com.daw.pms.Entity.NeteaseCloudMusic.NCMUser;
import com.daw.pms.Service.NeteaseCloudMusic.NCMUserService;
import com.daw.pms.Utils.HttpTools;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class NCMUserServiceImpl implements NCMUserService {
  private final HttpTools httpTools;

  public NCMUserServiceImpl(HttpTools httpTools) {
    this.httpTools = httpTools;
  }

  /**
   * Return the user info of netease cloud music.
   *
   * @param uid Your user id in netease cloud music.
   * @param cookie Your cookie for netease cloud music.
   * @return Your user info for your netease cloud music wrapped in NCMUser.
   */
  @Override
  public NCMUser getUserInfo(String uid, String cookie) {
    String baseUrl = httpTools.ncmHost + ":" + httpTools.ncmPort;
    NCMUser ncmUser =
        extractNCMUser(
            httpTools.requestGetAPI(
                baseUrl + NCMAPI.USER_DETAIL,
                new HashMap<String, String>() {
                  {
                    put("uid", uid);
                  }
                },
                Optional.of(cookie)));
    assignVipInfo(
        ncmUser,
        httpTools.requestGetAPI(
            baseUrl + NCMAPI.VIP_INFO, new HashMap<String, String>(), Optional.of(cookie)));
    assignLastLoginInfo(
        ncmUser,
        httpTools.requestGetAPI(
            baseUrl + NCMAPI.USER_ACCOUNT, new HashMap<String, String>(), Optional.of(cookie)));
    return ncmUser;
  }

  /**
   * Resolve raw user info returned by ncm proxy server.
   *
   * @param rawUserInfo Raw user info.
   * @return Your user info in ncm platform.
   */
  private NCMUser extractNCMUser(String rawUserInfo) {
    JsonNode jsonNode;
    try {
      jsonNode = new ObjectMapper().readTree(rawUserInfo);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    JsonNode profileNode = jsonNode.get("profile");
    NCMUser ncmUser = new NCMUser();
    ncmUser.setName(profileNode.get("nickname").textValue());
    ncmUser.setHeadPic(profileNode.get("avatarUrl").textValue());
    ncmUser.setBgPic(profileNode.get("backgroundUrl").textValue());
    ncmUser.setId(profileNode.get("userId").longValue());
    ncmUser.setLevel(jsonNode.get("level").shortValue());
    ncmUser.setListenSongs(jsonNode.get("listenSongs").intValue());
    ncmUser.setFollows(profileNode.get("follows").intValue());
    ncmUser.setFans(profileNode.get("followeds").intValue());
    ncmUser.setPlaylistCount(profileNode.get("playlistCount").intValue());
    ncmUser.setCreateTime(profileNode.get("createTime").longValue());
    ncmUser.setVipType(profileNode.get("vipType").intValue());

    ncmUser.setSignature(profileNode.get("signature").textValue());
    ncmUser.setBirthday(profileNode.get("birthday").longValue());
    ncmUser.setGender(profileNode.get("gender").shortValue());
    ncmUser.setProvince(profileNode.get("province").intValue());
    ncmUser.setCity(profileNode.get("city").intValue());

    return ncmUser;
  }

  /**
   * Assign detail vip info to user.
   *
   * @param user Your user info.
   * @param rawVipInfo Raw vip info.
   */
  void assignVipInfo(NCMUser user, String rawVipInfo) {
    JsonNode jsonNode;
    try {
      jsonNode = new ObjectMapper().readTree(rawVipInfo);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    JsonNode dataNode = jsonNode.get("data");
    user.setRedVipLevel(dataNode.get("redVipLevel").intValue());
    user.setRedVipExpireTime(dataNode.get("associator").get("expireTime").longValue());
    user.setRedVipLevelIcon(dataNode.get("redVipLevelIcon").textValue());
    user.setRedVipDynamicIconUrl(dataNode.get("redVipDynamicIconUrl").textValue());
    user.setRedVipDynamicIconUrl2(dataNode.get("redVipDynamicIconUrl2").textValue());
    user.setMusicPackageVipLevel(dataNode.get("musicPackage").get("vipLevel").intValue());
    user.setMusicPackageVipExpireTime(dataNode.get("musicPackage").get("expireTime").longValue());
    user.setMusicPackageVipLevelIcon(dataNode.get("musicPackage").get("iconUrl").textValue());
    user.setRedPlusVipLevel(dataNode.get("redplus").get("vipLevel").intValue());
    user.setRedPlusVipExpireTime(dataNode.get("redplus").get("expireTime").longValue());
    user.setRedPlusVipLevelIcon(dataNode.get("redplus").get("iconUrl").textValue());
  }

  /**
   * Assign last login info to user.
   *
   * @param user Your user info.
   * @param rawLastLoginInfo Raw last login info.
   */
  void assignLastLoginInfo(NCMUser user, String rawLastLoginInfo) {
    JsonNode jsonNode;
    try {
      jsonNode = new ObjectMapper().readTree(rawLastLoginInfo);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    JsonNode profileNode = jsonNode.get("profile");
    user.setLastLoginTime(profileNode.get("lastLoginTime").longValue());
    user.setLastLoginIP(profileNode.get("lastLoginIP").textValue());
  }
}
