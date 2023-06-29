package com.daw.pms.Service;

import java.util.Map;

/**
 * Service for handle all user information in all music apps.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 6/28/23
 */
public interface UserService {
  /**
   * Return entire user information for all platforms.
   *
   * @param uid User id.
   * @return Return entire user information for all platforms.
   */
  Map<String, Object> getUserInfo(String uid);
}
