package com.daw.pms.Service.PMS.impl;

import cn.hutool.captcha.generator.RandomGenerator;
import com.daw.pms.Config.UserRole;
import com.daw.pms.DTO.*;
import com.daw.pms.Entity.OAuth2.GitHubOAuth2User;
import com.daw.pms.Entity.OAuth2.GoogleOAuth2User;
import com.daw.pms.Entity.PMS.PMSUserDetails;
import com.daw.pms.Service.PMS.LoginService;
import com.daw.pms.Service.PMS.UserService;
import com.daw.pms.Utils.EmailUtil;
import com.daw.pms.Utils.HttpTools;
import com.daw.pms.Utils.PmsUserDetailsUtil;
import com.daw.pms.Utils.RegistrationCodeUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.mail.MessagingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

/**
 * Service for handle register, login and logout.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/6/23
 */
@Service
public class LoginServiceImpl implements LoginService {
  @Value("${pms.registration-code:PMSDAW}")
  private String rootRegistrationCode;

  private final HttpTools httpTools;
  private final PmsUserDetailsUtil pmsUserDetailsUtil;
  private final RedisTemplate<String, Object> redisTemplate;
  private final PasswordEncoder passwordEncoder;
  private final EmailUtil emailUtil;
  private final RegistrationCodeUtil registrationCodeUtil;
  private final DaoAuthenticationProvider authenticationProvider;
  private final UserService userService;
  private final OAuth2UserDetailsServiceImpl OAuth2UserDetailsServiceImpl;
  private final SessionRegistry sessionRegistry;
  private final ClientRegistrationRepository clientRegistrationRepository;

  /**
   * Constructor for LoginServiceImpl.
   *
   * @param httpTools Http tools.
   * @param pmsUserDetailsUtil PmsUserDetailsUtil.
   * @param redisTemplate RedisTemplate.
   * @param passwordEncoder PasswordEncoder.
   * @param emailUtil EmailUtil.
   * @param registrationCodeUtil RegistrationCodeUtil.
   * @param authenticationProvider DaoAuthenticationProvider.
   * @param userService UserService object.
   * @param OAuth2UserDetailsServiceImpl OAuth2UserDetailsServiceImpl.
   * @param sessionRegistry SessionRegistry.
   * @param clientRegistrationRepository ClientRegistrationRepository.
   */
  public LoginServiceImpl(
      HttpTools httpTools,
      PmsUserDetailsUtil pmsUserDetailsUtil,
      RedisTemplate<String, Object> redisTemplate,
      PasswordEncoder passwordEncoder,
      EmailUtil emailUtil,
      RegistrationCodeUtil registrationCodeUtil,
      DaoAuthenticationProvider authenticationProvider,
      UserService userService,
      OAuth2UserDetailsServiceImpl OAuth2UserDetailsServiceImpl,
      SessionRegistry sessionRegistry,
      ClientRegistrationRepository clientRegistrationRepository) {
    this.httpTools = httpTools;
    this.pmsUserDetailsUtil = pmsUserDetailsUtil;
    this.redisTemplate = redisTemplate;
    this.passwordEncoder = passwordEncoder;
    this.emailUtil = emailUtil;
    this.registrationCodeUtil = registrationCodeUtil;
    this.authenticationProvider = authenticationProvider;
    this.userService = userService;
    this.OAuth2UserDetailsServiceImpl = OAuth2UserDetailsServiceImpl;
    this.sessionRegistry = sessionRegistry;
    this.clientRegistrationRepository = clientRegistrationRepository;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Login to playlist master by email and password.
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
    LoginResult loginResult = new LoginResult();
    PMSUserDetails pmsUserDetails = (PMSUserDetails) authenticate.getPrincipal();
    loginResult.setId(pmsUserDetails.getUser().getId());
    loginResult.setCookie(request.getSession().getId());
    return Result.ok(loginResult);
  }

  /**
   * {@inheritDoc}
   *
   * <p>Login to playlist master by GitHub.
   */
  @Override
  public Result loginByGitHub(String code, String registrationCode, HttpServletRequest request) {
    //    System.out.println("GitHub authorization code: " + code);
    ClientRegistration github = clientRegistrationRepository.findByRegistrationId("github");
    String access_token_url = github.getProviderDetails().getTokenUri();
    access_token_url += "?client_id=" + github.getClientId();
    access_token_url += "&client_secret=" + github.getClientSecret();
    access_token_url += "&code=" + code;
    String tokenResponse =
        httpTools.requestGetAPIByFinalUrlWithProxy(
            access_token_url, new HttpHeaders(), Optional.empty());
    Map<String, String> tokenResponseMap =
        Arrays.stream(tokenResponse.split("&"))
            .collect(
                Collectors.toMap(
                    s -> s.split("=")[0], s -> s.split("=").length == 1 ? "" : s.split("=")[1]));
    String accessToken = tokenResponseMap.get("access_token");
    String refreshToken = tokenResponseMap.get("refresh_token");
    Instant issuedAt = Instant.now();
    int expiresIn = Integer.parseInt(tokenResponseMap.get("expires_in"));
    Instant expiresAt = issuedAt.plusSeconds(expiresIn);
    SecurityContext securityContext = SecurityContextHolder.getContext();

    OAuth2AccessToken oAuth2AccessToken =
        new OAuth2AccessToken(
            OAuth2AccessToken.TokenType.BEARER,
            accessToken,
            issuedAt,
            expiresAt,
            github.getScopes());

    OAuth2UserRequest oAuth2UserRequest = new OAuth2UserRequest(github, oAuth2AccessToken);
    GitHubOAuth2User oAuth2User =
        (GitHubOAuth2User) OAuth2UserDetailsServiceImpl.loadUser(oAuth2UserRequest);
    boolean isUsernameExists = userService.checkIfPMSUserNameExist(oAuth2User.getName(), 1);
    Result result;
    if (!isUsernameExists) {
      // Need to input registration code.
      if ("".equals(registrationCode)) {
        LoginResult loginResult = new LoginResult();
        loginResult.setUserExists(false);
        return Result.ok(loginResult);
      }

      // Validate registration code.
      if (!rootRegistrationCode.equals(registrationCode)) {
        return Result.fail("Invalid registration code.");
      }

      UserDTO userDTO = new UserDTO();
      userDTO.setName(oAuth2User.getName());
      userDTO.setRole("ROLE_" + UserRole.USER);
      userDTO.setEnabled(true);
      userDTO.setLoginType(1);
      userDTO.setEmail(oAuth2User.getEmail());
      userDTO.setAvatar(oAuth2User.getAvatar());

      Long newUserId = Long.valueOf(userService.addUser(userDTO).getData().toString());
      String newUserCookie = request.getSession().getId();
      LoginResult loginResult = new LoginResult();
      loginResult.setId(newUserId);
      loginResult.setCookie(newUserCookie);
      loginResult.setUserExists(true);
      oAuth2User.setId(newUserId);
      // Store oauth2 token.
      OAuth2AuthenticationToken oAuth2AuthenticationToken =
          new OAuth2AuthenticationToken(
              oAuth2User, oAuth2User.getAuthorities(), github.getRegistrationId());
      securityContext.setAuthentication(oAuth2AuthenticationToken);
      sessionRegistry.registerNewSession(newUserCookie, oAuth2AuthenticationToken.getPrincipal());
      result = Result.ok(loginResult);
    } else {
      Long userId = oAuth2User.getId();
      HashSet<GrantedAuthority> authorities = new HashSet<>(oAuth2User.getAuthorities());
      Result basicPMSUserInfo = userService.getBasicPMSUserInfo(userId);
      BasicPMSUserInfoDTO storedUserInfo = (BasicPMSUserInfoDTO) basicPMSUserInfo.getData();

      // Add additional role for user from database.
      String storedUserInfoRole = storedUserInfo.getRole();
      if (!("ROLE_" + UserRole.USER).equals(storedUserInfoRole)) {
        SimpleGrantedAuthority storedUserRoleAuthority =
            new SimpleGrantedAuthority(storedUserInfoRole);
        authorities.add(storedUserRoleAuthority);
      }
      String newUserCookie = request.getSession().getId();
      // Store oauth2 token.
      OAuth2AuthenticationToken oAuth2AuthenticationToken =
          new OAuth2AuthenticationToken(oAuth2User, authorities, github.getRegistrationId());
      securityContext.setAuthentication(oAuth2AuthenticationToken);
      sessionRegistry.registerNewSession(newUserCookie, oAuth2AuthenticationToken.getPrincipal());
      // Update the oauth2 user info if changed.
      String newUserName = oAuth2User.getName();
      String newUserEmail = oAuth2User.getEmail();
      String newUserAvatar = oAuth2User.getAvatar();
      if (!(newUserName.equals(storedUserInfo.getName())
          && newUserEmail.equals(storedUserInfo.getEmail())
          && newUserAvatar.equals(storedUserInfo.getAvatar()))) {
        Result updateBasicPMSUserInfoResult =
            userService.updateBasicPMSUserInfo(userId, newUserName, newUserEmail, newUserAvatar);
        if (!updateBasicPMSUserInfoResult.getSuccess()) {
          throw new RuntimeException(updateBasicPMSUserInfoResult.getMessage());
        }
      }
      LoginResult loginResult = new LoginResult();
      loginResult.setId(userId);
      loginResult.setCookie(newUserCookie);
      loginResult.setUserExists(true);
      result = Result.ok(loginResult);
    }
    return result;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Login to playlist master by Google.
   */
  @Override
  public Result loginByGoogle(String code, String registrationCode, HttpServletRequest request) {
    //    System.out.println("Google authorization code: " + code);
    ClientRegistration google = clientRegistrationRepository.findByRegistrationId("google");
    String access_token_url = google.getProviderDetails().getTokenUri();
    access_token_url += "?client_id=" + google.getClientId();
    access_token_url += "&client_secret=" + google.getClientSecret();
    access_token_url += "&grant_type=authorization_code";
    access_token_url += "&code=" + code;
    access_token_url += "&redirect_uri=" + google.getRedirectUri();
    String tokenResponse =
        httpTools.requestPostAPIByFinalUrlWithProxy(
            access_token_url, new HttpHeaders(), Optional.empty());
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(tokenResponse);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    String accessToken = jsonNode.get("access_token").textValue();
    //    System.out.println("Access token: " + accessToken);
    int expiresIn = jsonNode.get("expires_in").intValue();
    //    String refreshToken = jsonNode.get("refresh_token").textValue();
    Set<String> scopes = new HashSet<>(Arrays.asList(jsonNode.get("scope").textValue().split(" ")));
    String tokenType = jsonNode.get("token_type").textValue();
    String idToken = jsonNode.get("id_token").textValue();
    Instant issuedAt = Instant.now();
    Instant expiresAt = issuedAt.plusSeconds(expiresIn);
    //    System.out.println("Issued at: " + issuedAt);
    //    System.out.println("Expires at: " + expiresAt);

    SecurityContext securityContext = SecurityContextHolder.getContext();
    OAuth2AccessToken oAuth2AccessToken =
        new OAuth2AccessToken(
            OAuth2AccessToken.TokenType.BEARER, accessToken, issuedAt, expiresAt, scopes);

    OAuth2UserRequest oAuth2UserRequest = new OAuth2UserRequest(google, oAuth2AccessToken);
    GoogleOAuth2User oAuth2User =
        (GoogleOAuth2User) OAuth2UserDetailsServiceImpl.loadUser(oAuth2UserRequest);
    boolean isUsernameExists = userService.checkIfPMSUserNameExist(oAuth2User.getName(), 2);
    Result result;
    if (!isUsernameExists) {
      // Need to input registration code.
      if ("".equals(registrationCode)) {
        LoginResult loginResult = new LoginResult();
        loginResult.setUserExists(false);
        return Result.ok(loginResult);
      }

      // Validate registration code.
      if (!rootRegistrationCode.equals(registrationCode)) {
        return Result.fail("Invalid registration code.");
      }

      UserDTO userDTO = new UserDTO();
      userDTO.setName(oAuth2User.getName());
      userDTO.setRole("ROLE_" + UserRole.USER);
      userDTO.setEnabled(true);
      userDTO.setLoginType(2);
      userDTO.setEmail(oAuth2User.getEmail());
      userDTO.setAvatar(oAuth2User.getAvatar());
      Long newUserId = Long.valueOf(userService.addUser(userDTO).getData().toString());
      oAuth2User.setId(newUserId);
      // Store oauth2 token.
      OAuth2AuthenticationToken oAuth2AuthenticationToken =
          new OAuth2AuthenticationToken(
              oAuth2User, oAuth2User.getAuthorities(), google.getRegistrationId());
      securityContext.setAuthentication(oAuth2AuthenticationToken);
      String newUserCookie = request.getSession().getId();
      sessionRegistry.registerNewSession(newUserCookie, oAuth2AuthenticationToken.getPrincipal());
      LoginResult loginResult = new LoginResult();
      loginResult.setId(newUserId);
      loginResult.setCookie(newUserCookie);
      loginResult.setUserExists(true);
      result = Result.ok(loginResult);
    } else {
      // Store oauth2 token.
      String newUserCookie = request.getSession().getId();
      OAuth2AuthenticationToken oAuth2AuthenticationToken =
          new OAuth2AuthenticationToken(
              oAuth2User, oAuth2User.getAuthorities(), google.getRegistrationId());
      securityContext.setAuthentication(oAuth2AuthenticationToken);
      sessionRegistry.registerNewSession(newUserCookie, oAuth2AuthenticationToken.getPrincipal());
      Long currentLoginUserId = pmsUserDetailsUtil.getCurrentLoginUserId();

      // Update the oauth2 user info if changed.
      Result basicPMSUserInfo = userService.getBasicPMSUserInfo(currentLoginUserId);
      BasicPMSUserInfoDTO storedUserInfo = (BasicPMSUserInfoDTO) basicPMSUserInfo.getData();
      String newUserName = oAuth2User.getName();
      String newUserEmail = oAuth2User.getEmail();
      String newUserAvatar = oAuth2User.getAvatar();
      if (!(newUserName.equals(storedUserInfo.getName())
          && newUserEmail.equals(storedUserInfo.getEmail())
          && newUserAvatar.equals(storedUserInfo.getAvatar()))) {
        Result updateBasicPMSUserInfoResult =
            userService.updateBasicPMSUserInfo(
                currentLoginUserId, newUserName, newUserEmail, newUserAvatar);
        if (!updateBasicPMSUserInfoResult.getSuccess()) {
          throw new RuntimeException(updateBasicPMSUserInfoResult.getMessage());
        }
      }
      LoginResult loginResult = new LoginResult();
      loginResult.setId(currentLoginUserId);
      loginResult.setCookie(newUserCookie);
      loginResult.setUserExists(true);
      result = Result.ok(loginResult);
    }
    return result;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Register playlist master account using email and password.
   */
  @Override
  public Result register(RegisterFormDTO registerFormDTO)
      throws MessagingException, UnsupportedEncodingException {
    String registrationCode = registerFormDTO.getRegistrationCode();
    // Validate registration code.
    if (!rootRegistrationCode.equals(registrationCode)) {
      return Result.fail("Invalid registration code.");
    }
    //    long invitorId = registrationCodeUtil.getUserIdByRegistrationCode(registrationCode);\

    boolean isUsernameExists = userService.checkIfPMSUserNameExist(registerFormDTO.getName(), 0);
    if (isUsernameExists) {
      return Result.fail("Name already exists, please change username.");
    }

    boolean isEmailAddressExists =
        userService.checkIfEmailAddressExist(registerFormDTO.getEmail(), 0);
    if (isEmailAddressExists) {
      return Result.fail("Email already exists, please login.");
    }

    boolean isPhoneNumberExists =
        userService.checkIfPhoneNumberExist(registerFormDTO.getPhoneNumber(), 0);
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
   * {@inheritDoc}
   *
   * <p>Logout current pms user.
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
   * {@inheritDoc}
   *
   * <p>Forget user's password, send verifying code to user's email, only used when user have login.
   */
  @Override
  public Result forgotPassword() throws MessagingException, UnsupportedEncodingException {
    Long currentUserId = pmsUserDetailsUtil.getCurrentLoginUserId();
    String currentUserEmail = pmsUserDetailsUtil.getCurrentLoginUserEmail();
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
   * {@inheritDoc}
   *
   * <p>Bind email, send verifying code to user's email, need login first.
   */
  @Override
  public Result bindEmail(String email) throws MessagingException, UnsupportedEncodingException {
    Long currentUserId = pmsUserDetailsUtil.getCurrentLoginUserId();

    // Generate verify token.
    RandomGenerator randomGenerator =
        new RandomGenerator("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789", 8);
    String token = randomGenerator.generate();

    // Store token to redis.
    String tokenKey = "bind_email_token::" + currentUserId;
    redisTemplate.opsForValue().set(tokenKey, token, 10, TimeUnit.MINUTES);

    // Send token to user's email.
    emailUtil.sendResetPassEmail(email, token);
    return Result.ok(
        "Please check your email, we have send the verified code to your email: "
            + email
            + ", the token will expire after 10 minutes.");
  }

  /**
   * {@inheritDoc}
   *
   * <p>Send token to yur email for verifying, no need to login first.
   */
  @Override
  public Result sendVerifyToken(String email, Integer type)
      throws MessagingException, UnsupportedEncodingException {
    if (type == 2) {
      boolean userExisted = userService.identifyUserByEmail(email, 0);
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
   * {@inheritDoc}
   *
   * <p>Verify token for resetting user's password, need to log in first.
   */
  @Override
  public Result verifyResetPassToken(ResetPassDTO resetPassDTO) {
    String newPass = resetPassDTO.getPassword();
    String repeatedNewPass = resetPassDTO.getRepeatedPassword();
    String token = resetPassDTO.getToken();
    if (!newPass.equals(repeatedNewPass)) {
      return Result.fail("Passwords don't match, please check your password");
    }

    Long currentUserId = pmsUserDetailsUtil.getCurrentLoginUserId();
    String tokenKey = "reset_pass_token::" + currentUserId;
    Object realToken = redisTemplate.opsForValue().get(tokenKey);
    if (realToken == null) {
      return Result.fail("Token has expired, please resend verified token");
    }

    if (!realToken.equals(token)) {
      return Result.fail("Wrong token");
    }

    redisTemplate.delete(tokenKey);

    return userService.updatePassword(currentUserId, newPass);
  }

  /**
   * {@inheritDoc}
   *
   * <p>Verify token for binding user's email, need to log in first.
   */
  @Override
  public Result verifyBindEmailToken(BindEmailDTO bindEmailDTO) {
    String email = bindEmailDTO.getEmail();
    String token = bindEmailDTO.getToken();

    Long currentUserId = pmsUserDetailsUtil.getCurrentLoginUserId();

    //    boolean emailAddressExist = userService.checkIfEmailAddressExist(email);
    //    if (emailAddressExist) {
    //      return Result.fail("Email has been used");
    //    }

    String tokenKey = "bind_email_token::" + currentUserId;
    Object realToken = redisTemplate.opsForValue().get(tokenKey);
    if (realToken == null) {
      return Result.fail("Token has expired, please resend verified token");
    }

    if (!realToken.equals(token)) {
      return Result.fail("Wrong token");
    }

    redisTemplate.delete(tokenKey);

    return userService.updateEmail(currentUserId, email);
  }

  /**
   * {@inheritDoc}
   *
   * <p>Verify token for resetting user's password, don't need to log in first.
   */
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

    redisTemplate.delete(tokenKey);

    Long currentUserId = userService.getUserIdByEmail(email, 0);
    return userService.updatePassword(currentUserId, newPass);
  }

  /**
   * {@inheritDoc}
   *
   * <p>Verify token for create new account, don't need to log in first.
   */
  @Override
  public Result verifySignUpTokenWithoutLogin(SignUpNologinDTO signUpNologinDTO) {
    String registrationCode = signUpNologinDTO.getRegistrationCode();
    if (!rootRegistrationCode.equals(registrationCode)) {
      return Result.fail("Invalid registration code.");
    }
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

    redisTemplate.delete(tokenKey);

    String encodedPassword = passwordEncoder.encode(signUpNologinDTO.getPassword());
    UserDTO userDTO = new UserDTO();
    userDTO.setName(signUpNologinDTO.getName());
    userDTO.setPass(encodedPassword);
    userDTO.setRole("ROLE_" + UserRole.USER);
    userDTO.setEnabled(true);
    userDTO.setLoginType(0);
    userDTO.setEmail(signUpNologinDTO.getEmail());
    userDTO.setPhone(signUpNologinDTO.getPhoneNumber());
    return userService.addUser(userDTO);
  }
}
