package com.daw.pms.Service.Bilibili.impl;

import com.daw.pms.Config.BilibiliAPI;
import com.daw.pms.DTO.Result;
import com.daw.pms.Entity.Bilibili.BiliDetailResource;
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
      return Result.fail(jsonNode.get("message").textValue());
    }
    JsonNode dataNode = jsonNode.get("data");
    JsonNode upperNode = dataNode.get("owner");
    JsonNode stateNode = dataNode.get("stat");
    JsonNode pagesNode = dataNode.get("pages");
    BiliDetailResource data = new BiliDetailResource();
    data.setId(dataNode.get("aid").longValue());
    data.setBvid(dataNode.get("bvid").textValue());
    data.setCid(dataNode.get("cid").longValue());
    data.setTitle(dataNode.get("title").textValue());
    data.setCover(dataNode.get("pic").textValue());
    data.setPage(dataNode.get("videos").intValue());
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
    List<BiliSubpageOfResource> pages = new ArrayList<>();
    for (JsonNode pageNode : pagesNode) {
      BiliSubpageOfResource subpage = new BiliSubpageOfResource();
      subpage.setCid(pageNode.get("cid").longValue());
      subpage.setPage(pageNode.get("page").intValue());
      subpage.setPartName(pageNode.get("part").textValue());
      subpage.setDuration(pageNode.get("duration").intValue());
      subpage.setWidth(pageNode.get("dimension").get("width").intValue());
      subpage.setHeight(pageNode.get("dimension").get("height").intValue());
      subpage.setFirstFrame(pageNode.get("first_frame").textValue());
      pages.add(subpage);
    }
    data.setSubpages(pages);
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
    Result result;
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
      result = Result.ok(data);
    } else {
      result = Result.fail(jsonNode.get("message").textValue());
    }
    return result;
  }
}
