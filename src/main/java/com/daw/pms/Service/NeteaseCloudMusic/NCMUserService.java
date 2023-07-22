package com.daw.pms.Service.NeteaseCloudMusic;

import com.daw.pms.Entity.NeteaseCloudMusic.NCMUser;

/**
 * Service for handle user info of netease cloud music.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 7/21/23
 */
public interface NCMUserService {
  /**
   * Return the user info of netease cloud music.
   *
   * @param uid Your user id in netease cloud music.
   * @param cookie Your cookie for netease cloud music.
   * @return Your user info for your netease cloud music wrapped in NCMUser.
   */
  NCMUser getUserInfo(String uid, String cookie);
}
