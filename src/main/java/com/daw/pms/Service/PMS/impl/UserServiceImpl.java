package com.daw.pms.Service.PMS.impl;

import com.daw.pms.Entity.Basic.BasicUser;
import com.daw.pms.Service.NeteaseCloudMusic.NCMUserService;
import com.daw.pms.Service.PMS.UserService;
import com.daw.pms.Service.QQMusic.QQMusicUserService;
import java.io.Serializable;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for handle all users' information in all music apps.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 7/11/23
 */
@Service
public class UserServiceImpl implements UserService, Serializable {
  @Value("${pms.id}")
  private Long pmsId;

  @Value("${pms.name}")
  private String pmsName;

  @Value("${pms.headPic}")
  private String pmsHeadPic;

  @Value("${pms.bgPic}")
  private String pmsBgPic;

  @Value("${qqmusic.id}")
  private Long qqMusicId;

  @Value("${qqmusic.cookie}")
  private String qqMusicCookie;

  @Value("${ncm.id}")
  private Long ncmId;

  @Value("${ncm.cookie}")
  private String ncmCookie;

  private final QQMusicUserService qqMusicUserService;
  private final NCMUserService ncmUserService;

  public UserServiceImpl(QQMusicUserService qqMusicUserService, NCMUserService ncmUserService) {
    this.qqMusicUserService = qqMusicUserService;
    this.ncmUserService = ncmUserService;
  }

  /**
   * Return user information for specific platform.
   *
   * @param id Your user id in pms.
   * @param platform Which platform the user belongs to. 0 represents pms, 1 represents qq music, 2
   *     represents netease cloud music, 3 represents bilibili.
   * @return User information for specific platform.
   */
  @Override
  public BasicUser getUserInfo(Long id, @NotNull Integer platform) {
    if (platform == 0) {
      throw new RuntimeException("Not yet implement pms platform.");
    } else if (platform == 1) {
      return qqMusicUserService.getUserInfo(qqMusicId, qqMusicCookie);
    } else if (platform == 2) {
      return ncmUserService.getUserInfo(ncmId, ncmCookie);
    } else if (platform == 3) {
      throw new RuntimeException("Not yet implement bilibili platform.");
    } else {
      throw new RuntimeException("Invalid platform.");
    }

    //    }
    //    PMSUser user = new PMSUser();
    //    user.setName(pmsName);
    //    user.setHeadPic(pmsHeadPic);
    //    user.setBgPic(pmsBgPic);
    //    user.setId(pmsId);
    //    Map<String, BasicUser> subUsers = new HashMap<>();
    //    QQMusicUser qqMusicUser = qqMusicUserService.getUserInfo(qqMusicId, qqMusicCookie);
    //    NCMUser ncmUser = ncmUserService.getUserInfo(ncmId, ncmCookie);
    //    subUsers.put("qqmusic", qqMusicUser);
    //    subUsers.put("ncm", ncmUser);
    //    user.setSubUsers(subUsers);
    //
    //    switch (platform) {
    //      case 1:
    //        return user.getSubUsers().get("qqmusic");
    //      case 2:
    //        return user.getSubUsers().get("ncm");
    //      case 3:
    //        return user.getSubUsers().get("bilibili");
    //      default:
    //        return user;
    //    }
  }
}
