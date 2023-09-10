package com.daw.pms.Controller;

import com.daw.pms.DTO.Result;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
public class HelloWorldController {
  @GetMapping("/hello")
  public Map<String, Object> helloWorld() {
    Map<String, Object> map = new HashMap<>();
    map.put("time", new Date());
    SecurityContext securityContext = SecurityContextHolder.getContext();
    Authentication authentication = securityContext.getAuthentication();
    map.put("authentication", authentication);
    return map;
  }

  @PostMapping("/myendpoint")
  public Result myEndpoint(@RequestBody Map<String, Object> library, @RequestParam int platform) {
    System.out.println(library);
    return null;
  }
}
