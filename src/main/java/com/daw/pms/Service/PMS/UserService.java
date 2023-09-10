package com.daw.pms.Service.PMS;

import com.daw.pms.DTO.Result;
import com.daw.pms.DTO.ThirdAppCredentialDTO;
import com.daw.pms.DTO.UserDTO;
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

  /**
   * Check if the pms user with username already exists.
   *
   * @param username User name.
   * @return True if the username already exists, false otherwise.
   */
  boolean checkIfPMSUserNameExist(String username);

  /**
   * Check if the pms user binds this email already exists.
   *
   * @param email User email.
   * @return True if the email has already been bound, false otherwise.
   */
  boolean checkIfEmailAddressExist(String email);

  /**
   * Check if the phone number exists.
   *
   * @param phoneNumber Phone number to bind.
   * @return True if the phone number has already been bound, false otherwise.
   */
  boolean checkIfPhoneNumberExist(String phoneNumber);

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
   * Add new pms user.
   *
   * @param userDTO User dto.
   * @return Registered user's id in pms if success.
   */
  Result addUser(UserDTO userDTO);

  /**
   * The user email.
   *
   * @param email User's email.
   * @return True if the user binding this email exists.
   */
  boolean identifyUserByEmail(String email);

  /**
   * Get user id by email.
   *
   * @param email User's email.
   * @return User id.
   */
  Long getUserIdByEmail(String email);
}
