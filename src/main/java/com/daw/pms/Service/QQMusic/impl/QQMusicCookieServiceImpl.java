package com.daw.pms.Service.QQMusic.impl;

import com.daw.pms.Config.QQMusicAPI;
import com.daw.pms.Service.QQMusic.QQMusicCookieService;
import com.daw.pms.Utils.HttpTools;
import java.util.HashMap;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Service for handling cookie for qq music.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 5/31/23
 */
@Service
public class QQMusicCookieServiceImpl implements QQMusicCookieService {
  private final HttpTools httpTools;
  private final String baseUrl;

  /**
   * Constructor for QQMusicCookieServiceImpl.
   *
   * @param httpTools a {@link com.daw.pms.Utils.HttpTools} object.
   */
  public QQMusicCookieServiceImpl(HttpTools httpTools) {
    this.httpTools = httpTools;
    this.baseUrl = httpTools.qqmusicHost + ":" + httpTools.qqmusicPort;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Store your qq music cookie to QQMusicAPI server.
   *
   * @apiNote POST /user/setCookie {"data":"{@code cookie}"}
   */
  @Override
  public String setCookie(String cookie) {
    MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
    requestBody.add("data", cookie);
    return httpTools.requestPostAPI(
        baseUrl + QQMusicAPI.SET_COOKIE, requestBody, Optional.empty(), Optional.empty());
  }

  /**
   * {@inheritDoc}
   *
   * <p>Apply cookie for every request.
   *
   * @apiNote GET /user/applyCookie?id={@code id}
   */
  @Override
  public String applyCookie(String id) {
    return httpTools.requestGetAPI(
        baseUrl + QQMusicAPI.APPLY_COOKIE,
        new HashMap<String, String>() {
          {
            put("id", id);
          }
        },
        Optional.empty());
  }

  /**
   * {@inheritDoc}
   *
   * <p>Return the cookie stored in QQMusicAPI server.
   *
   * @apiNote GET /user/getCookie?raw={@code raw}
   */
  @Override
  public String getCookie(Integer raw) {
    return httpTools.requestGetAPI(
        baseUrl + QQMusicAPI.GET_COOKIE,
        new HashMap<String, Integer>() {
          {
            put("raw", raw);
          }
        },
        Optional.empty());
  }
}
