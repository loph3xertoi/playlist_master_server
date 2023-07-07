package com.daw.pms.Service;

import com.daw.pms.Entity.QQMusicMV;
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
   * Get mv basic information according to its {@code vid}.
   *
   * @param vid The vid of the mv.
   * @param cookie Your qq music cookie.
   * @return The basic information of the mv {@code vid} wrapped with QQMusicMV.
   * @apiNote GET /mv?id={@code vid}
   */
  QQMusicMV getMVInfo(String vid, String cookie);

  /**
   * Get the url for the mv(s) {@code vids}.
   *
   * @param vids The vid of the mv, multi vid separated by comma.
   * @param cookie Your qq music cookie.
   * @return A map which key is the vid and value is a list of urls of this mv.
   * @apiNote GET /mv/url?id={@code vids}
   */
  Map<String, List<String>> getMVsLink(String vids, String cookie);
}
