package com.daw.pms.Service;

import com.daw.pms.Entity.QQMusicUser;

/**
 * Service for handle user info of qq music.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 6/1/23
 */
public interface QQMusicUserService {
  /**
   * Return the user info for your qq music.
   *
   * @param id Your qq number.
   * @param cookie Your cookie for qq music.
   * @return Your user info for your qq music wrapped in QQMusicUser.
   * @apiNote GET /user/detail?id={@code id}
   */
  QQMusicUser getUserInfo(String id, String cookie);
}
