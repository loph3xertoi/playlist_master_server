package com.daw.pms.Service.PMS;

import com.daw.pms.DTO.LoginFormDTO;
import com.daw.pms.DTO.RegisterFormDTO;
import com.daw.pms.DTO.Result;
import java.io.UnsupportedEncodingException;
import javax.mail.MessagingException;

/**
 * Service for handle register, login and logout.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/6/23
 */
public interface LoginService {
  /**
   * Login to playlist master.
   *
   * @param loginFormDTO Login info.
   * @return Result whose data is user's id in pms.
   */
  Result login(LoginFormDTO loginFormDTO);

  /**
   * Register playlist master account.
   *
   * @param registerFormDTO Register form dto.
   * @return Registered user's id in pms if success.
   */
  Result register(RegisterFormDTO registerFormDTO);

  /**
   * Logout current pms user.
   *
   * @return Result for logout.
   */
  Result logout();

  /**
   * Forget user's password, send verifying code to user's email, only used when user have login.
   *
   * @return Common result.
   */
  Result forgotPassword() throws MessagingException, UnsupportedEncodingException;

  /**
   * Forget user's password, send verifying code to user's email, no need to login.
   *
   * @return Common result.
   */
  Result forgotPasswordByEmail(String email)
      throws MessagingException, UnsupportedEncodingException;

  /**
   * Verify token for resetting user's password, need to log in first.
   *
   * @param newPass New password.
   * @param repeatedNewPass Repeated password.
   * @param token Token user input.
   * @return Common result.
   */
  Result verifyResetPassToken(String newPass, String repeatedNewPass, String token);

  /**
   * Verify token for resetting user's password, don't need to log in first.
   *
   * @param newPass New password.
   * @param repeatedNewPass Repeated password.
   * @param token Token user input.
   * @param email User's email.
   * @return Common result.
   */
  Result verifyResetPassTokenWithoutLogin(
      String newPass, String repeatedNewPass, String token, String email);
}
