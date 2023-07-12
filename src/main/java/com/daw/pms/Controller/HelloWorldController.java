package com.daw.pms.Controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {
  @GetMapping("hello")
  public Map<String, Object> helloWorld() {
    Map<String, Object> map = new HashMap<>();
    map.put("The date right now", new Date());
    return map;
  }
}
