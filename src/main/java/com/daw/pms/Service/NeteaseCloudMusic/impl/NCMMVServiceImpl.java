package com.daw.pms.Service.NeteaseCloudMusic.impl;

import com.daw.pms.Config.NCMAPI;
import com.daw.pms.Entity.Basic.BasicSinger;
import com.daw.pms.Entity.Basic.BasicVideo;
import com.daw.pms.Entity.NeteaseCloudMusic.NCMDetailVideo;
import com.daw.pms.Entity.NeteaseCloudMusic.NCMSinger;
import com.daw.pms.Entity.NeteaseCloudMusic.NCMVideo;
import com.daw.pms.Service.NeteaseCloudMusic.NCMMVService;
import com.daw.pms.Utils.HttpTools;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * Service for handle mv in ncm.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 7/24/23
 */
@Service
public class NCMMVServiceImpl implements NCMMVService {
  private final HttpTools httpTools;
  private final String baseUrl;

  /**
   * Constructor for NCMMVServiceImpl.
   *
   * @param httpTools a {@link com.daw.pms.Utils.HttpTools} object.
   */
  public NCMMVServiceImpl(HttpTools httpTools) {
    this.httpTools = httpTools;
    this.baseUrl = "http://" + httpTools.ncmHost + ":" + httpTools.ncmPort;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Get detail video information according to its {@code id}.
   *
   * @apiNote mvid: GET /mv/detail?mvid={@code id} | mlogId: GET /video/detail?id={@code id}
   */
  @Override
  public NCMDetailVideo getDetailMV(String id, String cookie) {
    Boolean isMLog = Pattern.matches(".*[a-zA-Z].*", id);
    String url = isMLog ? baseUrl + NCMAPI.VIDEO_DETAIL : baseUrl + NCMAPI.MV_DETAIL;
    Map<String, String> parameters = new HashMap<>();
    if (isMLog) {
      String vid = convertMLogIdToVid(id, cookie);
      parameters.put("id", vid);
    } else {
      parameters.put("mvid", id);
    }
    String rawDetailVideo = httpTools.requestGetAPI(url, parameters, Optional.of(cookie));
    NCMDetailVideo ncmDetailVideo = extractDetailVideo(rawDetailVideo, isMLog);
    // Set links.
    if (isMLog) {
      ncmDetailVideo.setLinks(getMLogLinks(id, cookie));
    } else {
      Map<String, Integer> rates = ncmDetailVideo.getRates();
      Map<String, String> links =
          rates.keySet().stream()
              .collect(
                  Collectors.toMap(
                      rate -> rate,
                      rate -> getMVLink(ncmDetailVideo.getId(), Integer.parseInt(rate), cookie)));
      ncmDetailVideo.setLinks(links);
    }
    return ncmDetailVideo;
  }

  private NCMDetailVideo extractDetailVideo(String rawDetailVideo, Boolean isMLog) {
    NCMDetailVideo ncmDetailVideo = new NCMDetailVideo();
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(rawDetailVideo);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    JsonNode dataNode = jsonNode.get("data");
    if (isMLog) {
      ncmDetailVideo.setName(dataNode.get("title").textValue());
      ncmDetailVideo.setCover(dataNode.get("coverUrl").textValue());

      List<BasicSinger> singers = new ArrayList<>();
      JsonNode creatorNode = dataNode.get("creator");
      NCMSinger singer = new NCMSinger();
      singer.setId(creatorNode.get("userId").longValue());
      singer.setName(creatorNode.get("nickname").textValue());
      singer.setHeadPic(creatorNode.get("avatarUrl").textValue());
      singers.add(singer);
      ncmDetailVideo.setSingers(singers);

      ncmDetailVideo.setId(dataNode.get("vid").textValue());
      ncmDetailVideo.setDesc(dataNode.get("description").textValue());
      ncmDetailVideo.setPlayCount(dataNode.get("playTime").intValue());
      ncmDetailVideo.setSubCount(dataNode.get("subscribeCount").intValue());
      ncmDetailVideo.setShareCount(dataNode.get("shareCount").intValue());
      ncmDetailVideo.setCommentCount(dataNode.get("commentCount").intValue());
      ncmDetailVideo.setDuration(dataNode.get("durationms").intValue());
      ncmDetailVideo.setPublishTime(dataNode.get("publishTime").asText());

      HashMap<String, Integer> rates = new HashMap<>();
      JsonNode ratesNode = dataNode.get("resolutions");
      for (JsonNode rateNode : ratesNode) {
        rates.put(rateNode.get("resolution").asText(), rateNode.get("size").intValue());
      }
      ncmDetailVideo.setRates(rates);
    } else {
      ncmDetailVideo.setName(dataNode.get("name").textValue());
      ncmDetailVideo.setCover(dataNode.get("cover").textValue());

      List<BasicSinger> singers = new ArrayList<>();
      JsonNode singersNode = dataNode.get("artists");
      for (JsonNode singerNode : singersNode) {
        NCMSinger singer = new NCMSinger();
        singer.setId(singerNode.get("id").longValue());
        singer.setName(singerNode.get("name").textValue());
        singer.setHeadPic(singerNode.get("img1v1Url").textValue());
        singers.add(singer);
      }
      ncmDetailVideo.setSingers(singers);

      ncmDetailVideo.setId(dataNode.get("id").asText());
      ncmDetailVideo.setDesc(dataNode.get("desc").textValue());
      ncmDetailVideo.setPlayCount(dataNode.get("playCount").intValue());
      ncmDetailVideo.setSubCount(dataNode.get("subCount").intValue());
      ncmDetailVideo.setShareCount(dataNode.get("shareCount").intValue());
      ncmDetailVideo.setCommentCount(dataNode.get("commentCount").intValue());
      ncmDetailVideo.setDuration(dataNode.get("duration").intValue());
      ncmDetailVideo.setPublishTime(dataNode.get("publishTime").textValue());

      HashMap<String, Integer> rates = new HashMap<>();
      JsonNode ratesNode = dataNode.get("brs");
      for (JsonNode rateNode : ratesNode) {
        rates.put(rateNode.get("br").asText(), rateNode.get("size").intValue());
      }
      ncmDetailVideo.setRates(rates);
    }
    return ncmDetailVideo;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Get the url of the mv.
   *
   * @apiNote GET /mv/url?id={@code mvId}&amp;r={@code rate}
   */
  @Override
  public String getMVLink(String mvId, Integer rate, String cookie) {
    return extractMVLink(
        httpTools.requestGetAPI(
            baseUrl + NCMAPI.MV_URL,
            new HashMap<String, String>() {
              {
                put("id", mvId);
                put("r", rate.toString());
              }
            },
            Optional.of(cookie)));
  }

  private String extractMVLink(String rawMVLink) {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(rawMVLink);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    return jsonNode.get("data").get("url").textValue();
  }

  /**
   * {@inheritDoc}
   *
   * <p>Get mlog's links.
   *
   * @apiNote GET /mlog/url?id={@code mLogId}
   */
  @Override
  public Map<String, String> getMLogLinks(String mLogId, String cookie) {
    return extractMLogLinks(
        httpTools.requestGetAPI(
            baseUrl + NCMAPI.MLOG_URL,
            new HashMap<String, String>() {
              {
                put("id", mLogId);
              }
            },
            Optional.of(cookie)));
  }

  private Map<String, String> extractMLogLinks(String rawMLogLinks) {
    Map<String, String> links = new HashMap<>();
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(rawMLogLinks);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    JsonNode urlInfosNode =
        jsonNode.get("data").get("resource").get("content").get("video").get("urlInfos");
    for (JsonNode urlInfoNode : urlInfosNode) {
      links.put(urlInfoNode.get("r").asText(), urlInfoNode.get("url").textValue());
    }
    return links;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Get all related videos with song {@code songId}.
   *
   * @apiNote GET /mlog/music/rcmd?songid={@code songId}&amp;mvid={@code mvId}&amp;limit={@code
   *     limit}
   */
  @Override
  public List<BasicVideo> getRelatedVideos(Long songId, String mvId, Integer limit, String cookie) {
    return extractRelatedVideos(
        httpTools.requestGetAPI(
            baseUrl + NCMAPI.RELATED_VIDEOS,
            new HashMap<String, String>() {
              {
                put("songid", songId.toString());
                if (mvId != null) put("mvid", mvId);
                put("limit", limit.toString());
              }
            },
            Optional.of(cookie)));
  }

  private List<BasicVideo> extractRelatedVideos(String rawRelatedVideos) {
    List<BasicVideo> relatedVideos = new ArrayList<>();
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(rawRelatedVideos);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    JsonNode videosNode = jsonNode.get("data").get("feeds");
    for (JsonNode videoNode : videosNode) {
      JsonNode resourceNode = videoNode.get("resource");
      JsonNode mlogBaseDataNode = resourceNode.get("mlogBaseData");
      JsonNode mlogExtVONode = resourceNode.get("mlogExtVO");
      JsonNode userProfileNode = resourceNode.get("userProfile");
      NCMVideo ncmVideo = new NCMVideo();
      ncmVideo.setId(videoNode.get("id").textValue());
      ncmVideo.setName(mlogBaseDataNode.get("text").textValue());

      List<BasicSinger> singers = new ArrayList<>();
      if (!userProfileNode.isNull()) {
        NCMSinger singer = new NCMSinger();
        singer.setId(userProfileNode.get("userId").longValue());
        singer.setName(userProfileNode.get("nickname").textValue());
        singer.setHeadPic(userProfileNode.get("avatarUrl").textValue());
        singers.add(singer);
      } else {
        JsonNode artistsNode = mlogExtVONode.get("artists");
        for (JsonNode singerNode : artistsNode) {
          NCMSinger singer = new NCMSinger();
          singer.setId(singerNode.get("id").longValue());
          singer.setName(singerNode.get("name").textValue());
          JsonNode mlogUserNode = singerNode.get("mlogUser");
          String headPic =
              mlogUserNode.isNull()
                  ? singerNode.get("img1v1Url").textValue()
                  : mlogUserNode.get("avatarUrl").textValue();
          singer.setHeadPic(headPic);
          singers.add(singer);
        }
      }
      ncmVideo.setSingers(singers);

      ncmVideo.setCover(mlogBaseDataNode.get("coverUrl").textValue());
      ncmVideo.setPlayCount(mlogExtVONode.get("playCount").intValue());
      ncmVideo.setDuration(mlogBaseDataNode.get("duration").intValue());
      ncmVideo.setPublishTime(mlogBaseDataNode.get("pubTime").asText());
      relatedVideos.add(ncmVideo);
    }
    return relatedVideos;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Convert mlog id to vid.
   */
  public String convertMLogIdToVid(String mLogId, String cookie) {
    String rawVid =
        httpTools.requestGetAPI(
            baseUrl + NCMAPI.MLOG_TO_VIDEO,
            new HashMap<String, String>() {
              {
                put("id", mLogId);
              }
            },
            Optional.of(cookie));
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(rawVid);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    return jsonNode.get("data").textValue();
  }
}
