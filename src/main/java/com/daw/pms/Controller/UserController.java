package com.daw.pms.Controller;

import com.daw.pms.DTO.Result;
import org.springframework.web.bind.annotation.GetMapping;
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
  @GetMapping("/user")
  public Result getUser() {
    return Result.fail("To be implemented.");
  }
}
