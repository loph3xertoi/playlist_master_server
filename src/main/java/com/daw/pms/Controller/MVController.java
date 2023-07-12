package com.daw.pms.Controller;

import com.daw.pms.DTO.Result;
import com.daw.pms.Service.PMS.MVService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
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
   */
  @GetMapping("/mv/{vid}")
  public Result getDetailMV(
      @PathVariable String vid, @RequestParam(value = "platform") Integer platform) {
    return Result.ok(mvService.getDetailMV(vid, platform));
  }

  /**
   * Get all MVs links.
   *
   * @param vids The vid of the mv(s), multi vid separated by comma.
   * @param platform The platform id.
   * @return A map which key is the vid and value is a list of urls of this mv.
   */
  @GetMapping("/mvlink/{vids}")
  public Result getMVsLink(
      @PathVariable(name = "vids") String vids, @RequestParam Integer platform) {
    return Result.ok(mvService.getMVsLink(vids, platform));
  }

  /**
   * Get all related videos according to the song with {@code songId}.
   *
   * @param songId The song id.
   * @param platform The platform id.
   * @return All the related video about the song with {@code songId}.
   */
  @GetMapping("/relatedmv/{songId}")
  public Result getRelatedVideos(
      @PathVariable(name = "songId") Integer songId, @RequestParam Integer platform) {
    return Result.ok(mvService.getRelatedVideos(songId, platform));
  }
}
