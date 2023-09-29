package com.daw.pms.Controller;

import com.daw.pms.Utils.HttpTools;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.Optional;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handle cors for request to bilibili.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 8/20/23
 */
@RestController
@RequestMapping("/cors")
public class CorsController {
  private final HttpTools httpTools;

  /**
   * Get bilibili splash screen image, modify cors header.
   *
   * @return The splash screen image.
   */
  @Operation(summary = "Get bilibili splash screen image, modify cors header.")
  @ApiResponse(description = "The splash screen image.")
  @GetMapping("/bili/splash")
  public String getSplashScreen() {
    String url =
        "https://app.bilibili.com/x/v2/splash/brand/list?appkey=1d8b6e7d45233436&ts=0&sign=78a89e153cd6231a4a4d55013aa063ce";
    return httpTools.requestGetAPIByFinalUrl(url, new HttpHeaders(), Optional.empty());
  }

  /**
   * Constructor for CorsController.
   *
   * @param httpTools a {@link com.daw.pms.Utils.HttpTools} object.
   */
  public CorsController(HttpTools httpTools) {
    this.httpTools = httpTools;
  }

  /**
   * Get search suggestions in bilibili.
   *
   * @param keyword The keyword to search.
   * @return The search suggestions.
   */
  @Operation(summary = "Get search suggestions in bilibili.")
  @ApiResponse(description = "The search suggestions.")
  @GetMapping("/bili/suggestions/{keyword}")
  public String getSearchSuggestions(
      @Parameter(description = "The keyword to search.") @PathVariable String keyword) {
    String url =
        "https://s.search.bilibili.com/main/suggest?term=" + keyword + "&main_ver=v1&highlight=";
    return httpTools.requestGetAPIByFinalUrl(url, new HttpHeaders(), Optional.empty());
  }

  //  @GetMapping(value = "/image", produces = MediaType.IMAGE_PNG_VALUE)
  //  public byte[] getImage(@RequestParam String imageUrl) {
  //    ResponseEntity<byte[]> image =
  //        httpTools.requestGetImageByFinalUrl(imageUrl, Optional.ofNullable(biliCookie));
  //    return image.getBody();
  //  }
}
