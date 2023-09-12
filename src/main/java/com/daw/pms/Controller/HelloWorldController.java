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

@RestController
public class HelloWorldController {
  private final SessionRegistry sessionRegistry;

  public HelloWorldController(SessionRegistry sessionRegistry) {
    this.sessionRegistry = sessionRegistry;
  }

  @GetMapping("/hello")
  public Map<String, Object> helloWorld() {
    Map<String, Object> map = new HashMap<>();
    map.put("time", new Date());
    SecurityContext securityContext = SecurityContextHolder.getContext();
    Authentication authentication = securityContext.getAuthentication();
    map.put("authentication", authentication);
    return map;
  }

  @GetMapping("/users")
  public Object getLoggedUsers() {
    List<Object> users = new ArrayList<>();
    for (Object principal : sessionRegistry.getAllPrincipals()) {
      users.add(sessionRegistry.getAllSessions(principal, false));
    }
    return users;
  }

  @GetMapping("/cookies")
  public Cookie[] getCookies(HttpServletRequest request) {
    return request.getCookies();
  }

  @PostMapping("/myendpoint")
  public Result myEndpoint(@RequestBody Map<String, Object> library, @RequestParam int platform) {
    System.out.println(library);
    return null;
  }

  @GetMapping("/check")
  public Result checkLoginState() {
    return Result.ok();
  }
}
