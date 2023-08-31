package com.daw.pms.Controller;

import com.daw.pms.DTO.Result;
import com.daw.pms.Service.PMS.UserService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
   * @param id Your user id in pms.
   * @param platform Which platform the user belongs to. 0 represents pms, 1 represents qq music, 2
   *     represents netease cloud music, 3 represents bilibili.
   * @return User information for specific platform.
   * @apiNote GET /user/{@code id}?platform={@code platform}
   */
  @GetMapping("/user/{id}")
  public Result getUser(@PathVariable Long id, @RequestParam int platform) {
    try {
      return Result.ok(userService.getUserInfo(id, platform));
    } catch (Exception e) {
      return Result.fail(e.getMessage() + "\n#0\t" + e.getStackTrace()[0].toString());
    }
  }
}
