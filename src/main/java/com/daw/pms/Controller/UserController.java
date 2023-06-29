package com.daw.pms.Controller;

import com.daw.pms.DTO.Result;
import com.daw.pms.Service.UserService;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handle user's information.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 6/27/23
 */
@RestController
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/user/{uid}")
  public Result getUser(@PathVariable(name = "uid") String uid) {
    Map<String, Object> userInfo = userService.getUserInfo(uid);
    return Result.ok(userInfo);
  }
}
