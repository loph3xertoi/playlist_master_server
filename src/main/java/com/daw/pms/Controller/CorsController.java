package com.daw.pms.Controller;

import com.daw.pms.Utils.HttpTools;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
  @Value("${bilibili.cookie}")
  private String biliCookie;

  private final HttpTools httpTools;

  public CorsController(HttpTools httpTools) {
    this.httpTools = httpTools;
  }

  @GetMapping("/bili/splash")
  public String getSplashScreen() {
    String url =
        "https://app.bilibili.com/x/v2/splash/brand/list?appkey=1d8b6e7d45233436&ts=0&sign=78a89e153cd6231a4a4d55013aa063ce";
    return httpTools.requestGetAPIByFinalUrl(
        url, new HttpHeaders(), Optional.ofNullable(biliCookie));
  }

  @GetMapping("/bili/suggestions/{keyword}")
  public String getSearchSuggestions(@PathVariable String keyword) {
    String url =
        "https://s.search.bilibili.com/main/suggest?term=" + keyword + "&main_ver=v1&highlight=";
    return httpTools.requestGetAPIByFinalUrl(
        url, new HttpHeaders(), Optional.ofNullable(biliCookie));
  }

  @GetMapping(value = "/image", produces = MediaType.IMAGE_PNG_VALUE)
  public byte[] getImage(@RequestParam String imageUrl) {
    ResponseEntity<byte[]> image =
        httpTools.requestGetImageByFinalUrl(imageUrl, Optional.ofNullable(biliCookie));
    return image.getBody();
  }
}
