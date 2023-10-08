package com.daw.pms.Service.PMS.impl;

import com.daw.pms.DTO.BasicPMSUserInfoDTO;
import com.daw.pms.DTO.Result;
import com.daw.pms.DTO.ThirdAppCredentialDTO;
import com.daw.pms.DTO.UserDTO;
import com.daw.pms.Entity.Basic.BasicUser;
import com.daw.pms.Entity.Bilibili.BiliUser;
import com.daw.pms.Entity.NeteaseCloudMusic.NCMUser;
import com.daw.pms.Entity.OAuth2.GitHubOAuth2User;
import com.daw.pms.Entity.OAuth2.GoogleOAuth2User;
import com.daw.pms.Entity.PMS.PMSUser;
import com.daw.pms.Entity.PMS.PMSUserDetails;
import com.daw.pms.Entity.QQMusic.QQMusicUser;
import com.daw.pms.Mapper.UserMapper;
import com.daw.pms.Service.BiliBili.BiliUserService;
import com.daw.pms.Service.NeteaseCloudMusic.NCMUserService;
import com.daw.pms.Service.PMS.UserService;
import com.daw.pms.Service.QQMusic.QQMusicUserService;
import com.daw.pms.Utils.PmsUserDetailsUtil;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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
  private final PmsUserDetailsUtil pmsUserDetailsUtil;

  /**
   * Constructor for UserServiceImpl.
   *
   * @param qqMusicUserService a {@link com.daw.pms.Service.QQMusic.QQMusicUserService} object.
   * @param ncmUserService a {@link com.daw.pms.Service.NeteaseCloudMusic.NCMUserService} object.
   * @param biliUserService a {@link com.daw.pms.Service.BiliBili.BiliUserService} object.
   * @param userMapper a {@link com.daw.pms.Mapper.UserMapper} object.
   * @param passwordEncoder a {@link org.springframework.security.crypto.password.PasswordEncoder}
   *     object.
   * @param pmsUserDetailsUtil a {@link com.daw.pms.Utils.PmsUserDetailsUtil} object.
   */
  public UserServiceImpl(
      QQMusicUserService qqMusicUserService,
      NCMUserService ncmUserService,
      BiliUserService biliUserService,
      UserMapper userMapper,
      PasswordEncoder passwordEncoder,
      PmsUserDetailsUtil pmsUserDetailsUtil) {
    this.qqMusicUserService = qqMusicUserService;
    this.ncmUserService = ncmUserService;
    this.biliUserService = biliUserService;
    this.userMapper = userMapper;
    this.passwordEncoder = passwordEncoder;
    this.pmsUserDetailsUtil = pmsUserDetailsUtil;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Return user information for specific platform.
   */
  @Override
  public Result getUserInfo(Long id, Integer platform) {
    UserDTO userDTO = userMapper.getUser(id);
    if (platform == 0) {
      if (userDTO == null) {
        return Result.fail("No such user");
      }
      return Result.ok(getPMSUser(userDTO));
    } else if (platform == 1) {
      String qqmusicCookie = userDTO.getQqmusicCookie();
      if (qqmusicCookie == null) {
        return Result.fail("Please login QQ Music first");
      }
      return Result.ok(qqMusicUserService.getUserInfo(userDTO.getQqmusicId(), qqmusicCookie));
    } else if (platform == 2) {
      String ncmCookie = userDTO.getNcmCookie();
      if (ncmCookie == null) {
        return Result.fail("Please login Netease Cloud Music first");
      }
      return Result.ok(ncmUserService.getUserInfo(userDTO.getNcmId(), ncmCookie));
    } else if (platform == 3) {
      String biliCookie = userDTO.getBiliCookie();
      if (biliCookie == null) {
        return Result.fail("Please login BiliBili first");
      }
      return Result.ok(biliUserService.getUserInfo(biliCookie));
    } else {
      throw new RuntimeException("Invalid platform");
    }
  }

  /**
   * {@inheritDoc}
   *
   * <p>Check if the pms user with username already exists.
   */
  @Override
  public boolean checkIfPMSUserNameExist(String username, Integer loginType) {
    return userMapper.checkIfUserNameExist(username, loginType) == 1;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Check if the pms user binds this email already exists.
   */
  @Override
  public boolean checkIfEmailAddressExist(String email, Integer loginType) {
    return userMapper.checkIfEmailAddressExist(email, loginType) == 1;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Check if the phone number exists.
   */
  @Override
  public boolean checkIfPhoneNumberExist(String phoneNumber, Integer loginType) {
    return userMapper.checkIfPhoneNumberExist(phoneNumber, loginType) == 1;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Update the third party app's credential of current user.
   */
  @Override
  public Result updateThirdAppCredential(ThirdAppCredentialDTO credentialDTO, Integer platform) {
    Long pmsUserId = pmsUserDetailsUtil.getCurrentLoginUserId();
    String thirdId = credentialDTO.getThirdId().trim();
    String thirdCookie = credentialDTO.getThirdCookie().trim();
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
   * {@inheritDoc}
   *
   * <p>Update pms user's pass.
   */
  @Override
  public Result updatePassword(Long pmsUserId, String newPassword) {
    String encodedPassword = passwordEncoder.encode(newPassword);
    int rows = userMapper.updateUserPass(pmsUserId, encodedPassword);
    if (rows == 1) {
      return Result.ok();
    } else {
      return Result.fail("Failed to update password");
    }
  }

  /**
   * {@inheritDoc}
   *
   * <p>Update pms user's email.
   */
  @Override
  public Result updateEmail(Long pmsUserId, String email) {
    int rows = userMapper.updateUserEmail(pmsUserId, email);
    if (rows == 1) {
      return Result.ok();
    } else {
      return Result.fail("Failed to update email");
    }
  }

  /**
   * {@inheritDoc}
   *
   * <p>Update basic pms user info.
   */
  @Override
  public Result updateBasicPMSUserInfo(Long pmsUserId, String name, String email, String avatar) {
    int rows = userMapper.updateBasicPMSUserInfo(pmsUserId, name, email, avatar);
    if (rows == 1) {
      return Result.ok();
    } else {
      return Result.fail("Failed to update pms user's basic info");
    }
  }

  /**
   * {@inheritDoc}
   *
   * <p>Add new pms user.
   */
  @Override
  public Result addUser(UserDTO userDTO) {
    int rows = userMapper.addUser(userDTO);
    if (rows == 1) {
      return Result.ok(userDTO.getId());
    } else return Result.fail("Add user failed");
  }

  /**
   * {@inheritDoc}
   *
   * <p>Check if this email has already bound to user in this login type.
   */
  @Override
  public boolean identifyUserByEmail(String email, Integer loginType) {
    return userMapper.identifyUserByEmail(email, loginType) == 1;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Get user id by email in specific login type.
   */
  @Override
  public Long getUserIdByEmail(String email, Integer loginType) {
    return userMapper.getUserIdByEmail(email, loginType);
  }

  /**
   * {@inheritDoc}
   *
   * <p>Get basic pms user info.
   */
  @Override
  public Result getBasicPMSUserInfo(Long pmsUserId) {
    BasicPMSUserInfoDTO basicPMSUserInfo = userMapper.getBasicPMSUserInfo(pmsUserId);
    if (basicPMSUserInfo != null) {
      return Result.ok(basicPMSUserInfo);
    } else {
      return Result.fail("Get basic pms user info failed");
    }
  }

  private Result updateQQMusicCredential(Long pmsUserId, String qqmusicId, String qqmusicCookie) {
    int rows = userMapper.updateQQMusicCredential(pmsUserId, qqmusicId, qqmusicCookie);
    if (rows == 1) {
      // Update qq music credential in security context.
      SecurityContext securityContext = SecurityContextHolder.getContext();
      Authentication authentication = securityContext.getAuthentication();
      Object principal = authentication.getPrincipal();
      if (principal.getClass().equals(PMSUserDetails.class)) {
        PMSUserDetails pmsUserDetails = (PMSUserDetails) authentication.getPrincipal();
        pmsUserDetails.getUser().setQqmusicId(Long.valueOf(qqmusicId));
        pmsUserDetails.getUser().setQqmusicCookie(qqmusicCookie);
      } else if (principal.getClass().equals(GitHubOAuth2User.class)) {
        GitHubOAuth2User gitHubOAuth2User = (GitHubOAuth2User) authentication.getPrincipal();
        gitHubOAuth2User.setQqmusicId(Long.valueOf(qqmusicId));
        gitHubOAuth2User.setQqmusicCookie(qqmusicCookie);
      } else if (principal.getClass().equals(GoogleOAuth2User.class)) {
        GoogleOAuth2User googleOAuth2User = (GoogleOAuth2User) authentication.getPrincipal();
        googleOAuth2User.setQqmusicId(Long.valueOf(qqmusicId));
        googleOAuth2User.setQqmusicCookie(qqmusicCookie);
      } else {
        throw new RuntimeException("Invalid login type");
      }
      return Result.ok();
    } else {
      return Result.fail("Failed to update qqmusic credential");
    }
  }

  private Result updateNCMCredential(Long pmsUserId, String ncmId, String ncmCookie) {
    int rows = userMapper.updateNCMCredential(pmsUserId, ncmId, ncmCookie);
    if (rows == 1) {
      // Update ncm credential in security context.
      SecurityContext securityContext = SecurityContextHolder.getContext();
      Authentication authentication = securityContext.getAuthentication();
      Object principal = authentication.getPrincipal();
      if (principal.getClass().equals(PMSUserDetails.class)) {
        PMSUserDetails pmsUserDetails = (PMSUserDetails) authentication.getPrincipal();
        pmsUserDetails.getUser().setNcmId(Long.valueOf(ncmId));
        pmsUserDetails.getUser().setNcmCookie(ncmCookie);
      } else if (principal.getClass().equals(GitHubOAuth2User.class)) {
        GitHubOAuth2User gitHubOAuth2User = (GitHubOAuth2User) authentication.getPrincipal();
        gitHubOAuth2User.setNcmId(Long.valueOf(ncmId));
        gitHubOAuth2User.setNcmCookie(ncmCookie);
      } else if (principal.getClass().equals(GoogleOAuth2User.class)) {
        GoogleOAuth2User googleOAuth2User = (GoogleOAuth2User) authentication.getPrincipal();
        googleOAuth2User.setNcmId(Long.valueOf(ncmId));
        googleOAuth2User.setNcmCookie(ncmCookie);
      } else {
        throw new RuntimeException("Invalid login type");
      }
      return Result.ok();
    } else {
      return Result.fail("Failed to update qqmusic credential");
    }
  }

  private Result updateBiliCredential(Long pmsUserId, String biliId, String biliCookie) {
    int rows = userMapper.updateBiliCredential(pmsUserId, biliId, biliCookie);
    if (rows == 1) {
      // Update bilibili credential in security context.
      SecurityContext securityContext = SecurityContextHolder.getContext();
      Authentication authentication = securityContext.getAuthentication();
      Object principal = authentication.getPrincipal();
      if (principal.getClass().equals(PMSUserDetails.class)) {
        PMSUserDetails pmsUserDetails = (PMSUserDetails) authentication.getPrincipal();
        pmsUserDetails.getUser().setBilibiliId(Long.valueOf(biliId));
        pmsUserDetails.getUser().setBiliCookie(biliCookie);
      } else if (principal.getClass().equals(GitHubOAuth2User.class)) {
        GitHubOAuth2User gitHubOAuth2User = (GitHubOAuth2User) authentication.getPrincipal();
        gitHubOAuth2User.setBilibiliId(Long.valueOf(biliId));
        gitHubOAuth2User.setBiliCookie(biliCookie);
      } else if (principal.getClass().equals(GoogleOAuth2User.class)) {
        GoogleOAuth2User googleOAuth2User = (GoogleOAuth2User) authentication.getPrincipal();
        googleOAuth2User.setBilibiliId(Long.valueOf(biliId));
        googleOAuth2User.setBiliCookie(biliCookie);
      } else {
        throw new RuntimeException("Invalid login type");
      }
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
      System.out.println(e.getMessage() + "\n#0\t" + e.getStackTrace()[0].toString());
    }
    try {
      NCMUser ncmUser = ncmUserService.getUserInfo(userDTO.getNcmId(), userDTO.getNcmCookie());
      subUsers.put("ncm", ncmUser);
    } catch (Exception e) {
      System.out.println(e.getMessage() + "\n#0\t" + e.getStackTrace()[0].toString());
    }
    try {
      BiliUser biliUser = biliUserService.getUserInfo(userDTO.getBiliCookie());
      subUsers.put("bilibili", biliUser);
    } catch (Exception e) {
      System.out.println(e.getMessage() + "\n#0\t" + e.getStackTrace()[0].toString());
    }
    pmsUser.setSubUsers(subUsers);
    return pmsUser;
  }
}
