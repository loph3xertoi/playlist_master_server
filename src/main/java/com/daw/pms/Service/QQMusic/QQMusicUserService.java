package com.daw.pms.Service.QQMusic;

import com.daw.pms.Entity.QQMusic.QQMusicUser;

/**
 * Service for handle user info of qq music.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 6/1/23
 */
public interface QQMusicUserService {
  /**
   * Return the user info of qq music.
   *
   * @param qid Your qq number.
   * @param cookie Your cookie of qq music.
   * @return Your user info for your qq music wrapped in QQMusicUser.
   * @apiNote GET /user/detail?id={@code qid}
   */
  QQMusicUser getUserInfo(String qid, String cookie);
}
