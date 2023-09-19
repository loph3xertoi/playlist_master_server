package com.daw.pms.Service.BiliBili.impl;

import com.daw.pms.Config.BilibiliAPI;
import com.daw.pms.DTO.PagedDataDTO;
import com.daw.pms.DTO.Result;
import com.daw.pms.Entity.Bilibili.BiliDetailFavList;
import com.daw.pms.Entity.Bilibili.BiliFavList;
import com.daw.pms.Entity.Bilibili.BiliResource;
import com.daw.pms.Service.BiliBili.BiliFavListService;
import com.daw.pms.Utils.HttpTools;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Service for handling fav lists of bilibili.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 8/2/23
 */
@Service
public class BiliFavListServiceImpl implements BiliFavListService {

  private final HttpTools httpTools;

  /**
   * Constructor for BiliFavListServiceImpl.
   *
   * @param httpTools a {@link com.daw.pms.Utils.HttpTools} object.
   */
  public BiliFavListServiceImpl(HttpTools httpTools) {
    this.httpTools = httpTools;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Get all fav lists of bilibili.
   */
  public Result getFavLists(
      Integer pn, Integer ps, Long mid, String platform, Integer type, String cookie) {
    Map<String, Object> params = new HashMap<>();
    String url;
    if (pn == null || ps == null || type == null) {
      throw new RuntimeException("Invalid parameters");
    }
    if (platform == null) {
      platform = "web";
    }
    if (type == 0) {
      url = BilibiliAPI.GET_CREATED_FAV_LISTS;
    } else if (type == 1) {
      url = BilibiliAPI.GET_COLLECTED_FAV_LISTS;
    } else {
      throw new RuntimeException("type must be 0 or 1");
    }
    params.put("pn", pn);
    params.put("ps", ps);
    params.put("up_mid", mid);
    params.put("platform", platform);
    String rawFavLists = httpTools.requestGetAPI(url, params, Optional.of(cookie));
    return extractFavLists(rawFavLists, type);
  }

  private Result extractFavLists(String rawFavLists, int type) {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(rawFavLists);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    int code = jsonNode.get("code").intValue();
    if (code != 0) {
      throw new RuntimeException("BiliBili error: " + jsonNode.get("message").textValue());
    }
    JsonNode dataNode = jsonNode.get("data");
    JsonNode listNode = dataNode.get("list");
    PagedDataDTO<BiliFavList> data = new PagedDataDTO<>();
    List<BiliFavList> favLists = new ArrayList<>();
    listNode.forEach(
        favListNode -> {
          BiliFavList favList = new BiliFavList();
          favList.setId(favListNode.get("id").longValue());
          favList.setFid(favListNode.get("fid").longValue());
          favList.setMid(favListNode.get("mid").longValue());
          favList.setUpperName(favListNode.get("upper").get("name").textValue());
          favList.setViewCount(favListNode.get("view_count").longValue());
          favList.setName(favListNode.get("title").textValue());
          favList.setCover(favListNode.get("cover").textValue());
          favList.setItemCount(favListNode.get("media_count").intValue());
          favList.setType(type);
          favLists.add(favList);
        });
    data.setCount(dataNode.get("count").intValue());
    data.setList(favLists);
    data.setHasMore(dataNode.get("has_more").booleanValue());
    return Result.ok(data);
  }

  /**
   * {@inheritDoc}
   *
   * <p>Get detail fav list.
   */
  @Override
  public Result getDetailFavList(
      Long id,
      Integer pn,
      Integer ps,
      String keyword,
      String order,
      Integer range,
      Integer type,
      String cookie) {
    Map<String, Object> params = new HashMap<>();
    String url;
    if (pn == null || ps == null || type == null) {
      throw new RuntimeException("Invalid parameters");
    }
    if (type == 0) {
      url = BilibiliAPI.GET_DETAIL_CREATED_FAV_LIST;
      params.put("media_id", id);
    } else if (type == 1) {
      url = BilibiliAPI.GET_DETAIL_COLLECTED_FAV_LIST;
      params.put("season_id", id);
    } else {
      throw new RuntimeException("type must be 0 or 1");
    }
    params.put("pn", pn);
    params.put("ps", ps);
    params.put("platform", "web");
    if (type == 0 && keyword != null) params.put("keyword", keyword);
    if (type == 0 && order != null) params.put("order", order);
    if (type == 0 && range != null) params.put("type", range);
    String rawDetailFavList = httpTools.requestGetAPI(url, params, Optional.of(cookie));
    return extractDetailFavList(rawDetailFavList, type);
  }

  private Result extractDetailFavList(String rawDetailFavList, int type) {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(rawDetailFavList);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    int code = jsonNode.get("code").intValue();
    if (code != 0) {
      throw new RuntimeException("BiliBili error: " + jsonNode.get("message").textValue());
    }
    JsonNode dataNode = jsonNode.get("data");
    if (dataNode.isNull()) {
      throw new RuntimeException("Invalid library");
    }
    JsonNode infoNode = dataNode.get("info");
    JsonNode mediasNode = dataNode.get("medias");
    JsonNode cntInfoNode = infoNode.get("cnt_info");
    BiliDetailFavList biliDetailFavList = new BiliDetailFavList();
    List<BiliResource> resourceList = new ArrayList<>();
    biliDetailFavList.setId(infoNode.get("id").longValue());
    biliDetailFavList.setType(type);
    biliDetailFavList.setName(infoNode.get("title").textValue());
    biliDetailFavList.setCover(infoNode.get("cover").textValue());
    biliDetailFavList.setItemCount(infoNode.get("media_count").intValue());
    biliDetailFavList.setUpperName(infoNode.get("upper").get("name").textValue());
    biliDetailFavList.setCollectedCount(cntInfoNode.get("collect").longValue());
    biliDetailFavList.setViewCount(cntInfoNode.get("play").longValue());
    biliDetailFavList.setIntro(infoNode.get("intro").textValue());
    if (type == 0) {
      biliDetailFavList.setFid(infoNode.get("fid").longValue());
      biliDetailFavList.setMid(infoNode.get("mid").longValue());
      biliDetailFavList.setUpperHeadPic(infoNode.get("upper").get("face").textValue());
      biliDetailFavList.setLikeCount(cntInfoNode.get("thumb_up").longValue());
      biliDetailFavList.setShareCount(cntInfoNode.get("share").longValue());
      biliDetailFavList.setCreatedTime(infoNode.get("ctime").longValue());
      biliDetailFavList.setModifiedTime(infoNode.get("mtime").longValue());
      biliDetailFavList.setHasMore(dataNode.get("has_more").booleanValue());
      mediasNode.forEach(
          mediaNode -> {
            BiliResource resource = new BiliResource();
            resource.setId(mediaNode.get("id").longValue());
            resource.setBvid(mediaNode.get("bvid").textValue());
            resource.setType(mediaNode.get("type").intValue());
            resource.setTitle(mediaNode.get("title").textValue());
            resource.setCover(mediaNode.get("cover").textValue());
            resource.setPage(mediaNode.get("page").intValue());
            resource.setDuration(mediaNode.get("duration").intValue());
            resource.setUpperName(mediaNode.get("upper").get("name").textValue());
            resource.setPlayCount(mediaNode.get("cnt_info").get("play").longValue());
            resource.setDanmakuCount(mediaNode.get("cnt_info").get("danmaku").longValue());
            resourceList.add(resource);
          });
      biliDetailFavList.setResources(resourceList);
    } else if (type == 1) {
      biliDetailFavList.setMid(infoNode.get("upper").get("mid").longValue());
      biliDetailFavList.setDanmakuCount(cntInfoNode.get("danmaku").longValue());
      mediasNode.forEach(
          mediaNode -> {
            BiliResource resource = new BiliResource();
            resource.setId(mediaNode.get("id").longValue());
            resource.setBvid(mediaNode.get("bvid").textValue());
            resource.setTitle(mediaNode.get("title").textValue());
            resource.setCover(mediaNode.get("cover").textValue());
            resource.setDuration(mediaNode.get("duration").intValue());
            resource.setUpperName(mediaNode.get("upper").get("name").textValue());
            resource.setPlayCount(mediaNode.get("cnt_info").get("play").longValue());
            resource.setDanmakuCount(mediaNode.get("cnt_info").get("danmaku").longValue());
            resourceList.add(resource);
          });
      biliDetailFavList.setResources(resourceList);
    } else {
      throw new RuntimeException("type must be 0 or 1");
    }
    return Result.ok(biliDetailFavList);
  }

  /**
   * {@inheritDoc}
   *
   * <p>Create fav list in bilibili.
   *
   * @apiNote POST CREATE_FAV_LIST
   *     {"name":title,"intro":intro,"privacy":privacy,"cover":cover,"csrf":csrf}
   */
  @Override
  public Result createFavList(Map<String, String> favList, String cookie) {
    if (!favList.containsKey("name")) {
      throw new RuntimeException("Name is required.");
    }
    String title = favList.get("name");
    String intro = favList.get("intro") == null ? "" : favList.get("intro");
    int privacy = favList.get("privacy") == null ? 0 : Integer.parseInt(favList.get("privacy"));
    String cover = favList.get("cover");
    String csrf = getCsrf(cookie);
    MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
    requestBody.add("title", title);
    requestBody.add("intro", intro);
    requestBody.add("privacy", privacy);
    if (cover != null) {
      requestBody.add("cover", cover);
    }
    requestBody.add("csrf", csrf);
    String rawCreatingFavListResult =
        httpTools.requestPostAPI(
            BilibiliAPI.CREATE_FAV_LIST, requestBody, Optional.empty(), Optional.of(cookie));
    return extractCreatingFavListResult(rawCreatingFavListResult);
  }

  private Result extractCreatingFavListResult(String rawCreatingFavListResult) {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(rawCreatingFavListResult);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    int code = jsonNode.get("code").intValue();
    if (code != 0) {
      throw new RuntimeException("BiliBili error: " + jsonNode.get("message").textValue());
    }
    Long id = jsonNode.get("data").get("id").longValue();
    return Result.ok(id);
  }

  /**
   * {@inheritDoc}
   *
   * <p>Delete fav list in bilibili.
   *
   * @apiNote POST DELETE_FAV_LIST {"media_ids":{@code ids},"csrf":csrf}
   */
  @Override
  public Result deleteFavList(String ids, String cookie) {
    MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
    String csrf = getCsrf(cookie);
    requestBody.add("media_ids", ids);
    requestBody.add("csrf", csrf);
    String rawDeletingFavListResult =
        httpTools.requestPostAPI(
            BilibiliAPI.DELETE_FAV_LIST, requestBody, Optional.empty(), Optional.of(cookie));
    return extractDeletingFavListResult(rawDeletingFavListResult);
  }

  private Result extractDeletingFavListResult(String rawDeletingFavListResult) {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(rawDeletingFavListResult);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    int code = jsonNode.get("code").intValue();
    if (code != 0) {
      throw new RuntimeException("BiliBili error: " + jsonNode.get("message").textValue());
    }
    return Result.ok();
  }

  /**
   * {@inheritDoc}
   *
   * <p>Edit the existed fav list.
   *
   * @apiNote POST EDIT_FAV_LIST {"media_id":{@code id},"title":{@code title},"intro":{@code
   *     intro},"privacy":{@code privacy},"cover":{@code cover},"csrf":csrf}
   */
  @Override
  public Result editFavList(
      Long id, String title, String intro, Integer privacy, String cover, String cookie) {
    MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
    String csrf = getCsrf(cookie);
    requestBody.add("media_id", id);
    requestBody.add("title", title);
    requestBody.add("intro", intro);
    requestBody.add("privacy", privacy);
    requestBody.add("cover", cover);
    requestBody.add("csrf", csrf);
    String rawEditingFavListResult =
        httpTools.requestPostAPI(
            BilibiliAPI.EDIT_FAV_LIST, requestBody, Optional.empty(), Optional.of(cookie));
    return extractEditingFavListResult(rawEditingFavListResult);
  }

  private Result extractEditingFavListResult(String rawEditingFavListResult) {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(rawEditingFavListResult);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    int code = jsonNode.get("code").intValue();
    if (code != 0) {
      throw new RuntimeException("BiliBili error: " + jsonNode.get("message").textValue());
    }
    return Result.ok();
  }

  /**
   * {@inheritDoc}
   *
   * <p>Add multiple resources to fav list.
   *
   * @apiNote POST MULTI_ADD_RESOURCES {"src_media_id":{@code srcMediaId},"tar_media_id":{@code
   *     dstMediaId},"mid":{@code mid},"resources":{@code resourcesIds},"platform":{@code
   *     platform},"csrf":csrf}
   */
  @Override
  public Result multipleAddResources(
      String srcMediaId,
      String dstMediaId,
      Long mid,
      String resourcesIds,
      String platform,
      String cookie) {
    if (srcMediaId == null) {
      return Result.fail("Invalid parameters");
    }
    MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
    String csrf = getCsrf(cookie);
    requestBody.add("src_media_id", srcMediaId);
    requestBody.add("tar_media_id", dstMediaId);
    requestBody.add("mid", mid);
    requestBody.add("resources", resourcesIds);
    requestBody.add("platform", platform);
    requestBody.add("csrf", csrf);
    String rawMultipleAddResult =
        httpTools.requestPostAPI(
            BilibiliAPI.MULTI_ADD_RESOURCES, requestBody, Optional.empty(), Optional.of(cookie));
    return extractMultipleAddResult(rawMultipleAddResult);
  }

  private Result extractMultipleAddResult(String rawMultipleAddResult) {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(rawMultipleAddResult);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    int code = jsonNode.get("code").intValue();
    if (code != 0) {
      throw new RuntimeException("BiliBili error: " + jsonNode.get("message").textValue());
    }
    return Result.ok();
  }

  /**
   * {@inheritDoc}
   *
   * <p>Move multiple resources from one fav list to another.
   *
   * @apiNote POST MULTI_MOVE_RESOURCES {"src_media_id":{@code srcMediaId},"tar_media_id":{@code
   *     dstMediaId},"mid":{@code mid},"resources":{@code resourcesIds},"platform":{@code
   *     platform},"csrf":csrf}
   */
  @Override
  public Result multipleMoveResources(
      Long srcMediaId,
      Long dstMediaId,
      Long mid,
      String resourcesIds,
      String platform,
      String cookie) {
    MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
    String csrf = getCsrf(cookie);
    requestBody.add("src_media_id", srcMediaId);
    requestBody.add("tar_media_id", dstMediaId);
    requestBody.add("mid", mid);
    requestBody.add("resources", resourcesIds);
    requestBody.add("platform", platform);
    requestBody.add("csrf", csrf);
    String rawMultipleMoveResult =
        httpTools.requestPostAPI(
            BilibiliAPI.MULTI_MOVE_RESOURCES, requestBody, Optional.empty(), Optional.of(cookie));
    return extractMultipleAddResult(rawMultipleMoveResult);
  }

  /**
   * {@inheritDoc}
   *
   * <p>Delete multiple resources from fav list.
   *
   * @apiNote POST MULTI_DELETE_RESOURCES {"resources":{@code resourcesIds},"media_id":{@code
   *     mediaId},"platform":{@code platform},"csrf":csrf}
   */
  @Override
  public Result multipleDeleteResources(
      String resourcesIds, Long mediaId, String platform, String cookie) {
    MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
    String csrf = getCsrf(cookie);
    requestBody.add("resources", resourcesIds);
    requestBody.add("media_id", mediaId);
    requestBody.add("platform", platform);
    requestBody.add("csrf", csrf);
    String rawMultipleDeleteResult =
        httpTools.requestPostAPI(
            BilibiliAPI.MULTI_DELETE_RESOURCES, requestBody, Optional.empty(), Optional.of(cookie));
    return extractMultipleAddResult(rawMultipleDeleteResult);
  }

  /**
   * {@inheritDoc}
   *
   * <p>Add resource to fav lists.
   */
  @Override
  public Result favoriteResourceToFavLists(
      Long aid, Integer type, String targetFavListsIds, String cookie) {
    MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
    String csrf = getCsrf(cookie);
    requestBody.add("rid", aid);
    requestBody.add("type", type);
    requestBody.add("add_media_ids", targetFavListsIds);
    requestBody.add("csrf", csrf);
    String rawFavoritedResult =
        httpTools.requestPostAPI(
            BilibiliAPI.FAVORITE_RESOURCE_TO_FAV_LISTS,
            requestBody,
            Optional.of("https://www.bilibili.com"),
            Optional.of(cookie));
    return extractMultipleAddResult(rawFavoritedResult);
  }

  /**
   * {@inheritDoc}
   *
   * <p>Get csrf according to bilibili cookie.
   */
  public String getCsrf(String biliCookie) {
    String[] pairs = biliCookie.split(";");
    Map<String, String> map = new HashMap<>();
    for (String pair : pairs) {
      String[] keyValue = pair.trim().split("=");
      if (keyValue.length == 2) {
        map.put(keyValue[0], keyValue[1]);
      }
    }
    return map.get("bili_jct");
  }
}
