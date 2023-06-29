package com.daw.pms.Service.impl;

import com.daw.pms.Entity.QQMusicUser;
import com.daw.pms.Service.QQMusicCookieService;
import com.daw.pms.Service.QQMusicUserService;
import com.daw.pms.Service.UserService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
  private final QQMusicUserService qqMusicUserService;
  private final QQMusicCookieService qqMusicCookieService;

  public UserServiceImpl(
      QQMusicUserService qqMusicUserService, QQMusicCookieService qqMusicCookieService) {
    this.qqMusicUserService = qqMusicUserService;
    this.qqMusicCookieService = qqMusicCookieService;
  }

  /**
   * Return entire user information for all platforms.
   *
   * @param uid User id.
   * @return Return entire user information for all platforms.
   */
  @Override
  public Map<String, Object> getUserInfo(String uid) {
    HashMap<String, Object> userInfo = new HashMap<>();
    QQMusicUser qqMusicUser =
        qqMusicUserService.getUserInfo(uid, qqMusicCookieService.getCookie(1));
    userInfo.put("qqMusicUser", qqMusicUser);
    return userInfo;
  }
}
