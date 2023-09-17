package com.daw.pms.Controller;

import com.daw.pms.DTO.*;
import com.daw.pms.Service.PMS.LoginService;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling user authentication and authorization.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/8/23
 */
@RestController
@Validated
public class LoginController {
  private final LoginService loginService;

  public LoginController(LoginService loginService) {
    this.loginService = loginService;
  }

  /**
   * PMS user login endpoint.
   *
   * @param loginFormDTO Login form dto.
   * @param request Http servlet request.
   * @return Result whose data is user's id in pms.
   */
  @PostMapping("/login")
  public Result login(@Valid @RequestBody LoginFormDTO loginFormDTO, HttpServletRequest request) {
    return loginService.login(loginFormDTO, request);
  }

  /**
   * Login by GitHub.
   *
   * @param code Authorization code.
   * @param request Http servlet request.
   * @return Result whose data is user's id in pms.
   */
  @GetMapping("/login/oauth2/github")
  public Result loginByGitHub(@RequestParam String code, HttpServletRequest request) {
    return loginService.loginByGitHub(code, request);
  }

  /**
   * Login by Google.
   *
   * @param code Authorization code.
   * @param request Http servlet request.
   * @return Result whose data is user's id in pms.
   */
  @GetMapping("/login/oauth2/google")
  public Result loginByGoogle(@RequestParam String code, HttpServletRequest request) {
    Map<String, String[]> parameterMap = request.getParameterMap();
    return loginService.loginByGoogle(code, request);
    //    HttpTransportFactory httpTransportFactory =
    //        new HttpTransportFactory() {
    //          @Override
    //          public HttpTransport create() {
    //            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1",
    // 7890));
    //            return new NetHttpTransport.Builder().setProxy(proxy).build();
    //          }
    //        };
    //    HttpTransport httpTransport = httpTransportFactory.create();
    //    GsonFactory gsonFactory = new GsonFactory();
    //
    //    GenericUrl genericUrl = new GenericUrl("https://oauth2.googleapis.com/token");
    //    //    AuthorizationRequest authRequest = new AuthorizationRequest.Builder(
    //    //      serviceConfiguration,
    //    //      provider.getClientId(),
    //    //      ResponseTypeValues.CODE,
    //    //      provider.getRedirectUri()
    //    //    )
    //    //      .setScope(provider.getScope())
    //    //      .setCodeVerifier(null)
    //    //      .build();
    //    AuthorizationCodeTokenRequest authorizationCodeTokenRequest =
    //        new AuthorizationCodeTokenRequest(httpTransport, gsonFactory, genericUrl, code);
    //    authorizationCodeTokenRequest.setGrantType("authorization_code");
    //    authorizationCodeTokenRequest.setScopes(Collections.singleton("email"));
    //    authorizationCodeTokenRequest.set("code_verifier", null);
    //    authorizationCodeTokenRequest.setRedirectUri(
    //        "http://playlistmaster.com:8080/login/oauth2/google");
    //    ClientParametersAuthentication authentication =
    //        new ClientParametersAuthentication(
    //            "1043116433744-fu89rnc4g7tti8rek1k2d4gmutqmokt1.apps.googleusercontent.com",
    //            "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");
    //    authorizationCodeTokenRequest.setClientAuthentication(authentication);
    //    try {
    //      TokenResponse tokenResponse = authorizationCodeTokenRequest.execute();
    //      System.out.println(tokenResponse);
    //    } catch (IOException e) {
    //      throw new RuntimeException(e);
    //    }
    //    return null;
  }

  /**
   * Register account in pms.
   *
   * @param registerFormDTO Register form data.
   * @return Registered user's id in pms if success.
   */
  @PostMapping("/register")
  public Result register(@Valid @RequestBody RegisterFormDTO registerFormDTO)
      throws MessagingException, UnsupportedEncodingException {
    return loginService.register(registerFormDTO);
  }

  /**
   * Page for logout successfully.
   *
   * @param request Http servlet request.
   * @return Common result.
   */
  @GetMapping("/logout/success")
  public Result logout(HttpServletRequest request) {
    return loginService.logout(request);
  }

  /**
   * Forget user's password, send verifying code to user's email, need login first.
   *
   * @return Common result.
   */
  @GetMapping("/forgot/password")
  public Result forgotPassword() throws MessagingException, UnsupportedEncodingException {
    return loginService.forgotPassword();
  }

  /**
   * Bind email, send verifying code to user's email, need login first.
   *
   * @return Common result.
   */
  @GetMapping("/bind/email")
  public Result bindEmail(
      @Valid
          @RequestParam
          @NotBlank(message = "No blank email.")
          @Email(message = "Email format error.")
          String email)
      throws MessagingException, UnsupportedEncodingException {
    return loginService.bindEmail(email);
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
  @GetMapping("/sendcode")
  public Result sendVerifyTokenWithoutLogin(
      @Valid
          @RequestParam
          @NotBlank(message = "No blank email.")
          @Email(message = "Email format error.")
          String email,
      @RequestParam Integer type)
      throws MessagingException, UnsupportedEncodingException {
    return loginService.sendVerifyToken(email, type);
  }

  /**
   * Verify token for resetting user's password, need to log in first.
   *
   * @param resetPassDTO DTO for resetting password.
   * @return Common result.
   */
  @PostMapping("/verify/resetPassword")
  public Result verifyResetPassToken(@Valid @RequestBody ResetPassDTO resetPassDTO) {
    return loginService.verifyResetPassToken(resetPassDTO);
  }

  /**
   * Verify token for binding user's email, need to log in first.
   *
   * @param bindEmailDTO DTO for bind email.
   * @return Common result.
   */
  @PostMapping("/verify/bindEmail")
  public Result verifyBingEmailToken(@Valid @RequestBody BindEmailDTO bindEmailDTO) {
    return loginService.verifyBindEmailToken(bindEmailDTO);
  }

  /**
   * Verify token for resetting user's password, no need to log in.
   *
   * @param resetPassNologinDTO DTO for resetting password.
   * @return Common result.
   */
  @PostMapping("/verify/nologin/resetPassword")
  public Result verifyResetPassTokenWithoutLogin(
      @Valid @RequestBody ResetPassNologinDTO resetPassNologinDTO) {
    return loginService.verifyResetPassTokenWithoutLogin(resetPassNologinDTO);
  }

  /**
   * Verify token for sign up new account, no need to log in.
   *
   * @param signUpNologinDTO DTO for sign up.
   * @return Common result.
   */
  @PostMapping("/verify/nologin/signUp")
  public Result verifySignUpTokenWithoutLogin(
      @Valid @RequestBody SignUpNologinDTO signUpNologinDTO) {
    return loginService.verifySignUpTokenWithoutLogin(signUpNologinDTO);
  }
}
