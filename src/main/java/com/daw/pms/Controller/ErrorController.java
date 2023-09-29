package com.daw.pms.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
   * Error endpoint.
   *
   * @return Error message.
   */
  @Operation(summary = "Error endpoint")
  @ApiResponse(description = "Error message.")
  @GetMapping("/error")
  public String error() {
    return "Got error";
  }
}
