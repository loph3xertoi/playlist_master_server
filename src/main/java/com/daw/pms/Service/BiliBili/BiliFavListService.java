package com.daw.pms.Service.BiliBili;

import com.daw.pms.DTO.Result;
import java.util.Map;

/**
 * Service for handling fav lists of bilibili.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 8/2/23
 */
public interface BiliFavListService {
  /**
   * Get all fav lists of bilibili.
   *
   * @param pn Page number.
   * @param ps Page size.
   * @param mid Upper's mid.
   * @param platform The platform, default is web.
   * @param type 0 for created fav lists, 1 for collected fav lists.
   * @param cookie Your cookie for bilibili.
   * @return Paged bili fav list wrapped with Result DTO, data is PagedDataDTO&lt;BiliFavList&gt;.
   * @apiNote type==0: GET GET_CREATED_FAV_LISTS?pn={@code pn}&amp;ps={@code ps}&amp;up_mid={@code
   *     mid}, type==1: GET GET_COLLECTED_FAV_LISTS?pn={@code pn}&amp;ps={@code
   *     ps}&amp;up_mid={@code mid}&amp;platform={@code platform}.
   */
  Result getFavLists(
      Integer pn, Integer ps, Long mid, String platform, Integer type, String cookie);

  /**
   * Get detail fav list.
   *
   * @param id The media id(full id) of the fav list.
   * @param pn Page number.
   * @param ps Page size.
   * @param keyword The keyword of the fav list, for searching specific resources in this fav list.
   * @param order The order of the resources, mtime: by favorited time, view: by view count,
   *     pubtime: by published time, default is mtime.
   * @param range The range of searching, 0: current fav list, 1: all fav lists, default is 0.
   * @param type 0 for created fav lists, 1 for collected fav lists.
   * @param cookie Your cookie for bilibili.
   * @return Detail fav list wrapped with Result DTO, data is BiliDetailFavList.
   * @apiNote type==0: GET GET_DETAIL_CREATED_FAV_LIST?media_id={@code id}&amp;pn={@code
   *     pn}&amp;ps={@code ps}&amp;keyword={@code keyword}&amp;order={@code order}&amp;type={@code
   *     range} type==1: GET GET_DETAIL_COLLECTED_FAV_LIST?season_id={@code id}&amp;pn={@code
   *     pn}&amp;ps={@code ps}
   */
  Result getDetailFavList(
      Long id,
      Integer pn,
      Integer ps,
      String keyword,
      String order,
      Integer range,
      Integer type,
      String cookie);

  /**
   * Create fav list in bilibili.
   *
   * @param favList A map container the properties of the creating fav list.
   * @param cookie Your cookie for bilibili.
   * @return Result of creating fav list.
   * @apiNote POST CREATE_FAV_LIST
   *     {"name":title,"intro":intro,"privacy":privacy,"cover":cover,"csrf":csrf}
   */
  Result createFavList(Map<String, String> favList, String cookie);

  /**
   * Delete fav list in bilibili.
   *
   * @param ids The id of deleted fav lists, multiple fav lists' id separated by comma.
   * @param cookie Your cookie for bilibili.
   * @return Result of deleting fav list.
   * @apiNote POST DELETE_FAV_LIST {"media_ids":{@code ids},"csrf":csrf}
   */
  Result deleteFavList(String ids, String cookie);

  /**
   * Edit the existed fav list.
   *
   * @param id The id of the fav list.
   * @param title The new title of the fav list.
   * @param intro The new introduction of the fav list.
   * @param privacy The new privacy of the fav list, 0 for public, 1 for private.
   * @param cover The new cover of the fav list.
   * @param cookie Your cookie for bilibili.
   * @return Result of editing fav list.
   * @apiNote POST EDIT_FAV_LIST {"media_id":{@code id},"title":{@code title},"intro":{@code
   *     intro},"privacy":{@code privacy},"cover":{@code cover},"csrf":csrf}
   */
  Result editFavList(
      Long id, String title, String intro, Integer privacy, String cover, String cookie);

  /**
   * Add multiple resources to fav list.
   *
   * @param srcMediaId Source fav list's id.
   * @param dstMediaId Target fav list's id.
   * @param mid Upper's mid.
   * @param resourcesIds The id and type of the resources, format is id:type, multiple resources
   *     separated by comma, type may be 2: video, 12: audio, 21: videos.
   * @param platform The platform, default is web.
   * @param cookie Your cookie for bilibili.
   * @return Result of adding resources to fav list.
   * @apiNote POST MULTI_ADD_RESOURCES {"src_media_id":{@code srcMediaId},"tar_media_id":{@code
   *     dstMediaId},"mid":{@code mid},"resources":{@code resourcesIds},"platform":{@code
   *     platform},"csrf":csrf}
   */
  Result multipleAddResources(
      String srcMediaId,
      String dstMediaId,
      Long mid,
      String resourcesIds,
      String platform,
      String cookie);

  /**
   * Move multiple resources from one fav list to another.
   *
   * @param srcMediaId Source fav list's id.
   * @param dstMediaId Target fav list's id.
   * @param mid Upper's mid.
   * @param resourcesIds The id and type of the resources, format is id:type, multiple resources
   *     separated by comma, type may be 2: video, 12: audio, 21: videos.
   * @param platform The platform, default is web.
   * @param cookie Your cookie for bilibili.
   * @return Result of moving resources from one fav list to another.
   * @apiNote POST MULTI_MOVE_RESOURCES {"src_media_id":{@code srcMediaId},"tar_media_id":{@code
   *     dstMediaId},"mid":{@code mid},"resources":{@code resourcesIds},"platform":{@code
   *     platform},"csrf":csrf}
   */
  Result multipleMoveResources(
      Long srcMediaId,
      Long dstMediaId,
      Long mid,
      String resourcesIds,
      String platform,
      String cookie);

  /**
   * Delete multiple resources from fav list.
   *
   * @param resourcesIds The id and type of the resources, format is id:type, multiple resources
   *     separated by comma, type may be 2: video, 12: audio, 21: videos.
   * @param mediaId Fav list's id.
   * @param platform The platform, default is web.
   * @param cookie Your cookie for bilibili.
   * @return Result of deleting resources from fav list.
   * @apiNote POST MULTI_DELETE_RESOURCES {"resources":{@code resourcesIds},"media_id":{@code
   *     mediaId},"platform":{@code platform},"csrf":csrf}
   */
  Result multipleDeleteResources(String resourcesIds, Long mediaId, String platform, String cookie);

  /**
   * Add resource to fav lists.
   *
   * @param aid The aid of the resource.
   * @param type The type of resource, must be 2.
   * @param targetFavListsIds The id of target fav lists, multiple fav lists' id separated by comma.
   * @param cookie Your cookie for bilibili.
   * @return Result of adding resource to fav lists.
   */
  Result favoriteResourceToFavLists(
      Long aid, Integer type, String targetFavListsIds, String cookie);

  /**
   * Get csrf according to bilibili cookie.
   *
   * @param biliCookie Your cookie for bilibili.
   * @return The csrf string.
   */
  String getCsrf(String biliCookie);
}
