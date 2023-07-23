package com.daw.pms.Service.PMS;

import com.daw.pms.Entity.Basic.BasicUser;

/**
 * Service for handle all users' information in all music apps.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 7/11/23
 */
public interface UserService {
  /**
   * Return user information for specific platform.
   *
   * @param id Your user id in pms.
   * @param platform Which platform the user belongs to. 0 represents pms, 1 represents qq music, 2
   *     represents netease cloud music, 3 represents bilibili.
   * @return User information for specific platform.
   */
  BasicUser getUserInfo(Long id, Integer platform);
}
