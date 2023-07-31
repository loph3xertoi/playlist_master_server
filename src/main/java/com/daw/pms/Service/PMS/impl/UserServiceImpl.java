package com.daw.pms.Service.PMS.impl;

import com.daw.pms.Entity.Basic.BasicUser;
import com.daw.pms.Service.Bilibili.BilibiliUserService;
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

  @Value("${bilibili.cookie}")
  private String bilibiliCookie;

  private final QQMusicUserService qqMusicUserService;
  private final NCMUserService ncmUserService;
  private final BilibiliUserService bilibiliUserService;

  public UserServiceImpl(
      QQMusicUserService qqMusicUserService,
      NCMUserService ncmUserService,
      BilibiliUserService bilibiliUserService) {
    this.qqMusicUserService = qqMusicUserService;
    this.ncmUserService = ncmUserService;
    this.bilibiliUserService = bilibiliUserService;
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
      return bilibiliUserService.getUserInfo(bilibiliCookie);
    } else {
      throw new RuntimeException("Invalid platform.");
    }
  }
}
