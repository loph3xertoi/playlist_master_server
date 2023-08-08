package com.daw.pms.Service.Bilibili.impl;

import com.daw.pms.Config.BilibiliAPI;
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
import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class BiliResourceServiceImpl implements BiliResourceService {
  private final HttpTools httpTools;
  private final BiliCookieService biliCookieService;

  public BiliResourceServiceImpl(HttpTools httpTools, BiliCookieService biliCookieService) {
    this.httpTools = httpTools;
    this.biliCookieService = biliCookieService;
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
        resource.setLinks((Map<String, Map<String, String>>) linksResult.getData());
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
    boolean isSeasonResource = !ugcSeasonNode.isNull();
    JsonNode pagesNode = dataNode.get("pages");
    JsonNode upperNode = dataNode.get("owner");
    JsonNode stateNode = dataNode.get("stat");
    BiliDetailResource data = new BiliDetailResource();
    data.setId(dataNode.get("aid").longValue());
    data.setBvid(dataNode.get("bvid").textValue());
    data.setCid(dataNode.get("cid").longValue());
    List<BiliSubpageOfResource> pages = new ArrayList<>();
    if (isSeasonResource) {
      data.setTitle(ugcSeasonNode.get("title").textValue());
      data.setCover(ugcSeasonNode.get("cover").textValue());
      data.setPage(ugcSeasonNode.get("ep_count").intValue());
      JsonNode episodesNode = ugcSeasonNode.get("sections").get(0).get("episodes");
      for (int i = 0; i < episodesNode.size(); i++) {
        JsonNode episodeNode = episodesNode.get(i);
        JsonNode pageNode = episodeNode.get("page");
        BiliSubpageOfResource subpage = new BiliSubpageOfResource();
        subpage.setCid(pageNode.get("cid").longValue());
        subpage.setPage(i + 1);
        subpage.setPartName(pageNode.get("part").textValue());
        subpage.setDuration(pageNode.get("duration").intValue());
        subpage.setWidth(pageNode.get("dimension").get("width").intValue());
        subpage.setHeight(pageNode.get("dimension").get("height").intValue());
        pages.add(subpage);
      }
      data.setSubpages(pages);
    } else {
      data.setTitle(dataNode.get("title").textValue());
      data.setCover(dataNode.get("pic").textValue());
      data.setPage(dataNode.get("videos").intValue());
      for (JsonNode pageNode : pagesNode) {
        BiliSubpageOfResource subpage = new BiliSubpageOfResource();
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
    data.setPlayCount(stateNode.get("view").longValue());
    data.setDanmakuCount(stateNode.get("danmaku").longValue());
    data.setCollectedCount(stateNode.get("favorite").longValue());
    data.setCommentCount(stateNode.get("reply").longValue());
    data.setCoinsCount(stateNode.get("coin").longValue());
    data.setSharedCount(stateNode.get("share").longValue());
    data.setLikedCount(stateNode.get("like").longValue());
    data.setIntro(dataNode.get("desc").textValue());
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
   * @return The links of this video, wrapped with Result DTO, the data is Map<String, Map<String,
   *     String>>, the key is "video" for video without sound and "audio" for audio only, The value
   *     is a map that the key is resource code and the value is the real link of corresponding
   *     audio or video, specific code see {@link <a
   *     href="https://socialsisteryi.github.io/bilibili-API-collect/docs/bangumi/videostream_url.html#qn%E8%A7%86%E9%A2%91%E6%B8%85%E6%99%B0%E5%BA%A6%E6%A0%87%E8%AF%86>Video
   *     code</a>}
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
    params.put("platform", "html");
    // Set the high quality, necessary for links with dash format.
    params.put("high_quality", 1);
    String rawResourceDashLinks =
        httpTools.requestGetAPI(BilibiliAPI.GET_RESOURCE_DASH_LINKS, params, Optional.of(cookie));
    return extractDashLinks(rawResourceDashLinks);
  }

  private Result extractDashLinks(String rawResourceDashLinks) {
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
      Map<String, Map<String, String>> data = new HashMap<>();
      Map<String, String> videoLinks = new HashMap<>();
      Map<String, String> audioLinks = new HashMap<>();
      videosNode.forEach(
          videoNode ->
              videoLinks.put(videoNode.get("id").asText(), videoNode.get("baseUrl").textValue()));
      audiosNode.forEach(
          audioNode ->
              audioLinks.put(audioNode.get("id").asText(), audioNode.get("baseUrl").textValue()));
      data.put("video", videoLinks);
      data.put("audio", audioLinks);
      return Result.ok(data);
    } else {
      throw new RuntimeException(jsonNode.get("message").textValue());
    }
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
