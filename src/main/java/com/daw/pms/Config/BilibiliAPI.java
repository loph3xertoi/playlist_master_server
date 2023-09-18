package com.daw.pms.Config;

/**
 * API for bilibili.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/18/23
 */
public class BilibiliAPI {
  public static final String GET_WBI_KEY = "https://api.bilibili.com/x/web-interface/nav";
  public static final String GET_LOGIN_INFO = "https://api.bilibili.com/x/web-interface/nav";
  public static final String GET_USER_SPACE = "https://api.bilibili.com/x/space/wbi/acc/info";
  public static final String GET_USER_STATE = "https://api.bilibili.com/x/web-interface/nav/stat";
  public static final String GET_IP_INFO = "https://api.bilibili.com/x/web-interface/zone";
  public static final String GET_CREATED_FAV_LISTS =
      "https://api.bilibili.com/x/v3/fav/folder/created/list";
  public static final String GET_COLLECTED_FAV_LISTS =
      "https://api.bilibili.com/x/v3/fav/folder/collected/list";
  public static final String GET_DETAIL_CREATED_FAV_LIST =
      "https://api.bilibili.com/x/v3/fav/resource/list";
  public static final String GET_DETAIL_COLLECTED_FAV_LIST =
      "https://api.bilibili.com/x/space/fav/season/list";
  public static final String CREATE_FAV_LIST = "https://api.bilibili.com/x/v3/fav/folder/add";
  public static final String DELETE_FAV_LIST = "https://api.bilibili.com/x/v3/fav/folder/del";
  public static final String EDIT_FAV_LIST = "https://api.bilibili.com/x/v3/fav/folder/edit";
  public static final String MULTI_ADD_RESOURCES =
      "https://api.bilibili.com/x/v3/fav/resource/copy";
  public static final String MULTI_MOVE_RESOURCES =
      "https://api.bilibili.com/x/v3/fav/resource/move";
  public static final String MULTI_DELETE_RESOURCES =
      "https://api.bilibili.com/x/v3/fav/resource/batch-del";
  public static final String GET_DETAIL_RESOURCE = "https://api.bilibili.com/x/web-interface/view";
  public static final String GET_RESOURCE_DASH_LINKS = "https://api.bilibili.com/x/player/playurl";
  public static final String SEARCH_RESOURCES =
      "https://api.bilibili.com/x/web-interface/search/type";
  public static final String FAVORITE_RESOURCE_TO_FAV_LISTS =
      "https://api.bilibili.com/medialist/gateway/coll/resource/deal";
}
