package com.daw.pms.Service.Bilibili;

import java.util.Map;

/**
 * Service for handle cookie in bilibili.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 7/30/23
 */
public interface BilibiliCookieService {
  /**
   * Get wbi key.
   *
   * @return Img key and sub key.
   */
  Map<String, String> getWbiKey();
}
