package com.daw.pms.Service.QQMusic.impl;

import com.daw.pms.Config.QQMusicAPI;
import com.daw.pms.Entity.QQMusic.QQMusicUser;
import com.daw.pms.Service.QQMusic.QQMusicUserService;
import com.daw.pms.Utils.HttpTools;
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
public class QQMusicUserServiceImpl implements QQMusicUserService {
  private final HttpTools httpTools;
  private final String baseUrl;

  /**
   * Constructor for QQMusicUserServiceImpl.
   *
   * @param httpTools a {@link com.daw.pms.Utils.HttpTools} object.
   */
  public QQMusicUserServiceImpl(HttpTools httpTools) {
    this.httpTools = httpTools;
    this.baseUrl = httpTools.qqmusicHost + ":" + httpTools.qqmusicPort;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Return the user info for your qq music.
   *
   * @apiNote GET /user/detail?id={@code qid}
   */
  @Override
  public QQMusicUser getUserInfo(Long qid, String cookie) {
    QQMusicUser qqMusicUser =
        extractQQMusicUser(
            httpTools.requestGetAPI(
                baseUrl + QQMusicAPI.GET_USERINFO,
                new HashMap<String, String>() {
                  {
                    put("id", qid.toString());
                  }
                },
                Optional.of(cookie)));
    qqMusicUser.setQqNumber(qid);
    return qqMusicUser;
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
    JsonNode creatorNode;
    try {
      creatorNode = jsonNode.get("data").get("creator");
    } catch (Exception e) {
      throw new RuntimeException(
          jsonNode.get("errMsg").textValue() + "：" + jsonNode.get("info").textValue());
    }

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
