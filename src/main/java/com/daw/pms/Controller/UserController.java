package com.daw.pms.Controller;

import com.daw.pms.DTO.Result;
import com.daw.pms.DTO.ThirdAppCredentialDTO;
import com.daw.pms.Service.PMS.UserService;
import com.daw.pms.Utils.PmsUserDetailsUtil;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * User controller.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 6/27/23
 */
@RestController
// @CacheConfig(cacheNames = "user-cache")
// @Cacheable(key = "#root.methodName + '(' + #root.args + ')'", unless = "!#result.success")
public class UserController {
  private final UserService userService;
  private final PmsUserDetailsUtil pmsUserDetailsUtil;

  /**
   * Constructor for UserController.
   *
   * @param userService a {@link com.daw.pms.Service.PMS.UserService} object.
   * @param pmsUserDetailsUtil a {@link com.daw.pms.Utils.PmsUserDetailsUtil} object.
   */
  public UserController(UserService userService, PmsUserDetailsUtil pmsUserDetailsUtil) {
    this.userService = userService;
    this.pmsUserDetailsUtil = pmsUserDetailsUtil;
  }

  /**
   * Get user information for specific platform.
   *
   * @param id Your pms user id, Used for cache evicting.
   * @param platform Which platform the user belongs to. 0 represents pms, 1 represents qq music, 2
   *     represents netease cloud music, 3 represents bilibili.
   * @return User information for specific platform.
   * @apiNote GET /user/{@code id}?platform={@code platform}
   */
  @GetMapping("/user/{id}")
  public Result getUser(@PathVariable String id, @RequestParam int platform) {
    Long pmsUserId = pmsUserDetailsUtil.getCurrentLoginUserId();
    return userService.getUserInfo(pmsUserId, platform);
  }

  /**
   * Update the credential of third app.
   *
   * @param credentialDTO Credential dto.
   * @param platform 1 for qqmusic, 2 for ncm, 3 for bilibili.
   * @return The result for updating credential.
   */
  @PutMapping("/credential")
  public Result updateThirdAppCredential(
      @Valid @RequestBody ThirdAppCredentialDTO credentialDTO, @RequestParam int platform) {
    return userService.updateThirdAppCredential(credentialDTO, platform);
  }

  /**
   * Get basic pms user info of the current user login.
   *
   * @return Basic pms user info.
   */
  @GetMapping("/user/basic")
  public Result getBasicPMSUserInfo() {
    Long pmsUserId = pmsUserDetailsUtil.getCurrentLoginUserId();
    return userService.getBasicPMSUserInfo(pmsUserId);
  }
}
