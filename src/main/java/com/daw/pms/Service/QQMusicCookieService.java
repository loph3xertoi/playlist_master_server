package com.daw.pms.Service;

/**
 * Service for handling cookie for qq music.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 5/31/23
 */
public interface QQMusicCookieService {

  /**
   * Store your qq music cookie to QQMusicAPI server.
   *
   * @param cookie qq music cookie.
   * @return Return result from QQMusicAPI server.
   * @apiNote POST /user/setCookie {"data":"{@code cookie}"}
   */
  String setCookie(String cookie);

  /**
   * Apply cookie for every request.
   *
   * @param id Your qq number.
   * @return Return result from QQMusicAPI server.
   * @apiNote GET /user/applyCookie?id={@code id}
   */
  String applyCookie(String id);

  /**
   * Return the cookie stored in QQMusicAPI server.
   *
   * @param raw Return raw cookie if raw equal to 1.
   * @return Return cookie in json(raw=0) or in raw string(raw=1).
   * @apiNote GET /user/getCookie?raw={@code raw}
   */
  String getCookie(Integer raw);
}
