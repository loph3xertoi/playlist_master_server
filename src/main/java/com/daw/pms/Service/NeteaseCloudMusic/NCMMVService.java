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
   * @param id The id of this video.
   * @param cookie Your cookie for netease cloud music.
   * @return The detail information of the mv {@code id}.
   * @apiNote mvid: GET /mv/detail?mvid={@code id} | mlogId: GET /video/detail?id={@code id}
   */
  NCMDetailVideo getDetailMV(String id, String cookie);

  /**
   * Get the url of the mv.
   *
   * @param mvId The mvid of the mv.
   * @param rate The rate of the mv.
   * @param cookie Your cookie for netease cloud music.
   * @return MV's link with rate {@code rate}.
   * @apiNote GET /mv/url?id={@code mvId}&amp;r={@code rate}
   */
  String getMVLink(String mvId, Integer rate, String cookie);

  /**
   * Get mlog's links.
   *
   * @param mLogId The mlog's id.
   * @param cookie Your cookie for netease cloud music.
   * @return The mlog's links, the key is resolution, the value is the corresponding link.
   * @apiNote GET /mlog/url?id={@code mLogId}
   */
  Map<String, String> getMLogLinks(String mLogId, String cookie);

  /**
   * Get all related videos with song {@code songId}.
   *
   * @param songId The song's id.
   * @param mvId The mvid of the song.
   * @param limit The count of related videos returned.
   * @param cookie Your cookie for netease cloud music.
   * @return A list of related videos.
   * @apiNote GET /mlog/music/rcmd?songid={@code songId}&amp;mvid={@code mvId}&amp;limit={@code
   *     limit}
   */
  List<BasicVideo> getRelatedVideos(Long songId, String mvId, Integer limit, String cookie);

  /**
   * Convert mlog id to vid.
   *
   * @param mLogId Mlog id.
   * @param cookie Your cookie for netease cloud music.
   * @return The vid of the mlog.
   */
  String convertMLogIdToVid(String mLogId, String cookie);
}
