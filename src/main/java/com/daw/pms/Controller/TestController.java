package com.daw.pms.Controller;

import com.daw.pms.DTO.Result;
import com.daw.pms.Utils.PmsUserDetailsUtil;
import com.daw.pms.Utils.RegistrationCodeUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
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
  @Value("${pms.registration-code:PMSDAW}")
  private String rootRegistrationCode;
  private final SessionRegistry sessionRegistry;
  private final PmsUserDetailsUtil pmsUserDetailsUtil;
  private final RegistrationCodeUtil registrationCodeUtil;

  /**
   * Constructor for TestController.
   *
   * @param sessionRegistry SessionRegistry.
   * @param pmsUserDetailsUtil PmsUserDetailsUtil.
   * @param registrationCodeUtil RegistrationCodeUtil.
   */
  public TestController(
      SessionRegistry sessionRegistry,
      PmsUserDetailsUtil pmsUserDetailsUtil,
      RegistrationCodeUtil registrationCodeUtil) {
    this.sessionRegistry = sessionRegistry;
    this.pmsUserDetailsUtil = pmsUserDetailsUtil;
    this.registrationCodeUtil = registrationCodeUtil;
  }

  /**
   * Test endpoint.
   *
   * @return Current user's authentication.
   */
  @Operation(summary = "Test endpoint.")
  @ApiResponse(description = "Current user's authentication.")
  @GetMapping("/hello")
  public Map<String, Object> test(HttpServletRequest request) {
    Map<String, Object> map = new HashMap<>();
    map.put("time", new Date());
    SecurityContext securityContext = SecurityContextHolder.getContext();
    Authentication authentication = securityContext.getAuthentication();
    map.put("authentication", authentication);
    for (int i = 0; i < 2; i++) {
      String registrationCode = registrationCodeUtil.getRegistrationCodeByUserId(0);
      System.out.println("registrationCode: " + registrationCode);
      long invitorId = registrationCodeUtil.getUserIdByRegistrationCode(registrationCode);
      System.out.println("invitorId: " + invitorId);
    }
    System.out.println(rootRegistrationCode);
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
