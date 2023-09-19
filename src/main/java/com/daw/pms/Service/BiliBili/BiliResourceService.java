package com.daw.pms.Service.BiliBili;

import com.daw.pms.DTO.Result;

/**
 * Service for handling resources in bilibili.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 8/2/23
 */
public interface BiliResourceService {
  /**
   * Get detail resource of bilibili.
   *
   * @param bvid The bvid of this resource.
   * @param cookie Your cookie for bilibili.
   * @return Detail resource of bilibili, wrapped with Result DTO, the data is BiliDetailResource.
   * @apiNote GET GET_DETAIL_RESOURCE?bvid={@code bvid}&amp;wts=wts&amp;w_rid=w_rid
   */
  Result getDetailResource(String bvid, String cookie);

  /**
   * Get resource's dash links of bilibili.
   *
   * @param bvid The resource's bvid.
   * @param cid The resource's cid.
   * @param cookie Your cookie for bilibili.
   * @return The links of this resource, wrapped with Result DTO, the data is BiliLinksDTO.
   * @apiNote GET GET_RESOURCE_DASH_LINKS?bvid={@code bvid}&amp;cid={@code
   *     cid}&amp;fnval=16&amp;fourk=1&amp;platform=html&amp;high_quality=1&amp;wts=wts&amp;w_rid=w_rid
   */
  Result getResourceDashLink(String bvid, Long cid, String cookie);

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
   * @return Searched resources wrapped with Result DTO, the data is
   *     PagedDataDTO&lt;BiliResource&gt;.
   * @apiNote GET SEARCH_RESOURCES?search_type={@code searchType}&amp;keyword={@code
   *     keyword}&amp;order={@code order}&amp;duration={@code duration}&amp;tids={@code
   *     tids}&amp;page={@code page}
   */
  Result searchResources(
      String searchType,
      String keyword,
      String order,
      Integer duration,
      Integer tids,
      Integer page,
      String cookie);
}
