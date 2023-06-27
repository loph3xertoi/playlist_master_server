package com.daw.pms.Service.impl;

import com.daw.pms.Config.QQMusicAPI;
import com.daw.pms.Service.QQMusicCookieService;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Service for handling cookie for qq music.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 5/31/23
 */
@Service
public class QQMusicCookieServiceImpl extends QQMusicBase implements QQMusicCookieService {

  /**
   * Store your qq music cookie to QQMusicAPI server.
   *
   * @param cookie qq music cookie.
   * @return Return result from QQMusicAPI server.
   * @apiNote POST /user/setCookie {"data":"{@code cookie}"}
   */
  @Override
  public String setCookie(String cookie) {
    String url = host + ":" + port + QQMusicAPI.SET_COOKIE;

    HttpHeaders headers = new HttpHeaders();
    //    headers.set("Authorization", "Bearer " + accessToken);
    Map<String, String> request = new HashMap<>();
    request.put("data", cookie);

    HttpEntity<?> entity = new HttpEntity<>(request, headers);

    ResponseEntity<String> response =
        restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    return response.getBody();
  }

  /**
   * Apply cookie for every request.
   *
   * @param id Your qq number.
   * @return Return result from QQMusicAPI server.
   * @apiNote GET /user/applyCookie?id={@code id}
   */
  @Override
  public String applyCookie(String id) {
    return requestGetAPI(
        QQMusicAPI.APPLY_COOKIE,
        new HashMap<String, String>() {
          {
            put("id", id);
          }
        },
        Optional.empty());
  }

  /**
   * Return the cookie stored in QQMusicAPI server.
   *
   * @param raw Return raw cookie if raw equal to 1.
   * @return Return cookie in json(raw=0) or in raw string(raw=1).
   * @apiNote GET /user/getCookie?raw={@code raw}
   */
  @Override
  public String getCookie(Integer raw) {
    return requestGetAPI(
        QQMusicAPI.GET_COOKIE,
        new HashMap<String, Integer>() {
          {
            put("raw", raw);
          }
        },
        Optional.empty());
  }
}
