package com.daw.pms.Service.Bilibili;

import com.daw.pms.Entity.Bilibili.BilibiliUser;

/**
 * Service for handle user info in bilibili platform.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 7/30/23
 */
public interface BilibiliUserService {
  /**
   * Return the user info for bilibili.
   *
   * @param cookie Your cookie for bilibili.
   * @return Your user info in bilibili.
   */
  BilibiliUser getUserInfo(String cookie);
}
