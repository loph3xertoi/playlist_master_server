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
   */
  @GetMapping("/song/{songMid}")
  public Result getDetailSong(
      @PathVariable(name = "songMid") String songMid,
      @RequestParam(value = "platform") Integer platform) {
    return Result.ok(songService.getDetailSong(songMid, platform));
  }

  /**
   * Get similar songs with song mid {@code songMid}.
   *
   * @param songId The song mid.
   * @param platform The platform id.
   * @return The similar songs.
   */
  @GetMapping("/similarsongs/{songId}")
  public Result getSimilarSongs(
      @PathVariable(name = "songId") String songId,
      @RequestParam(value = "platform") Integer platform) {
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
   */
  @GetMapping("/songlink/{songMid}")
  public Result getSongLink(
      @PathVariable(name = "songMid") String songMid,
      @RequestParam(value = "mediaMid") String mediaMid,
      @RequestParam(value = "type") String type,
      @RequestParam(value = "platform") Integer platform) {
    return Result.ok(songService.getSongLink(songMid, type, mediaMid, platform));
  }

  /**
   * Get songs link.
   *
   * @param songMids The song mids.
   * @param platform The platform id.
   * @return The urls of your songs with mid {@code songMids}.
   */
  @GetMapping("/songslink/{songMids}")
  public Result getSongsLink(
      @PathVariable(name = "songMids") String songMids,
      @RequestParam(value = "platform") Integer platform) {
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
      @PathVariable(name = "name") String name,
      @RequestParam(value = "pageNo") Integer pageNo,
      @RequestParam(value = "pageSize") Integer pageSize,
      @RequestParam(value = "platform") Integer platform) {
    return Result.ok(songService.searchSongByName(name, pageNo, pageSize, platform));
  }
}
