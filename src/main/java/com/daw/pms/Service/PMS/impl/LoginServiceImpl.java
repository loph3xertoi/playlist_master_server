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
import com.daw.pms.Utils.PMSUserDetailsUtil;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
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

@Service
public class LoginServiceImpl implements LoginService {
  private final HttpTools httpTools;
  private final RedisTemplate<String, Object> redisTemplate;
  private final PasswordEncoder passwordEncoder;
  private final EmailUtil emailUtil;
  private final DaoAuthenticationProvider authenticationProvider;
  private final UserService userService;
  private final OAuth2UserDetailsServiceImpl OAuth2UserDetailsServiceImpl;
  private final SessionRegistry sessionRegistry;
  private final ClientRegistrationRepository clientRegistrationRepository;

  public LoginServiceImpl(
      HttpTools httpTools,
      RedisTemplate<String, Object> redisTemplate,
      PasswordEncoder passwordEncoder,
      EmailUtil emailUtil,
      DaoAuthenticationProvider authenticationProvider,
      UserService userService,
      OAuth2UserDetailsServiceImpl OAuth2UserDetailsServiceImpl,
      SessionRegistry sessionRegistry,
      ClientRegistrationRepository clientRegistrationRepository) {
    this.httpTools = httpTools;
    this.redisTemplate = redisTemplate;
    this.passwordEncoder = passwordEncoder;
    this.emailUtil = emailUtil;
    this.authenticationProvider = authenticationProvider;
    this.userService = userService;
    this.OAuth2UserDetailsServiceImpl = OAuth2UserDetailsServiceImpl;
    this.sessionRegistry = sessionRegistry;
    this.clientRegistrationRepository = clientRegistrationRepository;
  }

  /**
   * Login to playlist master by email and password.
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
   * Login to playlist master by GitHub.
   *
   * @param code Authorization code.
   * @param request Http servlet request.
   * @return Result whose data is user's id in pms.
   */
  @Override
  public Result loginByGitHub(String code, HttpServletRequest request) {
    //    System.out.println("Authorization code: " + code);
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
    //    System.out.println("Access token: " + accessToken);
    Instant issuedAt = Instant.now();
    int expiresIn = Integer.parseInt(tokenResponseMap.get("expires_in"));
    Instant expiresAt = issuedAt.plusSeconds(expiresIn);
    SecurityContext securityContext = SecurityContextHolder.getContext();
    //    System.out.println("Issued at: " + issuedAt);
    //    System.out.println("Expires at: " + expiresAt);
    //    CompletableFuture<String> refreshTokenResponseFuture =
    //        CompletableFuture.supplyAsync(
    //            () -> {
    //              int delay = expiresIn - 180;
    //              try {
    //                String refresh_token_url = github.getProviderDetails().getTokenUri();
    //                refresh_token_url += "?client_id=" + github.getClientId();
    //                refresh_token_url += "&client_secret=" + github.getClientSecret();
    //                refresh_token_url += "&grant_type=" + "refresh_token";
    //                refresh_token_url += "&refresh_token=" + refreshToken;
    //                //                TimeUnit.SECONDS.sleep(10);
    //                TimeUnit.SECONDS.sleep(delay);
    //                return httpTools.requestPostAPIByFinalUrlWithProxy(
    //                    refresh_token_url, new HttpHeaders(), Optional.empty());
    //              } catch (InterruptedException e) {
    //                throw new RuntimeException(e);
    //              }
    //            });
    //
    //    // if success:
    //    refreshTokenResponseFuture.thenAccept(
    //        (result) -> {
    //          Map<String, String> refreshTokenResponseMap =
    //              Arrays.stream(result.split("&"))
    //                  .collect(
    //                      Collectors.toMap(
    //                          s -> s.split("=")[0],
    //                          s -> s.split("=").length == 1 ? "" : s.split("=")[1]));
    //          String newAccessToken = refreshTokenResponseMap.get("access_token");
    //          Instant newIssuedAt = Instant.now();
    //          int newExpiresIn = Integer.parseInt(refreshTokenResponseMap.get("expires_in"));
    //          Instant newExpiresAt = newIssuedAt.plusSeconds(newExpiresIn);
    //          OAuth2AccessToken newOAuth2AccessToken =
    //              new OAuth2AccessToken(
    //                  OAuth2AccessToken.TokenType.BEARER,
    //                  newAccessToken,
    //                  newIssuedAt,
    //                  newExpiresAt,
    //                  github.getScopes());
    //          Authentication authentication = securityContext.getAuthentication();
    //          GitHubOAuth2User oAuth2User = (GitHubOAuth2User) authentication.getPrincipal();
    //          oAuth2User.setOauth2AccessToken(newOAuth2AccessToken);
    //        });
    //
    //    // if fail
    //    refreshTokenResponseFuture.exceptionally(
    //        (e) -> {
    //          e.printStackTrace();
    //          return null;
    //        });

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
      UserDTO userDTO = new UserDTO();
      userDTO.setName(oAuth2User.getName());
      userDTO.setRole("ROLE_" + UserRole.USER);
      userDTO.setEnabled(true);
      userDTO.setLoginType(1);
      userDTO.setEmail(oAuth2User.getEmail());
      userDTO.setAvatar(oAuth2User.getAvatar());

      result = userService.addUser(userDTO);
      oAuth2User.setId(Long.valueOf(result.getData().toString()));
      // Store oauth2 token.
      OAuth2AuthenticationToken oAuth2AuthenticationToken =
          new OAuth2AuthenticationToken(
              oAuth2User, oAuth2User.getAuthorities(), github.getRegistrationId());
      securityContext.setAuthentication(oAuth2AuthenticationToken);
      sessionRegistry.registerNewSession(
          request.getSession().getId(), oAuth2AuthenticationToken.getPrincipal());
    } else {
      // Store oauth2 token.
      OAuth2AuthenticationToken oAuth2AuthenticationToken =
          new OAuth2AuthenticationToken(
              oAuth2User, oAuth2User.getAuthorities(), github.getRegistrationId());
      securityContext.setAuthentication(oAuth2AuthenticationToken);
      sessionRegistry.registerNewSession(
          request.getSession().getId(), oAuth2AuthenticationToken.getPrincipal());
      Long currentLoginUserId = PMSUserDetailsUtil.getCurrentLoginUserId();

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
      result = Result.ok(currentLoginUserId);
    }
    return result;
  }

  /**
   * Login to playlist master by Google.
   *
   * @param code Authorization code.
   * @param request Http servlet request.
   * @return Result whose data is user's id in pms.
   */
  @Override
  public Result loginByGoogle(String code, HttpServletRequest request) {
    //    System.out.println("Authorization code: " + code);
    ClientRegistration google = clientRegistrationRepository.findByRegistrationId("google");
    String access_token_url = google.getProviderDetails().getTokenUri();
    access_token_url += "?client_id=" + google.getClientId();
    access_token_url += "&client_secret=" + google.getClientSecret();
    access_token_url += "&grant_type=authorization_code";
    access_token_url += "&code=" + code;
    access_token_url += "&redirect_uri=http://playlistmaster.com:8080/login/oauth2/google";
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
      UserDTO userDTO = new UserDTO();
      userDTO.setName(oAuth2User.getName());
      userDTO.setRole("ROLE_" + UserRole.USER);
      userDTO.setEnabled(true);
      userDTO.setLoginType(2);
      userDTO.setEmail(oAuth2User.getEmail());
      userDTO.setAvatar(oAuth2User.getAvatar());
      result = userService.addUser(userDTO);
      oAuth2User.setId(Long.valueOf(result.getData().toString()));
      // Store oauth2 token.
      OAuth2AuthenticationToken oAuth2AuthenticationToken =
          new OAuth2AuthenticationToken(
              oAuth2User, oAuth2User.getAuthorities(), google.getRegistrationId());
      securityContext.setAuthentication(oAuth2AuthenticationToken);
      sessionRegistry.registerNewSession(
          request.getSession().getId(), oAuth2AuthenticationToken.getPrincipal());
    } else {
      // Store oauth2 token.
      OAuth2AuthenticationToken oAuth2AuthenticationToken =
          new OAuth2AuthenticationToken(
              oAuth2User, oAuth2User.getAuthorities(), google.getRegistrationId());
      securityContext.setAuthentication(oAuth2AuthenticationToken);
      sessionRegistry.registerNewSession(
          request.getSession().getId(), oAuth2AuthenticationToken.getPrincipal());
      Long currentLoginUserId = PMSUserDetailsUtil.getCurrentLoginUserId();

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
      result = Result.ok(currentLoginUserId);
    }
    return result;
  }

  /**
   * Register playlist master account using email and password.
   *
   * @param registerFormDTO Register form dto.
   * @return Registered user's id in pms if success.
   */
  @Override
  public Result register(RegisterFormDTO registerFormDTO)
      throws MessagingException, UnsupportedEncodingException {
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
   * Bind email, send verifying code to user's email, need login first.
   *
   * @param email Email to bind.
   * @return Common result.
   */
  @Override
  public Result bindEmail(String email) throws MessagingException, UnsupportedEncodingException {
    Long currentUserId = PMSUserDetailsUtil.getCurrentLoginUserId();

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

    redisTemplate.delete(tokenKey);

    return userService.updatePassword(currentUserId, newPass);
  }

  /**
   * Verify token for binding user's email, need to log in first.
   *
   * @param bindEmailDTO DTO for bind email.
   * @return Common result.
   */
  @Override
  public Result verifyBindEmailToken(BindEmailDTO bindEmailDTO) {
    String email = bindEmailDTO.getEmail();
    String token = bindEmailDTO.getToken();

    Long currentUserId = PMSUserDetailsUtil.getCurrentLoginUserId();

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
