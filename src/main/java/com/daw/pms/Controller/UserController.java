package com.daw.pms.Controller;

import com.daw.pms.DTO.Result;
import com.daw.pms.Service.PMS.UserService;
import com.daw.pms.Utils.PMSUserDetailsUtil;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

/**
 * User controller.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 6/27/23
 */
@RestController
@CacheConfig(cacheNames = "user-cache")
@Cacheable(key = "#root.methodName + '(' + #root.args + ')'", unless = "!#result.success")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
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
    Long pmsUserId = PMSUserDetailsUtil.getCurrentLoginUserId();
    return Result.ok(userService.getUserInfo(pmsUserId, platform));
  }

  /**
   * Update the credential of third app.
   *
   * @param thirdId Third app's id of user.
   * @param thirdCookie Third app's cookie of user.
   * @param platform 1 for qqmusic, 2 for ncm, 3 for bilibili.
   * @return The result for updating credential.
   */
  @PutMapping("/credential")
  public Result updateThirdAppCredential(
      @RequestParam String thirdId, @RequestParam String thirdCookie, @RequestParam int platform) {
    return userService.updateThirdAppCredential(thirdId, thirdCookie, platform);
  }
}
