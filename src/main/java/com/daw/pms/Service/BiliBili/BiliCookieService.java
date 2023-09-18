package com.daw.pms.Service.BiliBili;

import java.util.Map;

/**
 * Service for handle cookie in bilibili.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 7/30/23
 */
public interface BiliCookieService {
  /**
   * Get wbi key.
   *
   * @return Img key and sub key.
   * @apiNote GET GET_WBI_KEY
   */
  Map<String, String> getWbiKey();
}
