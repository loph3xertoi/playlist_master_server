package com.daw.pms.Service.Bilibili;

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
   * @apiNote GET GET_DETAIL_RESOURCE?bvid={@code bvid}&wts=wts&w_rid=w_rid
   */
  Result getDetailResource(String bvid, String cookie);

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
  Result getResourceDashLink(String bvid, Long cid, String cookie);
}
