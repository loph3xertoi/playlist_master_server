package com.daw.pms.Service.Bilibili.impl;

import com.daw.pms.Config.BilibiliAPI;
import com.daw.pms.DTO.BiliLinksDTO;
import com.daw.pms.DTO.PagedDataDTO;
import com.daw.pms.DTO.Result;
import com.daw.pms.Entity.Bilibili.BiliDetailResource;
import com.daw.pms.Entity.Bilibili.BiliResource;
import com.daw.pms.Entity.Bilibili.BiliSubpageOfResource;
import com.daw.pms.Service.Bilibili.BiliCookieService;
import com.daw.pms.Service.Bilibili.BiliResourceService;
import com.daw.pms.Utils.HttpTools;
import com.daw.pms.Utils.WbiBiliBili;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class BiliResourceServiceImpl implements BiliResourceService {
  @Value("${pms.host}")
  private String pmsHost;

  @Value("${pms.port}")
  private int pmsPort;

  private final HttpTools httpTools;

  private final BiliCookieService biliCookieService;

  private final RedisTemplate<String, String> redisTemplate;

  public BiliResourceServiceImpl(
      HttpTools httpTools,
      BiliCookieService biliCookieService,
      RedisTemplate<String, String> redisTemplate) {
    this.httpTools = httpTools;
    this.biliCookieService = biliCookieService;
    this.redisTemplate = redisTemplate;
  }

  /**
   * Get detail resource of bilibili.
   *
   * @param bvid The bvid of this resource.
   * @param cookie Your cookie for bilibili.
   * @return Detail resource of bilibili, wrapped with Result DTO, the data is BiliDetailResource.
   * @apiNote GET GET_DETAIL_RESOURCE?bvid={@code bvid}&wts=wts&w_rid=w_rid
   */
  @Override
  public Result getDetailResource(String bvid, String cookie) {
    Map<String, Object> params = new HashMap<>();
    if (bvid == null) {
      throw new RuntimeException("Invalid parameters");
    }
    params.put("bvid", bvid);
    Map<String, String> wbiKey = biliCookieService.getWbiKey();
    String signedUri =
        WbiBiliBili.wbiSignRequestParam(params, wbiKey.get("img_key"), wbiKey.get("sub_key"));
    String url = BilibiliAPI.GET_DETAIL_RESOURCE + "?" + signedUri;
    String rawDetailResource = httpTools.requestGetAPIByFinalUrl(url, Optional.of(cookie));
    Result result = extractDetailResource(rawDetailResource);
    if (result.getSuccess()) {
      BiliDetailResource resource = (BiliDetailResource) result.getData();
      Result linksResult = getResourceDashLink(bvid, resource.getCid(), cookie);
      if (linksResult.getSuccess()) {
        resource.setLinks((BiliLinksDTO) linksResult.getData());
      } else {
        throw new RuntimeException(linksResult.getMessage());
      }
    }
    return result;
  }

  private Result extractDetailResource(String rawDetailResource) {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(rawDetailResource);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    int code = jsonNode.get("code").intValue();
    if (code != 0) {
      throw new RuntimeException(jsonNode.get("message").textValue());
    }
    JsonNode dataNode = jsonNode.get("data");
    // The ugcSeasonNode exists only in episodes resource.
    JsonNode ugcSeasonNode = dataNode.get("ugc_season");
    boolean isSeasonResource = ugcSeasonNode != null;
    JsonNode pagesNode = dataNode.get("pages");
    JsonNode upperNode = dataNode.get("owner");
    BiliDetailResource data = new BiliDetailResource();
    data.setId(dataNode.get("aid").longValue());
    data.setBvid(dataNode.get("bvid").textValue());
    data.setAid(dataNode.get("aid").longValue());
    data.setCid(dataNode.get("cid").longValue());
    List<BiliSubpageOfResource> pages = new ArrayList<>();
    if (isSeasonResource) {
      JsonNode stateNode = ugcSeasonNode.get("stat");
      data.setIsSeasonResource(true);
      data.setTitle(ugcSeasonNode.get("title").textValue());
      data.setCover(ugcSeasonNode.get("cover").textValue());
      data.setPage(ugcSeasonNode.get("ep_count").intValue());
      data.setIntro(ugcSeasonNode.get("intro").textValue());
      data.setPlayCount(stateNode.get("view").longValue());
      data.setDanmakuCount(stateNode.get("danmaku").longValue());
      data.setCommentCount(stateNode.get("reply").longValue());
      data.setCollectedCount(stateNode.get("fav").longValue());
      data.setCoinsCount(stateNode.get("coin").longValue());
      data.setSharedCount(stateNode.get("share").longValue());
      data.setLikedCount(stateNode.get("like").longValue());
      JsonNode episodesNode = ugcSeasonNode.get("sections").get(0).get("episodes");
      ArrayList<BiliDetailResource> episodes = new ArrayList<>(data.getPage());
      for (JsonNode episodeNode : episodesNode) {
        JsonNode arcNode = episodeNode.get("arc");
        JsonNode statNode = arcNode.get("stat");
        BiliDetailResource episode = new BiliDetailResource();
        episode.setId(episodeNode.get("id").longValue());
        episode.setBvid(episodeNode.get("bvid").textValue());
        episode.setAid(episodeNode.get("aid").longValue());
        episode.setCid(episodeNode.get("cid").longValue());
        episode.setType(2);
        episode.setTitle(episodeNode.get("title").textValue());
        episode.setCover(arcNode.get("pic").textValue());
        episode.setPage(1);
        episode.setIsSeasonResource(false);
        episode.setDuration(arcNode.get("duration").intValue());
        episode.setUpperName("");
        episode.setUpperMid(0L);
        episode.setUpperHeadPic("");
        episode.setPlayCount(statNode.get("view").longValue());
        episode.setDanmakuCount(statNode.get("danmaku").longValue());
        episode.setCollectedCount(stateNode.get("fav").longValue());
        episode.setCommentCount(stateNode.get("reply").longValue());
        episode.setCoinsCount(stateNode.get("coin").longValue());
        episode.setSharedCount(stateNode.get("share").longValue());
        episode.setLikedCount(stateNode.get("like").longValue());
        episode.setIntro(arcNode.get("desc").textValue());
        episode.setPublishedTime(arcNode.get("pubdate").longValue());
        episode.setCreatedTime(arcNode.get("ctime").longValue());
        episode.setDynamicLabels(arcNode.get("dynamic").textValue());
        episodes.add(episode);
      }
      data.setEpisodes(episodes);
    } else {
      JsonNode stateNode = dataNode.get("stat");
      data.setIsSeasonResource(false);
      data.setTitle(dataNode.get("title").textValue());
      data.setCover(dataNode.get("pic").textValue());
      data.setPage(dataNode.get("videos").intValue());
      data.setIntro(dataNode.get("desc").textValue());
      data.setPlayCount(stateNode.get("view").longValue());
      data.setDanmakuCount(stateNode.get("danmaku").longValue());
      data.setCommentCount(stateNode.get("reply").longValue());
      data.setCollectedCount(stateNode.get("favorite").longValue());
      data.setCoinsCount(stateNode.get("coin").longValue());
      data.setSharedCount(stateNode.get("share").longValue());
      data.setLikedCount(stateNode.get("like").longValue());
      for (JsonNode pageNode : pagesNode) {
        BiliSubpageOfResource subpage = new BiliSubpageOfResource();
        subpage.setBvid(dataNode.get("bvid").textValue());
        subpage.setCid(pageNode.get("cid").longValue());
        subpage.setPage(pageNode.get("page").intValue());
        subpage.setPartName(pageNode.get("part").textValue());
        subpage.setDuration(pageNode.get("duration").intValue());
        subpage.setWidth(pageNode.get("dimension").get("width").intValue());
        subpage.setHeight(pageNode.get("dimension").get("height").intValue());
        pages.add(subpage);
      }
      data.setSubpages(pages);
    }
    data.setDuration(dataNode.get("duration").intValue());
    data.setUpperName(upperNode.get("name").textValue());
    data.setUpperMid(upperNode.get("mid").longValue());
    data.setUpperHeadPic(upperNode.get("face").textValue());
    data.setPublishedTime(dataNode.get("pubdate").longValue());
    data.setCreatedTime(dataNode.get("ctime").longValue());
    data.setDynamicLabels(dataNode.get("dynamic").textValue());
    // TODO: fix this type.
    data.setType(2);
    return Result.ok(data);
  }

  /**
   * Get resource's dash links of bilibili.
   *
   * @param bvid The resource's bvid.
   * @param cid The resource's cid.
   * @param cookie Your cookie for bilibili.
   * @return The links of this resource, wrapped with Result DTO, the data is BiliLinksDTO.
   * @apiNote GET GET_RESOURCE_DASH_LINKS?bvid={@code bvid}&cid={@code
   *     cid}&fnval=16&fourk=1&platform=html&high_quality=1&wts=wts&w_rid=w_rid
   */
  @Override
  public Result getResourceDashLink(String bvid, Long cid, String cookie) {
    Map<String, Object> params = new HashMap<>();
    if (bvid == null || cid == null) {
      throw new RuntimeException("Invalid parameters");
    }
    params.put("bvid", bvid);
    params.put("cid", cid);
    // Get links with dash format.
    params.put("fnval", 16);
    // Enable 4k video.
    params.put("fourk", 1);
    // Set platform to html.
    params.put("platform", "html5");
    // Set the high quality, necessary for links with dash format.
    params.put("high_quality", 1);
    String rawResourceDashLinks =
        httpTools.requestGetAPI(BilibiliAPI.GET_RESOURCE_DASH_LINKS, params, Optional.of(cookie));
    return extractDashLinks(rawResourceDashLinks, bvid, cid);
  }

  private Result extractDashLinks(String rawResourceDashLinks, String bvid, Long cid) {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(rawResourceDashLinks);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    int code = jsonNode.get("code").intValue();
    if (code == 0) {
      JsonNode dataNode = jsonNode.get("data");
      JsonNode dashNode = dataNode.get("dash");
      JsonNode videosNode = dashNode.get("video");
      JsonNode audiosNode = dashNode.get("audio");
      BiliLinksDTO biliLinksDTO = new BiliLinksDTO();
      Map<String, String> videoLinks = new HashMap<>();
      Map<String, String> audioLinks = new HashMap<>();
      videosNode.forEach(
          videoNode ->
              videoLinks.put(videoNode.get("id").asText(), videoNode.get("baseUrl").textValue()));
      audiosNode.forEach(
          audioNode ->
              audioLinks.put(audioNode.get("id").asText(), audioNode.get("baseUrl").textValue()));
      biliLinksDTO.setVideo(videoLinks);
      biliLinksDTO.setAudio(audioLinks);
      String mpdName;
      try {
        mpdName = bvid + "_" + cid + ".mpd";
        generateMpd(jsonNode, mpdName);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
      String mpdLink = "/mpd/" + mpdName;
      biliLinksDTO.setMpd(mpdLink);
      return Result.ok(biliLinksDTO);
    } else {
      throw new RuntimeException(jsonNode.get("message").textValue());
    }
  }

  /** Generate Mpd file according to dash json. */
  private void generateMpd(JsonNode jsonNode, String mpdName) {
    CompletableFuture.runAsync(
        () -> {
          try {
            JsonNode dataNode = jsonNode.get("data");
            JsonNode dashNode = dataNode.get("dash");
            JsonNode videosNode = dashNode.get("video");
            JsonNode audiosNode = dashNode.get("audio");
            long durationSeconds = dashNode.get("duration").longValue();
            long durationMillis = durationSeconds * 1000;
            String duration = Duration.ofMillis(durationMillis).toString();
            double minBufferTimeSeconds = dashNode.get("minBufferTime").doubleValue();
            int minBufferTimeMillis = (int) minBufferTimeSeconds * 1000;
            String minBufferTime = Duration.ofMillis(minBufferTimeMillis).toString();
            XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
            //            xmlOutputFactory.setProperty("escapeCharacters", false);
            StringWriter stringWriter = new StringWriter();

            XMLStreamWriter xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(stringWriter);
            // Start the document
            xmlStreamWriter.writeStartDocument();
            xmlStreamWriter.writeStartElement("MPD");
            xmlStreamWriter.writeAttribute("xmlns", "urn:mpeg:dash:schema:mpd:2011");
            xmlStreamWriter.writeAttribute("type", "static");
            xmlStreamWriter.writeAttribute("mediaPresentationDuration", duration);
            xmlStreamWriter.writeAttribute("minBufferTime", minBufferTime);
            xmlStreamWriter.writeAttribute(
                "profiles", "urn:mpeg:dash:profile:isoff-live:2011,urn:com:dashif:dash264");

            xmlStreamWriter.writeStartElement("Period");
            xmlStreamWriter.writeAttribute("id", "1");
            xmlStreamWriter.writeAttribute("start", "PT0S");

            // Write video adaptation set.
            for (int i = 0; i < videosNode.size(); i++) {
              JsonNode videoNode = videosNode.get(i);
              String minBandwidth = videoNode.get("bandwidth").asText();
              String startWithSap = videoNode.get("startWithSap").asText();
              String initializationRange =
                  videoNode.get("SegmentBase").get("Initialization").textValue();
              String indexRange = videoNode.get("SegmentBase").get("indexRange").textValue();
              String id = videoNode.get("id").asText();
              String sar = videoNode.get("sar").textValue();
              String bandwidth = videoNode.get("bandwidth").asText();
              String width = videoNode.get("width").asText();
              String height = videoNode.get("height").asText();
              String baseUrl = videoNode.get("baseUrl").textValue();
              String codecs = videoNode.get("codecs").textValue();
              xmlStreamWriter.writeStartElement("AdaptationSet");
              xmlStreamWriter.writeAttribute("group", String.valueOf(i + 1));
              xmlStreamWriter.writeAttribute("mimeType", "video/mp4");
              xmlStreamWriter.writeAttribute("minBandwidth", minBandwidth);
              xmlStreamWriter.writeAttribute("maxBandwidth", minBandwidth);
              xmlStreamWriter.writeAttribute("segmentAlignment", "true");
              xmlStreamWriter.writeAttribute("startWithSAP", startWithSap);
              xmlStreamWriter.writeStartElement("SegmentBase");
              xmlStreamWriter.writeAttribute("indexRange", indexRange);
              xmlStreamWriter.writeStartElement("Initialization");
              xmlStreamWriter.writeAttribute("range", initializationRange);
              xmlStreamWriter.writeEndElement();
              xmlStreamWriter.writeEndElement();
              xmlStreamWriter.writeStartElement("Representation");
              xmlStreamWriter.writeAttribute("id", id);
              xmlStreamWriter.writeAttribute("sar", sar);
              xmlStreamWriter.writeAttribute("bandwidth", bandwidth);
              xmlStreamWriter.writeAttribute("width", width);
              xmlStreamWriter.writeAttribute("height", height);
              xmlStreamWriter.writeAttribute("frameRate", "30");
              xmlStreamWriter.writeAttribute("codecs", codecs);
              xmlStreamWriter.writeStartElement("BaseURL");
              xmlStreamWriter.writeCharacters(baseUrl);
              xmlStreamWriter.writeEndElement();
              xmlStreamWriter.writeEndElement();
              xmlStreamWriter.writeEndElement();
            }

            // Write audio adaptation set.
            for (int i = 0; i < audiosNode.size(); i++) {
              JsonNode audioNode = audiosNode.get(i);
              String minBandwidth = audioNode.get("bandwidth").asText();
              String startWithSap = audioNode.get("startWithSap").asText();
              String initializationRange =
                  audioNode.get("SegmentBase").get("Initialization").textValue();
              String indexRange = audioNode.get("SegmentBase").get("indexRange").textValue();
              String id = audioNode.get("id").asText();
              String bandwidth = audioNode.get("bandwidth").asText();
              String baseUrl = audioNode.get("baseUrl").textValue();
              String codecs = audioNode.get("codecs").textValue();
              xmlStreamWriter.writeStartElement("AdaptationSet");
              xmlStreamWriter.writeAttribute("group", String.valueOf(i + 1 + videosNode.size()));
              xmlStreamWriter.writeAttribute("mimeType", "audio/mp4");
              xmlStreamWriter.writeAttribute("minBandwidth", minBandwidth);
              xmlStreamWriter.writeAttribute("maxBandwidth", minBandwidth);
              xmlStreamWriter.writeAttribute("segmentAlignment", "true");
              xmlStreamWriter.writeAttribute("startWithSAP", startWithSap);
              xmlStreamWriter.writeStartElement("SegmentBase");
              xmlStreamWriter.writeAttribute("indexRange", indexRange);
              xmlStreamWriter.writeStartElement("Initialization");
              xmlStreamWriter.writeAttribute("range", initializationRange);
              xmlStreamWriter.writeEndElement();
              xmlStreamWriter.writeEndElement();
              xmlStreamWriter.writeStartElement("Representation");
              xmlStreamWriter.writeAttribute("id", id);
              xmlStreamWriter.writeAttribute("bandwidth", bandwidth);
              xmlStreamWriter.writeAttribute("codecs", codecs);
              xmlStreamWriter.writeAttribute("width", "0");
              xmlStreamWriter.writeAttribute("height", "0");
              xmlStreamWriter.writeStartElement("BaseURL");
              xmlStreamWriter.writeCharacters(baseUrl);
              xmlStreamWriter.writeEndElement();
              xmlStreamWriter.writeEndElement();
              xmlStreamWriter.writeEndElement();
            }

            // End the document
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeEndDocument();
            xmlStreamWriter.flush();
            xmlStreamWriter.close();

            redisTemplate.opsForValue().set(mpdName, stringWriter.toString());
            redisTemplate.expire(mpdName, 2, TimeUnit.HOURS);
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        });
  }

  /**
   * Search resources in bilibili.
   *
   * @param searchType The type of search, can be video, media_bangumi and so on.
   * @param keyword The keyword to search.
   * @param order The sorting order of searched result, totalrank: by overall, click: by click
   *     times, pubdate: by publish date, dm: by danmaku number, stow: by collected times, scores:
   *     by comments number.
   * @param duration Filter the result by duration, 0: all duration, 1: less than 10 minutes, 2: 10
   *     to 30 minutes, 3: 30 to 60 minutes, 4: more than 60 minutes.
   * @param tids Filter by the section number, default is 0(all sections).
   * @param page The page number.
   * @param cookie Your cookie for bilibili.
   * @return Searched resources wrapped with Result DTO, the data is PagedDataDTO<BiliResource>.
   */
  // String keyword, Integer offset, Integer limit, Integer type, String cookie
  @Override
  public Result searchResources(
      String searchType,
      String keyword,
      String order,
      Integer duration,
      Integer tids,
      Integer page,
      String cookie) {
    Map<String, Object> params = new HashMap<>();
    if (keyword == null || page == null) {
      throw new RuntimeException("Invalid parameters");
    }
    params.put("search_type", searchType == null ? "video" : searchType);
    params.put("keyword", keyword);
    params.put("order", order == null ? "totalrank" : order);
    params.put("duration", duration == null ? 0 : duration);
    params.put("tids", tids == null ? 0 : tids);
    params.put("page", page);
    Map<String, String> wbiKey = biliCookieService.getWbiKey();
    String signedUri =
        WbiBiliBili.wbiSignRequestParam(params, wbiKey.get("img_key"), wbiKey.get("sub_key"));
    String url = BilibiliAPI.SEARCH_RESOURCES + "?" + signedUri;
    String rawSearchedResources = httpTools.requestGetAPIByFinalUrl(url, Optional.of(cookie));
    return extractSearchedResources(rawSearchedResources);
  }

  private Result extractSearchedResources(String rawSearchedResources) {
    PagedDataDTO<BiliResource> pagedSongs = new PagedDataDTO<>();
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(rawSearchedResources);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    int resultCode = jsonNode.get("code").intValue();
    if (resultCode != 0) {
      String errorMsg = jsonNode.get("message").textValue();
      throw new RuntimeException(errorMsg);
    }
    JsonNode dataNode = jsonNode.get("data");
    int totalCount = dataNode.get("numResults").intValue();
    pagedSongs.setCount(totalCount);
    int currentPage = dataNode.get("page").intValue();
    int numPages = dataNode.get("numPages").intValue();
    pagedSongs.setHasMore(currentPage < numPages);
    JsonNode resultNode = dataNode.get("result");
    List<BiliResource> resources = new ArrayList<>();
    for (JsonNode resourceNode : resultNode) {
      BiliResource resource = new BiliResource();
      resource.setId(resourceNode.get("id").longValue());
      resource.setBvid(resourceNode.get("bvid").textValue());
      resource.setTitle(resourceNode.get("title").textValue());
      resource.setCover(resourceNode.get("pic").textValue());
      resource.setType(2);
      resource.setPage(1);
      String durationStr = resourceNode.get("duration").textValue();
      Integer durationInSecs =
          Integer.parseInt(durationStr.split(":")[0]) * 60
              + Integer.parseInt(durationStr.split(":")[1]);
      resource.setDuration(durationInSecs);
      resource.setUpperName(resourceNode.get("author").textValue());
      resource.setPlayCount(resourceNode.get("play").longValue());
      resource.setDanmakuCount(resourceNode.get("danmaku").longValue());
      resources.add(resource);
    }
    pagedSongs.setList(resources);
    return Result.ok(pagedSongs);
  }
}
