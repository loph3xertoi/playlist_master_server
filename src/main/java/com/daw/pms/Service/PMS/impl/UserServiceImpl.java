package com.daw.pms.Service.PMS.impl;

import com.daw.pms.DTO.UserDTO;
import com.daw.pms.Entity.Basic.BasicUser;
import com.daw.pms.Entity.Bilibili.BiliUser;
import com.daw.pms.Entity.NeteaseCloudMusic.NCMUser;
import com.daw.pms.Entity.PMS.PMSUser;
import com.daw.pms.Entity.QQMusic.QQMusicUser;
import com.daw.pms.Mapper.UserMapper;
import com.daw.pms.Service.Bilibili.BiliUserService;
import com.daw.pms.Service.NeteaseCloudMusic.NCMUserService;
import com.daw.pms.Service.PMS.UserService;
import com.daw.pms.Service.QQMusic.QQMusicUserService;
import java.io.Serializable;
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
public class UserServiceImpl implements UserService, Serializable {
  private final QQMusicUserService qqMusicUserService;
  private final NCMUserService ncmUserService;
  private final BiliUserService biliUserService;
  private final UserMapper userMapper;

  public UserServiceImpl(
      QQMusicUserService qqMusicUserService,
      NCMUserService ncmUserService,
      BiliUserService biliUserService,
      UserMapper userMapper) {
    this.qqMusicUserService = qqMusicUserService;
    this.ncmUserService = ncmUserService;
    this.biliUserService = biliUserService;
    this.userMapper = userMapper;
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
      return getPMSUser(id);
    } else if (platform == 1) {
      UserDTO userDTO = userMapper.getUser(id);
      return qqMusicUserService.getUserInfo(userDTO.getQqmusicId(), userDTO.getQqmusicCookie());
    } else if (platform == 2) {
      UserDTO userDTO = userMapper.getUser(id);
      return ncmUserService.getUserInfo(userDTO.getNcmId(), userDTO.getNcmCookie());
    } else if (platform == 3) {
      UserDTO userDTO = userMapper.getUser(id);
      return biliUserService.getUserInfo(userDTO.getBiliCookie());
    } else {
      throw new RuntimeException("Invalid platform");
    }
  }

  private PMSUser getPMSUser(Long id) {
    UserDTO userDTO = userMapper.getUser(id);
    PMSUser pmsUser = new PMSUser();
    pmsUser.setId(userDTO.getId());
    pmsUser.setName(userDTO.getName());
    pmsUser.setHeadPic(userDTO.getAvatar());
    pmsUser.setBgPic(userDTO.getBgPic());
    pmsUser.setIntro(userDTO.getIntro());
    Map<String, BasicUser> subUsers = new HashMap<>();
    try {
      QQMusicUser qqMusicUser =
          qqMusicUserService.getUserInfo(userDTO.getQqmusicId(), userDTO.getQqmusicCookie());
      subUsers.put("qqmusic", qqMusicUser);
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      NCMUser ncmUser = ncmUserService.getUserInfo(userDTO.getNcmId(), userDTO.getNcmCookie());
      subUsers.put("ncm", ncmUser);
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      BiliUser biliUser = biliUserService.getUserInfo(userDTO.getBiliCookie());
      subUsers.put("bilibili", biliUser);
    } catch (Exception e) {
      e.printStackTrace();
    }
    pmsUser.setSubUsers(subUsers);
    return pmsUser;
  }
}
