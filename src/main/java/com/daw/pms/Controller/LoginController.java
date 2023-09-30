package com.daw.pms.Controller;

import com.daw.pms.DTO.*;
import com.daw.pms.Service.PMS.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

  /**
   * Constructor for LoginController.
   *
   * @param loginService a {@link com.daw.pms.Service.PMS.LoginService} object.
   */
  public LoginController(LoginService loginService) {
    this.loginService = loginService;
  }

  /**
   * PMS user login endpoint.
   *
   * @param loginFormDTO Login form dto.
   * @param request Http servlet request.
   * @return Result whose data is LoginResult.
   */
  @Operation(summary = "PMS user login endpoint.")
  @ApiResponse(description = "Result whose data is LoginResult.")
  @PostMapping("/login")
  public Result login(
      @Parameter(description = "Login form dto.") @Valid @RequestBody LoginFormDTO loginFormDTO,
      @Parameter(description = "Http servlet request.") HttpServletRequest request) {
    return loginService.login(loginFormDTO, request);
  }

  /**
   * Login by GitHub.
   *
   * @param code Authorization code.
   * @param request Http servlet request.
   * @return Result whose data is LoginResult.
   */
  @Operation(summary = "Login by GitHub.")
  @ApiResponse(description = "Result whose data is LoginResult.")
  @GetMapping("/login/oauth2/github")
  public Result loginByGitHub(
      @Parameter(description = "Authorization code.") @RequestParam String code,
      @Parameter(description = "Http servlet request.") HttpServletRequest request) {
    return loginService.loginByGitHub(code, request);
  }

  /**
   * Login by Google.
   *
   * @param code Authorization code.
   * @param request Http servlet request.
   * @return Result whose data is LoginResult.
   */
  @Operation(summary = "Login by Google.")
  @ApiResponse(description = "Result whose data is LoginResult.")
  @GetMapping("/login/oauth2/google")
  public Result loginByGoogle(
      @Parameter(description = "Authorization code.") @RequestParam String code,
      @Parameter(description = "Http servlet request.") HttpServletRequest request) {
    Map<String, String[]> parameterMap = request.getParameterMap();
    return loginService.loginByGoogle(code, request);
  }

  /**
   * Register account in pms.
   *
   * @param registerFormDTO Register form data.
   * @return Registered user's id in pms if success.
   * @throws javax.mail.MessagingException if any.
   * @throws java.io.UnsupportedEncodingException if any.
   */
  @Operation(summary = "Register account in pms.")
  @ApiResponse(description = "Registered user's id in pms if success.")
  @PostMapping("/register")
  public Result register(
      @Parameter(description = "Register form data.") @Valid @RequestBody
          RegisterFormDTO registerFormDTO)
      throws MessagingException, UnsupportedEncodingException {
    return loginService.register(registerFormDTO);
  }

  /**
   * Page for logout successfully.
   *
   * @param request Http servlet request.
   * @return Common result.
   */
  @Operation(summary = "Page for logout successfully.")
  @ApiResponse(description = "Common result.")
  @GetMapping("/logout/success")
  public Result logout(
      @Parameter(description = "Http servlet request.") HttpServletRequest request) {
    return loginService.logout(request);
  }

  /**
   * Forget user's password, send verifying code to user's email, need login first.
   *
   * @return Common result.
   * @throws javax.mail.MessagingException if any.
   * @throws java.io.UnsupportedEncodingException if any.
   */
  @Operation(
      summary = "Forget user's password, send verifying code to user's email, need login first.")
  @ApiResponse(description = "Common result.")
  @GetMapping("/forgot/password")
  public Result forgotPassword() throws MessagingException, UnsupportedEncodingException {
    return loginService.forgotPassword();
  }

  /**
   * Bind email, send verifying code to user's email, need login first.
   *
   * @param email Email you want to bind.
   * @return Common result.
   * @throws javax.mail.MessagingException if any.
   * @throws java.io.UnsupportedEncodingException if any.
   */
  @Operation(summary = "Bind email, send verifying code to user's email, need login first.")
  @ApiResponse(description = "Common result.")
  @GetMapping("/bind/email")
  public Result bindEmail(
      @Parameter(description = "Email you want to bind.")
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
   * @throws javax.mail.MessagingException javax.mail.MessagingException.
   * @throws java.io.UnsupportedEncodingException java.io.UnsupportedEncodingException.
   */
  @Operation(summary = "Send token to yur email for verifying, no need to login first.")
  @ApiResponse(description = "Common result.")
  @GetMapping("/sendcode")
  public Result sendVerifyTokenWithoutLogin(
      @Parameter(description = "Email to receive token.")
          @Valid
          @RequestParam
          @NotBlank(message = "No blank email.")
          @Email(message = "Email format error.")
          String email,
      @Parameter(description = "Token type, 1 for sign up, 2 for reset password.") @RequestParam
          Integer type)
      throws MessagingException, UnsupportedEncodingException {
    return loginService.sendVerifyToken(email, type);
  }

  /**
   * Verify token for resetting user's password, need to log in first.
   *
   * @param resetPassDTO DTO for resetting password.
   * @return Common result.
   */
  @Operation(summary = "Verify token for resetting user's password, need to log in first.")
  @ApiResponse(description = "Common result.")
  @PostMapping("/verify/resetPassword")
  public Result verifyResetPassToken(
      @Parameter(description = "DTO for resetting password.") @Valid @RequestBody
          ResetPassDTO resetPassDTO) {
    return loginService.verifyResetPassToken(resetPassDTO);
  }

  /**
   * Verify token for binding user's email, need to log in first.
   *
   * @param bindEmailDTO DTO for bind email.
   * @return Common result.
   */
  @Operation(summary = "Verify token for binding user's email, need to log in first.")
  @ApiResponse(description = "Common result.")
  @PostMapping("/verify/bindEmail")
  public Result verifyBingEmailToken(
      @Parameter(description = "DTO for bind email.") @Valid @RequestBody
          BindEmailDTO bindEmailDTO) {
    return loginService.verifyBindEmailToken(bindEmailDTO);
  }

  /**
   * Verify token for resetting user's password, no need to log in.
   *
   * @param resetPassNologinDTO DTO for resetting password.
   * @return Common result.
   */
  @Operation(summary = "Verify token for resetting user's password, no need to log in.")
  @ApiResponse(description = "Common result.")
  @PostMapping("/verify/nologin/resetPassword")
  public Result verifyResetPassTokenWithoutLogin(
      @Parameter(description = "DTO for resetting password.") @Valid @RequestBody
          ResetPassNologinDTO resetPassNologinDTO) {
    return loginService.verifyResetPassTokenWithoutLogin(resetPassNologinDTO);
  }

  /**
   * Verify token for sign up new account, no need to log in.
   *
   * @param signUpNologinDTO DTO for sign up.
   * @return Common result.
   */
  @Operation(summary = "Verify token for sign up new account, no need to log in.")
  @ApiResponse(description = "Common result.")
  @PostMapping("/verify/nologin/signUp")
  public Result verifySignUpTokenWithoutLogin(
      @Parameter(description = "DTO for sign up.") @Valid @RequestBody
          SignUpNologinDTO signUpNologinDTO) {
    return loginService.verifySignUpTokenWithoutLogin(signUpNologinDTO);
  }
}
