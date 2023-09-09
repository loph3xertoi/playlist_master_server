package com.daw.pms.Service.PMS.impl;

import com.daw.pms.DTO.Result;
import com.daw.pms.DTO.ThirdAppCredentialDTO;
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
import com.daw.pms.Utils.PMSUserDetailsUtil;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.crypto.password.PasswordEncoder;
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
  private final PasswordEncoder passwordEncoder;

  public UserServiceImpl(
      QQMusicUserService qqMusicUserService,
      NCMUserService ncmUserService,
      BiliUserService biliUserService,
      UserMapper userMapper,
      PasswordEncoder passwordEncoder) {
    this.qqMusicUserService = qqMusicUserService;
    this.ncmUserService = ncmUserService;
    this.biliUserService = biliUserService;
    this.userMapper = userMapper;
    this.passwordEncoder = passwordEncoder;
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
  public BasicUser getUserInfo(Long id, Integer platform) {
    UserDTO userDTO = userMapper.getUser(id);
    if (platform == 0) {
      if (userDTO == null) {
        return null;
      }
      return getPMSUser(userDTO);
    } else if (platform == 1) {
      return qqMusicUserService.getUserInfo(userDTO.getQqmusicId(), userDTO.getQqmusicCookie());
    } else if (platform == 2) {
      return ncmUserService.getUserInfo(userDTO.getNcmId(), userDTO.getNcmCookie());
    } else if (platform == 3) {
      return biliUserService.getUserInfo(userDTO.getBiliCookie());
    } else {
      throw new RuntimeException("Invalid platform");
    }
  }

  /**
   * Check if the pms user with username already exists.
   *
   * @param username User name.
   * @return True if the username already exists, false otherwise.
   */
  @Override
  public boolean checkIfPMSUserNameExist(String username) {
    return userMapper.checkIfUserNameExist(username) == 1;
  }

  /**
   * Check if the pms user binds this email already exists.
   *
   * @param email User email.
   * @return True if the email has already been bound, false otherwise.
   */
  @Override
  public boolean checkIfEmailAddressExist(String email) {
    return userMapper.checkIfEmailAddressExist(email) == 1;
  }

  /**
   * Update the third party app's credential of current user.
   *
   * @param credentialDTO Credential dto.
   * @param platform 1 for qqmusic, 2 for ncm, 3 for bilibili.
   * @return The result for updating credential.
   */
  @Override
  public Result updateThirdAppCredential(ThirdAppCredentialDTO credentialDTO, Integer platform) {
    Long pmsUserId = PMSUserDetailsUtil.getCurrentLoginUserId();
    String thirdId = credentialDTO.getThirdId();
    String thirdCookie = credentialDTO.getThirdCookie();
    if (platform == 1) {
      return updateQQMusicCredential(pmsUserId, thirdId, thirdCookie);
    } else if (platform == 2) {
      return updateNCMCredential(pmsUserId, thirdId, thirdCookie);
    } else if (platform == 3) {
      return updateBiliCredential(pmsUserId, thirdId, thirdCookie);
    } else {
      throw new RuntimeException("Invalid platform");
    }
  }

  /**
   * Update pms user's pass.
   *
   * @param pmsUserId The pms user's id.
   * @param newPassword New plain password without encoded.
   * @return Common result.
   */
  @Override
  public Result updatePassword(Long pmsUserId, String newPassword) {
    String encodedPassword = passwordEncoder.encode(newPassword);
    userMapper.updateUserPass(pmsUserId, encodedPassword);
    return Result.ok();
  }

  /**
   * Add new pms user.
   *
   * @param userDTO User dto.
   * @return Registered user's id in pms if success.
   */
  @Override
  public Result addUser(UserDTO userDTO) {
    int rows = userMapper.addUser(userDTO);
    if (rows == 1) {
      return Result.ok(userDTO.getId());
    } else return Result.fail("Add user failed");
  }

  @Override
  public boolean identifyUserByEmail(String email) {
    return userMapper.identifyUserByEmail(email) == 1;
  }

  @Override
  public Long getUserIdByEmail(String email) {
    return userMapper.getUserIdByEmail(email);
  }

  private Result updateQQMusicCredential(Long pmsUserId, String qqmusicId, String qqmusicCookie) {
    int rows = userMapper.updateQQMusicCredential(pmsUserId, qqmusicId, qqmusicCookie);
    if (rows == 1) {
      return Result.ok();
    } else {
      return Result.fail("Failed to update qqmusic credential");
    }
  }

  private Result updateNCMCredential(Long pmsUserId, String ncmId, String ncmCookie) {
    int rows = userMapper.updateNCMCredential(pmsUserId, ncmId, ncmCookie);
    if (rows == 1) {
      return Result.ok();
    } else {
      return Result.fail("Failed to update qqmusic credential");
    }
  }

  private Result updateBiliCredential(Long pmsUserId, String biliId, String biliCookie) {
    int rows = userMapper.updateBiliCredential(pmsUserId, biliId, biliCookie);
    if (rows == 1) {
      return Result.ok();
    } else {
      return Result.fail("Failed to update qqmusic credential");
    }
  }

  private PMSUser getPMSUser(UserDTO userDTO) {
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
