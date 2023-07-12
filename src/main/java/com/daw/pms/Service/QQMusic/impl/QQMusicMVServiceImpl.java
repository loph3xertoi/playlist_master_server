package com.daw.pms.Service.QQMusic.impl;

import com.daw.pms.Config.QQMusicAPI;
import com.daw.pms.Entity.Basic.BasicSinger;
import com.daw.pms.Entity.Basic.BasicVideo;
import com.daw.pms.Entity.QQMusic.QQMusicDetailVideo;
import com.daw.pms.Entity.QQMusic.QQMusicSinger;
import com.daw.pms.Entity.QQMusic.QQMusicVideo;
import com.daw.pms.Service.QQMusic.QQMusicMVService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
   * Get detail video information according to its {@code vid}.
   *
   * @param vid The vid of the mv.
   * @param cookie Your qq music cookie.
   * @return The detail information of the mv {@code vid}, links needs to be completed.
   * @apiNote GET /mv?id={@code vid}
   */
  @Override
  public QQMusicDetailVideo getDetailMV(String vid, String cookie) {
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
   * Resolve raw detail video information.
   *
   * @param rawMV Raw mv info returned by qq music api.
   * @return Detail video information of the mv.
   */
  private QQMusicDetailVideo extractQQMusicMV(String rawMV) {
    QQMusicDetailVideo detailVideo = new QQMusicDetailVideo();
    JsonNode jsonNode;
    ArrayList<BasicSinger> singers = new ArrayList<>();
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
              setId(singerNode.get("id").intValue());
              setMid(singerNode.get("mid").textValue());
              setName(singerNode.get("name").textValue());
              setHeadPic(singerNode.get("picurl").textValue());
            }
          });
    }
    detailVideo.setName(infoNode.get("name").textValue());
    detailVideo.setCover(infoNode.get("cover_pic").textValue());
    detailVideo.setSingers(singers);
    detailVideo.setPubDate(infoNode.get("pubdate").intValue());
    detailVideo.setVid(infoNode.get("vid").textValue());
    detailVideo.setDuration(infoNode.get("duration").intValue());
    detailVideo.setPlayCnt(infoNode.get("playcnt").intValue());
    detailVideo.setDesc(infoNode.get("desc").textValue());
    return detailVideo;
  }

  /**
   * Extract rawMVLinks into a map of vid and a list of urls.
   *
   * @param rawMVLinks Raw mv links returned by qq music api.
   * @return A map of vid and a list of urls.
   */
  private Map<String, List<String>> extractQQMusicMVLinks(String rawMVLinks) {
    Map<String, List<String>> mvLinks;
    TypeReference<Map<String, List<String>>> typeRef =
        new TypeReference<Map<String, List<String>>>() {};
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(rawMVLinks);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    JsonNode mapNode = jsonNode.get("data");
    mvLinks = objectMapper.convertValue(mapNode, typeRef);
    return mvLinks;
  }

  /**
   * Get all the related basic videos information according to its {@code songId}.
   *
   * @param songId The song's id.
   * @param cookie Your qq music cookie.
   * @return A list of basic video information related to the song.
   */
  @Override
  public List<BasicVideo> getRelatedVideos(Integer songId, String cookie) {
    return extractRelatedMV(
        requestGetAPI(
            QQMusicAPI.GET_RELATED_MV,
            new HashMap<String, Integer>() {
              {
                put("id", songId);
              }
            },
            Optional.of(cookie)));
  }

  private List<BasicVideo> extractRelatedMV(String rawRelatedMVs) {
    List<BasicVideo> relatedMVs = new ArrayList<>();
    TypeReference<List<JsonNode>> typeRef = new TypeReference<List<JsonNode>>() {};
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(rawRelatedMVs);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    JsonNode mapNode = jsonNode.get("data");
    List<JsonNode> jsonNodeList = objectMapper.convertValue(mapNode, typeRef);
    jsonNodeList.forEach(
        videoNode ->
            relatedMVs.add(
                new QQMusicVideo() {
                  {
                    setName(videoNode.get("title").textValue());
                    setCover(videoNode.get("picurl").textValue());
                    setSingers(
                        new ArrayList<BasicSinger>() {
                          {
                            List<JsonNode> jsonNodeList =
                                objectMapper.convertValue(videoNode.get("singers"), typeRef);
                            jsonNodeList.forEach(
                                singerNode ->
                                    add(
                                        new QQMusicSinger() {
                                          {
                                            setName(singerNode.get("name").textValue());
                                            setHeadPic(singerNode.get("picurl").textValue());
                                            setId(singerNode.get("id").intValue());
                                            setMid(singerNode.get("mid").textValue());
                                          }
                                        }));
                          }
                        });
                    setVid(videoNode.get("vid").textValue());
                    setPlayCnt(videoNode.get("playcnt").intValue());
                  }
                }));
    return relatedMVs;
  }
}
