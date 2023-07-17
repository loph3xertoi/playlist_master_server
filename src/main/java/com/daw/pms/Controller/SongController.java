package com.daw.pms.Controller;

import com.daw.pms.DTO.Result;
import com.daw.pms.Entity.Basic.BasicSong;
import com.daw.pms.Service.PMS.SongService;
import java.util.List;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CacheConfig(cacheNames = "song-cache")
@Cacheable(key = "#root.methodName + '(' + #root.args + ')'")
public class SongController {
  private final SongService songService;

  public SongController(SongService songService) {
    this.songService = songService;
  }

  /**
   * Get detail song with song mid {@code songMid}.
   *
   * @param songMid The song mid.
   * @param platform The platform id.
   * @return The detail song.
   * @apiNote GET /song/{@code songMid}?platform={@code platform}
   */
  @GetMapping("/song/{songMid}")
  public Result getDetailSong(@PathVariable String songMid, @RequestParam Integer platform) {
    return Result.ok(songService.getDetailSong(songMid, platform));
  }

  /**
   * Get similar songs with song mid {@code songMid}.
   *
   * @param songId The song mid.
   * @param platform The platform id.
   * @return The similar songs.
   * @apiNote GET /similarSongs/{@code songId}?platform={@code platform}
   */
  @GetMapping("/similarSongs/{songId}")
  public Result getSimilarSongs(@PathVariable String songId, @RequestParam Integer platform) {
    List<BasicSong> similarSongs = songService.getSimilarSongs(songId, platform);
    return Result.ok(similarSongs, (long) similarSongs.size());
  }

  /**
   * Get song link with song mid {@code songMid}.
   *
   * @param songMid The song mid.
   * @param mediaMid The media mid.
   * @param type The quality(128, 320, flac, m4a, ogg) of song you want to get.
   * @param platform The platform id.
   * @return The url of your song with mid {@code songMid} and mediaMid {@code mediaMid} and type
   *     {@code type}.
   * @apiNote GET /songLink/{@code songMid}?mediaMid={@code mediaMid}&type={@code
   *     type}&platform={@code platform}
   */
  @GetMapping("/songLink/{songMid}")
  public Result getSongLink(
      @PathVariable String songMid,
      @RequestParam String mediaMid,
      @RequestParam String type,
      @RequestParam Integer platform) {
    return Result.ok(songService.getSongLink(songMid, type, mediaMid, platform));
  }

  /**
   * Get songs link.
   *
   * @param songMids The song mids.
   * @param platform The platform id.
   * @return The urls of your songs with mid {@code songMids}.
   * @apiNote GET /songsLink/{@code songMids}?platform={@code platform}
   */
  @GetMapping("/songsLink/{songMids}")
  public Result getSongsLink(@PathVariable String songMids, @RequestParam Integer platform) {
    return Result.ok(songService.getSongsLink(songMids, platform));
  }

  /**
   * Search song by name.
   *
   * @param name The song name to search.
   * @param pageNo The page number.
   * @param pageSize The page size.
   * @param platform The platform id.
   * @return The search result with page.
   */
  @GetMapping("/search/song/{name}")
  public Result searchSong(
      @PathVariable String name,
      @RequestParam Integer pageNo,
      @RequestParam Integer pageSize,
      @RequestParam Integer platform) {
    return Result.ok(songService.searchSongByName(name, pageNo, pageSize, platform));
  }
}
