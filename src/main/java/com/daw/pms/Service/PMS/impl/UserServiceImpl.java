package com.daw.pms.Service.PMS.impl;

import com.daw.pms.Entity.Basic.BasicUser;
import com.daw.pms.Entity.PMS.PMSUser;
import com.daw.pms.Entity.QQMusic.QQMusicUser;
import com.daw.pms.Service.PMS.UserService;
import com.daw.pms.Service.QQMusic.QQMusicCookieService;
import com.daw.pms.Service.QQMusic.QQMusicUserService;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

/**
 * Service for handle all users' information in all music apps.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 7/11/23
 */
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
   * Return user information for specific platform.
   *
   * @param id Your user id in pms.
   * @param platform Which platform the user belongs to. 0 represents pms, 1 represents qq music, 2
   *     represents netease music, 3 represents bilibili.
   * @return User information for specific platform.
   */
  @Override
  public BasicUser getUserInfo(String id, @NotNull Integer platform) {
    PMSUser user = new PMSUser();
    user.setName("Daw Loph");
    user.setHeadPic(
        "https://img0.baidu.com/it/u=819122015,412168181&fm=253&fmt=auto&app=138&f=JPEG?w=320&h=320");
    user.setBgPic(
        "https://img0.baidu.com/it/u=819122015,412168181&fm=253&fmt=auto&app=138&f=JPEG?w=320&h=320");
    user.setId("0");
    Map<String, BasicUser> subUsers = new HashMap<>();
    QQMusicUser qqMusicUser =
        qqMusicUserService.getUserInfo("2804161589", qqMusicCookieService.getCookie(1));
    subUsers.put("qqmusic", qqMusicUser);
    user.setSubUsers(subUsers);

    switch (platform) {
      case 1:
        return user.getSubUsers().get("qqmusic");
      case 2:
        return user.getSubUsers().get("neteasemusic");
      case 3:
        return user.getSubUsers().get("bilibili");
      default:
        return user;
    }
  }
}
