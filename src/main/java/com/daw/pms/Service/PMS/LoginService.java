package com.daw.pms.Service.PMS;

import com.daw.pms.DTO.*;
import java.io.UnsupportedEncodingException;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

/**
 * Service for handle register, login and logout.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/6/23
 */
public interface LoginService {
  /**
   * Login to playlist master by email and password.
   *
   * @param loginFormDTO Login info.
   * @param request Http servlet request.
   * @return Result whose data is user's id in pms.
   */
  Result login(LoginFormDTO loginFormDTO, HttpServletRequest request);

  /**
   * Login to playlist master by GitHub.
   *
   * @param code Authorization code.
   * @param request Http servlet request.
   * @return Result whose data is user's id in pms.
   */
  Result loginByGitHub(String code, HttpServletRequest request);

  /**
   * Login to playlist master by Google.
   *
   * @param code Authorization code.
   * @param request Http servlet request.
   * @return Result whose data is user's id in pms.
   */
  Result loginByGoogle(String code, HttpServletRequest request);

  /**
   * Register playlist master account using email and password.
   *
   * @param registerFormDTO Register form dto.
   * @return Registered user's id in pms if success.
   * @throws javax.mail.MessagingException if any.
   * @throws java.io.UnsupportedEncodingException if any.
   */
  Result register(RegisterFormDTO registerFormDTO)
      throws MessagingException, UnsupportedEncodingException;

  /**
   * Logout current pms user.
   *
   * @param request Http servlet request.
   * @return Result for logout.
   */
  Result logout(HttpServletRequest request);

  /**
   * Forget user's password, send verifying code to user's email, only used when user have login.
   *
   * @return Common result.
   * @throws javax.mail.MessagingException if any.
   * @throws java.io.UnsupportedEncodingException if any.
   */
  Result forgotPassword() throws MessagingException, UnsupportedEncodingException;

  /**
   * Bind email, send verifying code to user's email, need login first.
   *
   * @param email Email to bind.
   * @return Common result.
   * @throws javax.mail.MessagingException if any.
   * @throws java.io.UnsupportedEncodingException if any.
   */
  Result bindEmail(String email) throws MessagingException, UnsupportedEncodingException;

  /**
   * Send token to yur email for verifying, no need to login first.
   *
   * @param email Email to receive token.
   * @param type Token type, 1 for sign up, 2 for reset password.
   * @return Common result.
   * @throws javax.mail.MessagingException javax.mail.MessagingException.
   * @throws java.io.UnsupportedEncodingException java.io.UnsupportedEncodingException.
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
   * Verify token for binding user's email, need to log in first.
   *
   * @param bindEmailDTO DTO for bind email.
   * @return Common result.
   */
  Result verifyBindEmailToken(BindEmailDTO bindEmailDTO);

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
