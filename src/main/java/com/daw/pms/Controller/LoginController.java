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
  public Result register(@Valid @RequestBody RegisterFormDTO registerFormDTO) {
    return loginService.register(registerFormDTO);
  }

  /**
   * Logout current account.
   *
   * @return Return success.
   */
  @GetMapping("/logout")
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
   * Forget user's password, send verifying code to user's email, without needing login first.
   *
   * @return Common result.
   */
  @GetMapping("/forgot/nologin")
  public Result forgotPasswordWithoutLogin(
      @Valid
          @RequestParam
          @NotBlank(message = "No blank email.")
          @Email(message = "Email format error.")
          String email)
      throws MessagingException, UnsupportedEncodingException {
    return loginService.forgotPasswordByEmail(email);
  }

  /**
   * Verify token for resetting user's password.
   *
   * @param resetPassDTO DTO for resetting password.
   * @return Common result.
   */
  @PostMapping("/verify")
  public Result verifyResetPassToken(@Valid @RequestBody ResetPassDTO resetPassDTO) {
    return loginService.verifyResetPassToken(
        resetPassDTO.getPassword(), resetPassDTO.getRepeatedPassword(), resetPassDTO.getToken());
  }

  /**
   * Verify token for resetting user's password.
   *
   * @param resetPassNologinDTO DTO for resetting password.
   * @return Common result.
   */
  @PostMapping("/verify/nologin")
  public Result verifyResetPassTokenWithoutLogin(
      @Valid @RequestBody ResetPassNologinDTO resetPassNologinDTO) {
    return loginService.verifyResetPassTokenWithoutLogin(
        resetPassNologinDTO.getPassword(),
        resetPassNologinDTO.getRepeatedPassword(),
        resetPassNologinDTO.getToken(),
        resetPassNologinDTO.getEmail());
  }
}
