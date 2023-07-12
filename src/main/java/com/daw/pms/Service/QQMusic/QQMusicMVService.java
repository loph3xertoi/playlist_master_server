package com.daw.pms.Service.QQMusic;

import com.daw.pms.Entity.Basic.BasicVideo;
import com.daw.pms.Entity.QQMusic.QQMusicDetailVideo;
import java.util.List;
import java.util.Map;

/**
 * Service for handle mv in qq music.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 6/2/23
 */
public interface QQMusicMVService {
  /**
   * Get detail video information according to its {@code vid}.
   *
   * @param vid The vid of the mv.
   * @param cookie Your qq music cookie.
   * @return The detail information of the mv {@code vid}, links needs to be completed.
   * @apiNote GET /mv?id={@code vid}
   */
  QQMusicDetailVideo getDetailMV(String vid, String cookie);

  /**
   * Get the url for the mv(s) {@code vids}.
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
