package com.daw.pms.Service.PMS.impl;

import cn.hutool.captcha.generator.RandomGenerator;
import com.daw.pms.Config.UserRole;
import com.daw.pms.DTO.LoginFormDTO;
import com.daw.pms.DTO.RegisterFormDTO;
import com.daw.pms.DTO.Result;
import com.daw.pms.DTO.UserDTO;
import com.daw.pms.Entity.PMS.PMSUserDetails;
import com.daw.pms.Service.PMS.LoginService;
import com.daw.pms.Service.PMS.UserService;
import com.daw.pms.Utils.EmailUtil;
import com.daw.pms.Utils.PMSUserDetailsUtil;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;
import javax.mail.MessagingException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {
  private final RedisTemplate<String, Object> redisTemplate;
  private final PasswordEncoder passwordEncoder;
  private final EmailUtil emailUtil;
  private final DaoAuthenticationProvider authenticationProvider;
  private final UserService userService;

  public LoginServiceImpl(
      RedisTemplate<String, Object> redisTemplate,
      PasswordEncoder passwordEncoder,
      EmailUtil emailUtil,
      DaoAuthenticationProvider authenticationProvider,
      UserService userService) {
    this.redisTemplate = redisTemplate;
    this.passwordEncoder = passwordEncoder;
    this.emailUtil = emailUtil;
    this.authenticationProvider = authenticationProvider;
    this.userService = userService;
  }

  /**
   * Login to playlist master.
   *
   * @param loginFormDTO Login info.
   * @return Result whose data is user's id in pms.
   */
  @Override
  public Result login(LoginFormDTO loginFormDTO) {
    Authentication authenticate =
        authenticationProvider.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginFormDTO.getName(), loginFormDTO.getPassword()));
    SecurityContext securityContext = SecurityContextHolder.getContext();
    securityContext.setAuthentication(authenticate);
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
  public Result register(RegisterFormDTO registerFormDTO) {
    boolean isUsernameExists = userService.checkIfPMSUserNameExist(registerFormDTO.getName());
    if (isUsernameExists) {
      return Result.fail("Name already exists, please change username.");
    }

    boolean isEmailAddressExists = userService.checkIfEmailAddressExist(registerFormDTO.getEmail());
    if (isEmailAddressExists) {
      return Result.fail("Email already exists, please login.");
    }

    String encodedPassword = passwordEncoder.encode(registerFormDTO.getPassword());
    UserDTO userDTO = new UserDTO();
    userDTO.setName(registerFormDTO.getName());
    userDTO.setPass(encodedPassword);
    userDTO.setRole("ROLE_" + UserRole.USER);
    userDTO.setEnabled(true);
    userDTO.setEmail(registerFormDTO.getEmail());
    return userService.addUser(userDTO);
  }

  /**
   * Logout current pms user.
   *
   * @return Result for logout.
   */
  @Override
  public Result logout() {
    return null;
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
    emailUtil.sendEmail(currentUserEmail, token);
    return Result.ok(
        "Please check your email, we have send the verified code to your email: "
            + currentUserEmail
            + ", the token will expire after 10 minutes.");
  }

  /**
   * Forget user's password, send verifying code to user's email, no need to login.
   *
   * @return Common result.
   */
  @Override
  public Result forgotPasswordByEmail(String email)
      throws MessagingException, UnsupportedEncodingException {
    boolean userExisted = userService.identifyUserByEmail(email);
    if (!userExisted) {
      return Result.fail("No user found binds this email");
    }

    // Generate verify token.
    RandomGenerator randomGenerator =
        new RandomGenerator("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789", 8);
    String token = randomGenerator.generate();

    // Store token to redis.
    String tokenKey = "reset_pass_token::" + email;
    redisTemplate.opsForValue().set(tokenKey, token, 10, TimeUnit.MINUTES);

    // Send token to user's email.
    emailUtil.sendEmail(email, token);
    return Result.ok(
        "Please check your email, we have send the verified code to your email: "
            + email
            + ", the token will expire after 10 minutes.");
  }

  /**
   * Verify token for resetting user's password.
   *
   * @param token Token user input.
   * @return Common result.
   */
  @Override
  public Result verifyResetPassToken(String newPass, String repeatedNewPass, String token) {
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
  public Result verifyResetPassTokenWithoutLogin(
      String newPass, String repeatedNewPass, String token, String email) {
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
}
