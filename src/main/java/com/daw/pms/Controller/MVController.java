package com.daw.pms.Controller;

import com.daw.pms.DTO.Result;
import com.daw.pms.Service.MVService;
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

  @GetMapping("/mv/{vid}/{platformId}")
  public Result getDetailMV(@PathVariable String vid, @PathVariable Integer platformId) {
    return Result.ok(mvService.getDetailMV(vid, platformId));
  }

  @GetMapping("/mvlink/{platformId}")
  public Result getMVsLink(
      @RequestParam(value = "vids") String vids, @PathVariable Integer platformId) {
    return Result.ok(mvService.getMVsLink(vids, platformId));
  }
}
