package com.daw.pms.Service.QQMusic.impl;

import com.daw.pms.Config.QQMusicAPI;
import com.daw.pms.Entity.Basic.BasicSinger;
import com.daw.pms.Entity.Basic.BasicVideo;
import com.daw.pms.Entity.QQMusic.QQMusicDetailVideo;
import com.daw.pms.Entity.QQMusic.QQMusicSinger;
import com.daw.pms.Entity.QQMusic.QQMusicVideo;
import com.daw.pms.Service.QQMusic.QQMusicMVService;
import com.daw.pms.Utils.HttpTools;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.Serializable;
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
public class QQMusicMVServiceImpl implements QQMusicMVService, Serializable {
  private final HttpTools httpTools;
  private final String baseUrl;

  /**
   * Constructor for QQMusicMVServiceImpl.
   *
   * @param httpTools a {@link com.daw.pms.Utils.HttpTools} object.
   */
  public QQMusicMVServiceImpl(HttpTools httpTools) {
    this.httpTools = httpTools;
    this.baseUrl = "http://" + httpTools.qqmusicHost + ":" + httpTools.qqmusicPort;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Get detail video information according to its {@code vid}.
   *
   * @apiNote GET /mv?id={@code vid}
   */
  @Override
  public QQMusicDetailVideo getDetailMV(String vid, String cookie) {
    QQMusicDetailVideo qqMusicDetailVideo =
        extractQQMusicMV(
            httpTools.requestGetAPI(
                baseUrl + QQMusicAPI.GET_MV_INFO,
                new HashMap<String, String>() {
                  {
                    put("id", vid);
                  }
                },
                Optional.of(cookie)));
    Map<String, List<String>> mVsLink = getMVsLink(vid, cookie);
    qqMusicDetailVideo.setLinks(mVsLink.get(vid));
    return qqMusicDetailVideo;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Get the url for the mv(s) {@code vids}.
   *
   * @apiNote GET /mv/url?id={@code vids}
   */
  @Override
  public Map<String, List<String>> getMVsLink(String vids, String cookie) {
    return extractQQMusicMVLinks(
        httpTools.requestGetAPI(
            baseUrl + QQMusicAPI.GET_MV_LINK,
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
   * {@inheritDoc}
   *
   * <p>Get all the related basic videos information according to its {@code songId}.
   */
  @Override
  public List<BasicVideo> getRelatedVideos(Integer songId, String cookie) {
    return extractRelatedMV(
        httpTools.requestGetAPI(
            baseUrl + QQMusicAPI.GET_RELATED_MV,
            new HashMap<String, Integer>() {
              {
                put("id", songId);
              }
            },
            Optional.of(cookie)));
  }

  private List<BasicVideo> extractRelatedMV(String rawRelatedMVs) {
    List<BasicVideo> relatedMVs = new ArrayList<>();
    //    TypeReference<List<JsonNode>> typeRef = new TypeReference<List<JsonNode>>() {};
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(rawRelatedMVs);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    JsonNode mapNode = jsonNode.get("data");
    mapNode.forEach(
        videoNode ->
            relatedMVs.add(
                new QQMusicVideo() {
                  {
                    setName(videoNode.get("title").textValue());
                    setCover(videoNode.get("picurl").textValue());
                    setSingers(
                        new ArrayList<BasicSinger>() {
                          {
                            JsonNode singersNode = videoNode.get("singers");
                            singersNode.forEach(
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
