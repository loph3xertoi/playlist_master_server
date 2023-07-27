package com.daw.pms.Controller;

import com.daw.pms.DTO.Result;
import com.daw.pms.Entity.Basic.BasicVideo;
import com.daw.pms.Service.PMS.MVService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * MV controller.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 7/23/23
 */
@RestController
@CacheConfig(cacheNames = "mv-cache")
@Cacheable(key = "#root.methodName + '(' + #root.args + ')'", unless = "!#result.success")
public class MVController {
  private final MVService mvService;

  public MVController(MVService mvService) {
    this.mvService = mvService;
  }

  /**
   * Get detail video information according to its {@code vid}.
   *
   * @param vid The vid of the mv.
   * @param platform The platform id.
   * @return The detail information of the mv {@code vid}.
   * @apiNote GET /mv/{@code vid}?platform={@code platform}
   */
  @GetMapping("/mv/{vid}")
  public Result getDetailMV(@PathVariable String vid, @RequestParam Integer platform) {
    BasicVideo video = mvService.getDetailMV(vid, platform);
    return Result.ok(video);
  }

  /**
   * Get all MVs links.
   *
   * @param vids The vid of the mv(s), multi vid separated by comma.
   * @param platform The platform id.
   * @return A map which key is the vid and value is a list of urls of this mv.
   * @apiNote GET /mvLink/{@code vids}?platform={@code platform}
   */
  @GetMapping("/mvLink/{vids}")
  public Result getMVsLink(@PathVariable String vids, @RequestParam Integer platform) {
    return Result.ok(mvService.getMVsLink(vids, platform));
  }

  /**
   * Get all related videos according to the song with {@code songId}.
   *
   * @param songId The song id.
   * @param platform The platform id.
   * @return All the related video about the song with {@code songId}.
   * @apiNote GET /relatedMV/{@code songId}?platform={@code platform}
   */
  @GetMapping("/relatedMV/{songId}")
  public Result getRelatedVideos(@PathVariable Integer songId, @RequestParam Integer platform) {
    return Result.ok(mvService.getRelatedVideos(songId, platform));
  }
}
