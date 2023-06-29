package com.daw.pms.Service.impl;

import com.daw.pms.Config.QQMusicAPI;
import com.daw.pms.Entity.QQMusicUser;
import com.daw.pms.Service.QQMusicUserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Service for handle user info of qq music.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 6/1/23
 */
@Service
public class QQMusicUserServiceImpl extends QQMusicBase implements QQMusicUserService {
  /**
   * Return the user info for your qq music.
   *
   * @param id Your qq number.
   * @param cookie Your cookie for qq music.
   * @return Your user info for your qq music.
   * @apiNote GET /user/detail?id={@code id}
   */
  @Override
  public QQMusicUser getUserInfo(String id, String cookie) {
    return extractQQMusicUser(
        requestGetAPI(
            QQMusicAPI.GET_USERINFO,
            new HashMap<String, String>() {
              {
                put("id", id);
              }
            },
            Optional.of(cookie)));
  }

  /**
   * Transform raw user info string into QQMusicUser.
   *
   * @param rawUserInfo Raw user info string.
   * @return QQMusicUser for your qq music user.
   */
  private QQMusicUser extractQQMusicUser(String rawUserInfo) {
    JsonNode jsonNode;
    try {
      jsonNode = new ObjectMapper().readTree(rawUserInfo);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    QQMusicUser qqMusicUser = new QQMusicUser();
    JsonNode creatorNode = jsonNode.get("data").get("creator");

    qqMusicUser.setName(creatorNode.get("nick").textValue());
    qqMusicUser.setHeadPic(creatorNode.get("headpic").textValue());
    qqMusicUser.setLvPic(
        creatorNode.get("userInfoUI").get("iconlist").get(0).get("srcUrl").textValue());
    qqMusicUser.setListenPic(creatorNode.get("listeninfo").get("iconurl").textValue());
    qqMusicUser.setBgPic(creatorNode.get("backpic").get("picurl").textValue());
    qqMusicUser.setVisitorNum(creatorNode.get("nums").get("visitornum").intValue());
    qqMusicUser.setFansNum(creatorNode.get("nums").get("fansnum").intValue());
    qqMusicUser.setFollowNum(creatorNode.get("nums").get("follownum").intValue());
    qqMusicUser.setFriendsNum(creatorNode.get("nums").get("frdnum").intValue());
    return qqMusicUser;
  }
}
