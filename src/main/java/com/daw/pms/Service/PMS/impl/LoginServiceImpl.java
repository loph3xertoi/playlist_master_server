package com.daw.pms.Service.PMS.impl;

import cn.hutool.captcha.generator.RandomGenerator;
import com.daw.pms.Config.UserRole;
import com.daw.pms.DTO.*;
import com.daw.pms.Entity.PMS.PMSUserDetails;
import com.daw.pms.Service.PMS.LoginService;
import com.daw.pms.Service.PMS.UserService;
import com.daw.pms.Utils.EmailUtil;
import com.daw.pms.Utils.PMSUserDetailsUtil;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;
import javax.mail.MessagingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {
  private final RedisTemplate<String, Object> redisTemplate;
  private final PasswordEncoder passwordEncoder;
  private final EmailUtil emailUtil;
  private final DaoAuthenticationProvider authenticationProvider;
  private final UserService userService;
  private final SessionRegistry sessionRegistry;

  public LoginServiceImpl(
      RedisTemplate<String, Object> redisTemplate,
      PasswordEncoder passwordEncoder,
      EmailUtil emailUtil,
      DaoAuthenticationProvider authenticationProvider,
      UserService userService,
      SessionRegistry sessionRegistry) {
    this.redisTemplate = redisTemplate;
    this.passwordEncoder = passwordEncoder;
    this.emailUtil = emailUtil;
    this.authenticationProvider = authenticationProvider;
    this.userService = userService;
    this.sessionRegistry = sessionRegistry;
  }

  /**
   * Login to playlist master.
   *
   * @param loginFormDTO Login info.
   * @param request Http servlet request.
   * @return Result whose data is user's id in pms.
   */
  @Override
  public Result login(LoginFormDTO loginFormDTO, HttpServletRequest request) {
    Authentication authenticate =
        authenticationProvider.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginFormDTO.getEmail(), loginFormDTO.getPassword()));
    SecurityContext securityContext = SecurityContextHolder.getContext();
    securityContext.setAuthentication(authenticate);
    sessionRegistry.registerNewSession(request.getSession().getId(), authenticate.getPrincipal());
    PMSUserDetails pmsUserDetails = (PMSUserDetails) authenticate.getPrincipal();
    return Result.ok(pmsUserDetails.getUser().getId());
  }

  /**
   * Register playlist master account.
   *
   * @param registerFormDTO Register form dto.
   * @return Registered user's id in pms if success.
   */
  @Override
  public Result register(RegisterFormDTO registerFormDTO)
      throws MessagingException, UnsupportedEncodingException {
    boolean isUsernameExists = userService.checkIfPMSUserNameExist(registerFormDTO.getName());
    if (isUsernameExists) {
      return Result.fail("Name already exists, please change username.");
    }

    boolean isEmailAddressExists = userService.checkIfEmailAddressExist(registerFormDTO.getEmail());
    if (isEmailAddressExists) {
      return Result.fail("Email already exists, please login.");
    }

    boolean isPhoneNumberExists =
        userService.checkIfPhoneNumberExist(registerFormDTO.getPhoneNumber());
    if (isPhoneNumberExists) {
      return Result.fail("Phone number already exists, please change phone number.");
    }

    String currentUserEmail = registerFormDTO.getEmail();

    // Generate verify token.
    RandomGenerator randomGenerator =
        new RandomGenerator("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789", 8);
    String token = randomGenerator.generate();

    // Store token to redis.
    String tokenKey = "sign_up_token::" + currentUserEmail;
    redisTemplate.opsForValue().set(tokenKey, token, 10, TimeUnit.MINUTES);

    // Send token to user's email.
    emailUtil.sendSignUpEmail(currentUserEmail, token);
    return Result.ok(
        "Please check your email, we have send the verified code to your email: "
            + currentUserEmail
            + ", the token will expire after 10 minutes.");
  }

  /**
   * Logout current pms user.
   *
   * @param request Http servlet request.
   * @return Result for logout.
   */
  @Override
  public Result logout(HttpServletRequest request) {
    for (Cookie cookie : request.getCookies()) {
      if ("JSESSIONID".equals(cookie.getName())) {
        sessionRegistry.removeSessionInformation(cookie.getValue());
        return Result.ok();
      }
    }
    return Result.ok();
  }

  /**
   * Forget user's password, send verifying code to user's email, only used when user have login.
   *
   * @return Common result.
   */
  @Override
  public Result forgotPassword() throws MessagingException, UnsupportedEncodingException {
    Long currentUserId = PMSUserDetailsUtil.getCurrentLoginUserId();
    String currentUserEmail = PMSUserDetailsUtil.getCurrentLoginUserEmail();
    if (currentUserEmail == null || currentUserEmail.isEmpty()) {
      return Result.fail("Need to bind email first");
    }

    // Generate verify token.
    RandomGenerator randomGenerator =
        new RandomGenerator("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789", 8);
    String token = randomGenerator.generate();

    // Store token to redis.
    String tokenKey = "reset_pass_token::" + currentUserId;
    redisTemplate.opsForValue().set(tokenKey, token, 10, TimeUnit.MINUTES);

    // Send token to user's email.
    emailUtil.sendResetPassEmail(currentUserEmail, token);
    return Result.ok(
        "Please check your email, we have send the verified code to your email: "
            + currentUserEmail
            + ", the token will expire after 10 minutes.");
  }

  /**
   * Send token to yur email for verifying, no need to login first.
   *
   * @param email Email to receive token.
   * @param type Token type, 1 for sign up, 2 for reset password.
   * @return Common result.
   * @throws MessagingException MessagingException.
   * @throws UnsupportedEncodingException UnsupportedEncodingException.
   */
  @Override
  public Result sendVerifyToken(String email, Integer type)
      throws MessagingException, UnsupportedEncodingException {
    if (type == 2) {
      boolean userExisted = userService.identifyUserByEmail(email);
      if (!userExisted) {
        return Result.fail("No user found binds this email");
      }
    }

    // Generate verify token.
    RandomGenerator randomGenerator =
        new RandomGenerator("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789", 8);
    String token = randomGenerator.generate();

    // Store token to redis.
    String tokenKey;
    if (type == 1) {
      tokenKey = "sign_up_token::" + email;
      // Send sign up token to user's email.
      emailUtil.sendSignUpEmail(email, token);
    } else if (type == 2) {
      tokenKey = "reset_pass_token::" + email;
      // Send reset password token to user's email.
      emailUtil.sendResetPassEmail(email, token);
    } else {
      throw new RuntimeException("Invalid token type");
    }
    redisTemplate.opsForValue().set(tokenKey, token, 10, TimeUnit.MINUTES);

    return Result.ok(
        "Please check your email, we have send the verified code to your email: "
            + email
            + ", the token will expire after 10 minutes.");
  }

  /**
   * Verify token for resetting user's password.
   *
   * @param resetPassDTO@return Common result.
   */
  @Override
  public Result verifyResetPassToken(ResetPassDTO resetPassDTO) {
    String newPass = resetPassDTO.getPassword();
    String repeatedNewPass = resetPassDTO.getRepeatedPassword();
    String token = resetPassDTO.getToken();
    if (!newPass.equals(repeatedNewPass)) {
      return Result.fail("Passwords don't match, please check your password");
    }

    Long currentUserId = PMSUserDetailsUtil.getCurrentLoginUserId();
    String tokenKey = "reset_pass_token::" + currentUserId;
    Object realToken = redisTemplate.opsForValue().get(tokenKey);
    if (realToken == null) {
      return Result.fail("Token has expired, please resend verified token");
    }

    if (!realToken.equals(token)) {
      return Result.fail("Wrong token");
    }

    return userService.updatePassword(currentUserId, newPass);
  }

  @Override
  public Result verifyResetPassTokenWithoutLogin(ResetPassNologinDTO resetPassNologinDTO) {
    String email = resetPassNologinDTO.getEmail();
    String token = resetPassNologinDTO.getToken();
    String newPass = resetPassNologinDTO.getPassword();
    String repeatedNewPass = resetPassNologinDTO.getRepeatedPassword();
    // Reset password.
    if (!newPass.equals(repeatedNewPass)) {
      return Result.fail("Passwords don't match, please check your password");
    }

    String tokenKey = "reset_pass_token::" + email;
    Object realToken = redisTemplate.opsForValue().get(tokenKey);
    if (realToken == null) {
      return Result.fail("Token has expired, please resend verified token");
    }

    if (!realToken.equals(token)) {
      return Result.fail("Wrong token");
    }

    Long currentUserId = userService.getUserIdByEmail(email);
    return userService.updatePassword(currentUserId, newPass);
  }

  /**
   * Verify token for create new account, don't need to log in first.
   *
   * @param signUpNologinDTO Sign up dto.
   * @return Common result.
   */
  @Override
  public Result verifySignUpTokenWithoutLogin(SignUpNologinDTO signUpNologinDTO) {
    String token = signUpNologinDTO.getToken();
    String email = signUpNologinDTO.getEmail();
    String tokenKey = "sign_up_token::" + email;
    Object realToken = redisTemplate.opsForValue().get(tokenKey);
    if (realToken == null) {
      return Result.fail("Token has expired, please resend verified token");
    }

    if (!realToken.equals(token)) {
      return Result.fail("Wrong token");
    }

    String encodedPassword = passwordEncoder.encode(signUpNologinDTO.getPassword());
    UserDTO userDTO = new UserDTO();
    userDTO.setName(signUpNologinDTO.getName());
    userDTO.setPass(encodedPassword);
    userDTO.setRole("ROLE_" + UserRole.USER);
    userDTO.setEnabled(true);
    userDTO.setEmail(signUpNologinDTO.getEmail());
    userDTO.setPhone(signUpNologinDTO.getPhoneNumber());
    return userService.addUser(userDTO);
  }
}
