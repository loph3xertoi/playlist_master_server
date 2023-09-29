package com.daw.pms.Controller;

import com.daw.pms.DTO.Result;
import com.daw.pms.Utils.PmsUserDetailsUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
  private final PmsUserDetailsUtil pmsUserDetailsUtil;

  /**
   * Constructor for TestController.
   *
   * @param sessionRegistry a {@link SessionRegistry} object.
   * @param pmsUserDetailsUtil a {@link PmsUserDetailsUtil} object.
   */
  public TestController(SessionRegistry sessionRegistry, PmsUserDetailsUtil pmsUserDetailsUtil) {
    this.sessionRegistry = sessionRegistry;
    this.pmsUserDetailsUtil = pmsUserDetailsUtil;
  }

  /**
   * Test endpoint.
   *
   * @return Current user's authentication.
   */
  @Operation(summary = "Test endpoint.")
  @ApiResponse(description = "Current user's authentication.")
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
  @Operation(summary = "Get logged users.")
  @ApiResponse(description = "Logged users.")
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
  @Operation(summary = "Get cookies of current user.")
  @ApiResponse(description = "Cookies of current user.")
  @GetMapping("/cookies")
  public Cookie[] getCookies(@Parameter(description = "Http request.") HttpServletRequest request) {
    return request.getCookies();
  }

  /**
   * Check if the user is logged in.
   *
   * @return Common result if user has logged, 401 otherwise.
   */
  @Operation(summary = "Check if the user is logged in.")
  @ApiResponse(description = "Common result if user has logged, 401 otherwise.")
  @GetMapping("/check")
  public Result checkLoginState() {
    return Result.ok();
  }
}
