package com.daw.pms.Service.PMS;

import com.daw.pms.DTO.Result;
import com.daw.pms.DTO.ThirdAppCredentialDTO;
import com.daw.pms.DTO.UserDTO;

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
  Result getUserInfo(Long id, Integer platform);

  /**
   * Check if the pms user with username already exists.
   *
   * @param username User name.
   * @param loginType Login type: 0 for email & password, 1 for GitHub, 2 for Google.
   * @return True if the username already exists, false otherwise.
   */
  boolean checkIfPMSUserNameExist(String username, Integer loginType);

  /**
   * Check if the pms user binds this email already exists.
   *
   * @param email User email.
   * @param loginType Login type: 0 for email & password, 1 for GitHub, 2 for Google.
   * @return True if the email has already been bound, false otherwise.
   */
  boolean checkIfEmailAddressExist(String email, Integer loginType);

  /**
   * Check if the phone number exists.
   *
   * @param phoneNumber Phone number to bind.
   * @param loginType Login type: 0 for email & password, 1 for GitHub, 2 for Google.
   * @return True if the phone number has already been bound, false otherwise.
   */
  boolean checkIfPhoneNumberExist(String phoneNumber, Integer loginType);

  /**
   * Update the third party app's credential of current user.
   *
   * @param credentialDTO Credential dto.
   * @param platform 1 for qqmusic, 2 for ncm, 3 for bilibili.
   * @return The result for updating credential.
   */
  Result updateThirdAppCredential(ThirdAppCredentialDTO credentialDTO, Integer platform);

  /**
   * Update pms user's pass.
   *
   * @param pmsUserId The pms user's id.
   * @param newPassword New plain password without encoded.
   * @return Common result.
   */
  Result updatePassword(Long pmsUserId, String newPassword);

  /**
   * Update pms user's email.
   *
   * @param pmsUserId The pms user's id.
   * @param email Email to bind or update.
   * @return Common result.
   */
  Result updateEmail(Long pmsUserId, String email);

  /**
   * Update basic pms user info.
   *
   * @param pmsUserId The pms user's id.
   * @param name New name of pms user.
   * @param email New email of pms user.
   * @param avatar New avatar of pms user.
   * @return Common result.
   */
  Result updateBasicPMSUserInfo(Long pmsUserId, String name, String email, String avatar);

  /**
   * Add new pms user.
   *
   * @param userDTO User dto.
   * @return Registered user's id in pms if success.
   */
  Result addUser(UserDTO userDTO);

  /**
   * Check if this email has already bound to user in this login type.
   *
   * @param email User's email.
   * @param loginType Login type: 0 for email & password, 1 for GitHub, 2 for Google.
   * @return True if this email has already bound in this login type, false otherwise.
   */
  boolean identifyUserByEmail(String email, Integer loginType);

  /**
   * Get user id by email in specific login type.
   *
   * @param email User's email.
   * @param loginType Login type: 0 for email & password, 1 for GitHub, 2 for Google.
   * @return User id.
   */
  Long getUserIdByEmail(String email, Integer loginType);

  /**
   * Get basic pms user info.
   *
   * @param pmsUserId User id in pms.
   * @return Basic pms user info.
   */
  Result getBasicPMSUserInfo(Long pmsUserId);
}
