package com.daw.pms.Service.PMS;

import com.daw.pms.DTO.*;
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
  Result register(RegisterFormDTO registerFormDTO)
      throws MessagingException, UnsupportedEncodingException;

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
   * Send token to yur email for verifying, no need to login first.
   *
   * @param email Email to receive token.
   * @param type Token type, 1 for sign up, 2 for reset password.
   * @return Common result.
   * @throws MessagingException MessagingException.
   * @throws UnsupportedEncodingException UnsupportedEncodingException.
   */
  Result sendVerifyToken(String email, Integer type)
      throws MessagingException, UnsupportedEncodingException;

  /**
   * Verify token for resetting user's password, need to log in first.
   *
   * @param resetPassDTO DTO for resetting password.
   * @return Common result.
   */
  Result verifyResetPassToken(ResetPassDTO resetPassDTO);

  /**
   * Verify token for resetting user's password, don't need to log in first.
   *
   * @param resetPassNologinDTO Reset pass dto.
   * @return Common result.
   */
  Result verifyResetPassTokenWithoutLogin(ResetPassNologinDTO resetPassNologinDTO);

  /**
   * Verify token for create new account, don't need to log in first.
   *
   * @param signUpNologinDTO Sign up dto.
   * @return Common result.
   */
  Result verifySignUpTokenWithoutLogin(SignUpNologinDTO signUpNologinDTO);
}