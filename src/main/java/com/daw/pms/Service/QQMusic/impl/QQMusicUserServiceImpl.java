package com.daw.pms.Service.QQMusic.impl;

import com.daw.pms.Config.QQMusicAPI;
import com.daw.pms.Entity.QQMusic.QQMusicUser;
import com.daw.pms.Service.QQMusic.QQMusicUserService;
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
   * @param qid Your qq number.
   * @param cookie Your cookie for qq music.
   * @return Your user info for your qq music.
   * @apiNote GET /user/detail?id={@code qid}
   */
  @Override
  public QQMusicUser getUserInfo(String qid, String cookie) {
    return extractQQMusicUser(
        requestGetAPI(
            QQMusicAPI.GET_USERINFO,
            new HashMap<String, String>() {
              {
                put("id", qid);
              }
            },
            Optional.of(cookie)));
  }

  /**
   * Resolve raw user info returned by qq music proxy server.
   *
   * @param rawUserInfo Raw user info.
   * @return Your user info in qq music platform.
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
