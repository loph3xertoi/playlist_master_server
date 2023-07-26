package com.daw.pms.Service.NeteaseCloudMusic;

import com.daw.pms.Entity.Basic.BasicVideo;
import com.daw.pms.Entity.NeteaseCloudMusic.NCMDetailVideo;
import java.util.List;
import java.util.Map;

/**
 * Service for handle mv in ncm.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 7/24/23
 */
public interface NCMMVService {
  /**
   * Get detail video information according to its {@code id}.
   *
   * @param id The id of the mv.
   * @param cookie Your cookie for netease cloud music.
   * @return The detail information of the mv {@code id}, links needs to be completed.
   * @apiNote GET /mv/detail?mvid={@code id}
   */
  NCMDetailVideo getDetailMV(String id, String cookie);

  /**
   * Get the url for the mv(s) {@code mvids}.
   *
   * @param vids The vid of the mv, multi vid separated by comma.
   * @param cookie Your qq music cookie.
   * @return A map which key is the vid and value is a list of urls of this mv.
   * @apiNote GET /mv/url?id={@code vids}
   */
  Map<String, List<String>> getMVsLink(String vids, String cookie);

  /**
   * Get all the related basic videos information according to its {@code songId}.
   *
   * @param songId The song's id.
   * @param cookie Your qq music cookie.
   * @return A list of basic video information related to the song.
   */
  List<BasicVideo> getRelatedVideos(Integer songId, String cookie);
}
