package com.daw.pms.Service.impl;

import com.daw.pms.Config.QQMusicAPI;
import com.daw.pms.Entity.QQMusicMV;
import com.daw.pms.Entity.QQMusicSinger;
import com.daw.pms.Service.QQMusicMVService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import org.springframework.stereotype.Service;

/**
 * Service for handle mv in qq music.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 6/2/23
 */
@Service
public class QQMusicMVServiceImpl extends QQMusicBase implements QQMusicMVService {
  /**
   * Get mv basic information according to its {@code vid}.
   *
   * @param vid The vid of the mv.
   * @param cookie Your qq music cookie.
   * @return The basic information of the mv {@code vid} wrapped with QQMusicMV.
   * @apiNote GET /mv?id={@code vid}
   */
  @Override
  public QQMusicMV getMVInfo(String vid, String cookie) {
    return extractQQMusicMV(
        requestGetAPI(
            QQMusicAPI.GET_MV_INFO,
            new HashMap<String, String>() {
              {
                put("id", vid);
              }
            },
            Optional.of(cookie)));
  }

  /**
   * Get the url for the mv(s) {@code vids}.
   *
   * @param vids The vid of the mv, multi vid separated by comma.
   * @param cookie Your qq music cookie.
   * @return A map which key is the vid and value is a list of urls of this mv.
   * @apiNote GET /mv/url?id={@code vids}
   */
  @Override
  public Map<String, List<String>> getMVsLink(String vids, String cookie) {
    return extractQQMusicMVLinks(
        requestGetAPI(
            QQMusicAPI.GET_MV_LINK,
            new HashMap<String, String>() {
              {
                put("id", vids);
              }
            },
            Optional.of(cookie)));
  }

  /**
   * Extract rawMV into QQMusicMV.
   *
   * @param rawMV Raw mv info returned by qq music api.
   * @return Extracted QQMusicMV object.
   */
  private QQMusicMV extractQQMusicMV(String rawMV) {
    QQMusicMV qqMusicMV = new QQMusicMV();
    JsonNode jsonNode;
    ArrayList<QQMusicSinger> singers = new ArrayList<>();
    try {
      jsonNode = new ObjectMapper().readTree(rawMV);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    JsonNode infoNode = jsonNode.get("data").get("info");
    JsonNode singersNode = infoNode.get("singers");
    for (JsonNode singerNode : singersNode) {
      singers.add(
          new QQMusicSinger() {
            {
              setId(singerNode.get("id").asText());
              setMid(singerNode.get("mid").textValue());
              setName(singerNode.get("name").textValue());
            }
          });
    }
    qqMusicMV.setName(infoNode.get("name").textValue());
    qqMusicMV.setSingers(singers);
    qqMusicMV.setCoverPic(infoNode.get("cover_pic").textValue());
    qqMusicMV.setVid(infoNode.get("vid").textValue());
    qqMusicMV.setDuration(infoNode.get("duration").intValue());
    qqMusicMV.setPlayCnt(infoNode.get("playcnt").intValue());
    qqMusicMV.setDesc(infoNode.get("desc").textValue());
    return qqMusicMV;
  }

  /**
   * Extract rawMVLinks into a map of vid and a list of urls.
   *
   * @param rawMVLinks Raw mv links returned by qq music api.
   * @return A map of vid and a list of urls.
   */
  private Map<String, List<String>> extractQQMusicMVLinks(String rawMVLinks) {
    Map<String, List<String>> mvLinks;
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(rawMVLinks);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    JsonNode mapNode = jsonNode.get("data");
    mvLinks = objectMapper.convertValue(mapNode, Map.class);
    return mvLinks;
  }
}
