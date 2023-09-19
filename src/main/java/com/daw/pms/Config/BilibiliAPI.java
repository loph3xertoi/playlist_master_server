package com.daw.pms.Config;

/**
 * API for bilibili.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/18/23
 */
public class BilibiliAPI {
  /** Constant <code>GET_WBI_KEY="https://api.bilibili.com/x/web-interfac"{trunked}</code> */
  public static final String GET_WBI_KEY = "https://api.bilibili.com/x/web-interface/nav";
  /** Constant <code>GET_LOGIN_INFO="https://api.bilibili.com/x/web-interfac"{trunked}</code> */
  public static final String GET_LOGIN_INFO = "https://api.bilibili.com/x/web-interface/nav";
  /** Constant <code>GET_USER_SPACE="https://api.bilibili.com/x/space/wbi/ac"{trunked}</code> */
  public static final String GET_USER_SPACE = "https://api.bilibili.com/x/space/wbi/acc/info";
  /** Constant <code>GET_USER_STATE="https://api.bilibili.com/x/web-interfac"{trunked}</code> */
  public static final String GET_USER_STATE = "https://api.bilibili.com/x/web-interface/nav/stat";
  /** Constant <code>GET_IP_INFO="https://api.bilibili.com/x/web-interfac"{trunked}</code> */
  public static final String GET_IP_INFO = "https://api.bilibili.com/x/web-interface/zone";
  /**
   * Constant <code>GET_CREATED_FAV_LISTS="https://api.bilibili.com/x/v3/fav/folde"{trunked}</code>
   */
  public static final String GET_CREATED_FAV_LISTS =
      "https://api.bilibili.com/x/v3/fav/folder/created/list";
  /**
   * Constant <code>GET_COLLECTED_FAV_LISTS="https://api.bilibili.com/x/v3/fav/folde"{trunked}
   * </code>
   */
  public static final String GET_COLLECTED_FAV_LISTS =
      "https://api.bilibili.com/x/v3/fav/folder/collected/list";
  /**
   * Constant <code>GET_DETAIL_CREATED_FAV_LIST="https://api.bilibili.com/x/v3/fav/resou"{trunked}
   * </code>
   */
  public static final String GET_DETAIL_CREATED_FAV_LIST =
      "https://api.bilibili.com/x/v3/fav/resource/list";
  /**
   * Constant <code>GET_DETAIL_COLLECTED_FAV_LIST="https://api.bilibili.com/x/space/fav/se"{trunked}
   * </code>
   */
  public static final String GET_DETAIL_COLLECTED_FAV_LIST =
      "https://api.bilibili.com/x/space/fav/season/list";
  /** Constant <code>CREATE_FAV_LIST="https://api.bilibili.com/x/v3/fav/folde"{trunked}</code> */
  public static final String CREATE_FAV_LIST = "https://api.bilibili.com/x/v3/fav/folder/add";
  /** Constant <code>DELETE_FAV_LIST="https://api.bilibili.com/x/v3/fav/folde"{trunked}</code> */
  public static final String DELETE_FAV_LIST = "https://api.bilibili.com/x/v3/fav/folder/del";
  /** Constant <code>EDIT_FAV_LIST="https://api.bilibili.com/x/v3/fav/folde"{trunked}</code> */
  public static final String EDIT_FAV_LIST = "https://api.bilibili.com/x/v3/fav/folder/edit";
  /**
   * Constant <code>MULTI_ADD_RESOURCES="https://api.bilibili.com/x/v3/fav/resou"{trunked}</code>
   */
  public static final String MULTI_ADD_RESOURCES =
      "https://api.bilibili.com/x/v3/fav/resource/copy";
  /**
   * Constant <code>MULTI_MOVE_RESOURCES="https://api.bilibili.com/x/v3/fav/resou"{trunked}</code>
   */
  public static final String MULTI_MOVE_RESOURCES =
      "https://api.bilibili.com/x/v3/fav/resource/move";
  /**
   * Constant <code>MULTI_DELETE_RESOURCES="https://api.bilibili.com/x/v3/fav/resou"{trunked}</code>
   */
  public static final String MULTI_DELETE_RESOURCES =
      "https://api.bilibili.com/x/v3/fav/resource/batch-del";
  /**
   * Constant <code>GET_DETAIL_RESOURCE="https://api.bilibili.com/x/web-interfac"{trunked}</code>
   */
  public static final String GET_DETAIL_RESOURCE = "https://api.bilibili.com/x/web-interface/view";
  /**
   * Constant <code>GET_RESOURCE_DASH_LINKS="https://api.bilibili.com/x/player/playu"{trunked}
   * </code>
   */
  public static final String GET_RESOURCE_DASH_LINKS = "https://api.bilibili.com/x/player/playurl";
  /** Constant <code>SEARCH_RESOURCES="https://api.bilibili.com/x/web-interfac"{trunked}</code> */
  public static final String SEARCH_RESOURCES =
      "https://api.bilibili.com/x/web-interface/search/type";
  /**
   * Constant <code>
   * FAVORITE_RESOURCE_TO_FAV_LISTS="https://api.bilibili.com/medialist/gate"{trunked}</code>
   */
  public static final String FAVORITE_RESOURCE_TO_FAV_LISTS =
      "https://api.bilibili.com/medialist/gateway/coll/resource/deal";
}
