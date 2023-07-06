package com.daw.pms.Controller;

import com.daw.pms.DTO.Result;
import com.daw.pms.Entity.QQMusicBasicSong;
import com.daw.pms.Service.SongService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SongController {
  private final SongService songService;

  public SongController(SongService songService) {
    this.songService = songService;
  }

  @GetMapping("/song/{songMid}/{platformId}")
  public Result getDetailSong(
      @PathVariable(name = "songMid") String songMid,
      @PathVariable(name = "platformId") Integer platformId) {
    return Result.ok(songService.getDetailSong(songMid, platformId));
  }

  @GetMapping("/similarsongs/{songId}/{platformId}")
  public Result getSimilarSongs(
      @PathVariable(name = "songId") String songId,
      @PathVariable(name = "platformId") Integer platformId) {
    List<QQMusicBasicSong> similarSongs = songService.getSimilarSongs(songId, platformId);
    return Result.ok(similarSongs, (long) similarSongs.size());
  }

  @GetMapping("/songlink/{platformId}")
  public Result getSongLink(
      @PathVariable(name = "platformId") Integer platformId,
      @RequestParam(value = "songMid") String songMid,
      @RequestParam(value = "mediaMid") String mediaMid,
      @RequestParam(value = "type") String type) {
    return Result.ok(songService.getSongLink(songMid, type, mediaMid, platformId));
  }
}
