package com.daw.pms.Controller;

import com.daw.pms.DTO.Result;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
public class HelloWorldController {
  @GetMapping("/hello")
  public Map<String, Object> helloWorld() {
    Map<String, Object> map = new HashMap<>();
    map.put("The date right now", new Date());
    return map;
  }

  @PostMapping("/myendpoint")
  public Result myEndpoint(
      @RequestBody Map<String, Object> library, @RequestParam Integer platform) {
    System.out.println(library);
    return null;
  }
}
