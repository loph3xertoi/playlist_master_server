package com.daw.pms.Controller;

import com.daw.pms.DTO.*;
import com.daw.pms.Service.PMS.LoginService;
import java.io.UnsupportedEncodingException;
import javax.mail.MessagingException;
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
   * @return Result whose data is user's id in pms.
   */
  @PostMapping("/login")
  public Result login(@Valid @RequestBody LoginFormDTO loginFormDTO) {
    return loginService.login(loginFormDTO);
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
   * @return Common result.
   */
  @GetMapping("/logout/success")
  public Result logout() {
    return Result.ok();
  }

  /**
   * Forget user's password, send verifying code to user's email, need login first.
   *
   * @return Common result.
   */
  @GetMapping("/forgot")
  public Result forgotPassword() throws MessagingException, UnsupportedEncodingException {
    return loginService.forgotPassword();
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
  @PostMapping("/verify")
  public Result verifyResetPassToken(@Valid @RequestBody ResetPassDTO resetPassDTO) {
    return loginService.verifyResetPassToken(resetPassDTO);
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
