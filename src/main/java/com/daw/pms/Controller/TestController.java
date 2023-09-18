package com.daw.pms.Controller;

import com.daw.pms.DTO.Result;
import java.util.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for test purpose.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/18/23
 */
@RestController
public class TestController {
  private final SessionRegistry sessionRegistry;

  public TestController(SessionRegistry sessionRegistry) {
    this.sessionRegistry = sessionRegistry;
  }

  @GetMapping("/hello")
  public Map<String, Object> test() {
    Map<String, Object> map = new HashMap<>();
    map.put("time", new Date());
    SecurityContext securityContext = SecurityContextHolder.getContext();
    Authentication authentication = securityContext.getAuthentication();
    map.put("authentication", authentication);
    return map;
  }

  /**
   * Get logged users.
   *
   * @return Logged users.
   */
  @GetMapping("/users")
  public Object getLoggedUsers() {
    List<Object> users = new ArrayList<>();
    for (Object principal : sessionRegistry.getAllPrincipals()) {
      users.add(sessionRegistry.getAllSessions(principal, false));
    }
    return users;
  }

  /**
   * Get cookies of current user.
   *
   * @param request Http request.
   * @return Cookies of current user.
   */
  @GetMapping("/cookies")
  public Cookie[] getCookies(HttpServletRequest request) {
    return request.getCookies();
  }

  //  @PostMapping("/myendpoint")
  //  public Result myEndpoint(@RequestBody Map<String, Object> library, @RequestParam int platform)
  // {
  //    System.out.println(library);
  //    return null;
  //  }

  /**
   * Check if the user is logged in.
   *
   * @return Common result if user has logged, 401 otherwise.
   */
  @GetMapping("/check")
  public Result checkLoginState() {
    return Result.ok();
  }
}
