package com.daw.pms.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handle error endpoint.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/18/23
 */
@RestController
public class ErrorController {
  /**
   * error.
   *
   * @return a {@link java.lang.String} object.
   */
  @GetMapping("/error")
  public String error() {
    return "Got error";
  }
}
