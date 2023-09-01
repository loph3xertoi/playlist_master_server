package com.daw.pms.Controller;

import com.daw.pms.DTO.Result;
import com.daw.pms.Entity.Basic.BasicSong;
import com.daw.pms.Service.PMS.SongService;
import java.util.List;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Song controller.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 7/23/23
 */
@RestController
@CacheConfig(cacheNames = "song-cache")
@Cacheable(key = "#root.methodName + '(' + #root.args + ')'", unless = "!#result.success")
public class SongController {
  private final SongService songService;

  public SongController(SongService songService) {
    this.songService = songService;
  }

  /**
   * Get detail song with song id {@code songId}.
   *
   * @param songId The song id.
   * @param platform The platform id.
   * @return The detail song, wrapped with Result DTO, the data is subclass of BasicSong.
   * @apiNote GET /song/{@code songId}?platform={@code platform}
   */
  @GetMapping("/song/{songId}")
  public Result getDetailSong(@PathVariable String songId, @RequestParam int platform) {
    try {
      return songService.getDetailSong(songId, platform);
    } catch (Exception e) {
      return Result.fail(e.getMessage() + "\n#0\t" + e.getStackTrace()[0].toString());
    }
  }

  /**
   * Get similar songs with song id {@code songId}.
   *
   * @param songId The song id.
   * @param songType The pms song type, only used in pms platform.
   * @param platform The platform id.
   * @return The similar songs.
   * @apiNote GET /similarSongs/{@code songId}?platform={@code platform}
   */
  @GetMapping("/similarSongs/{songId}")
  public Result getSimilarSongs(
      @PathVariable String songId,
      @RequestParam(required = false) Integer songType,
      @RequestParam int platform) {
    List<? extends BasicSong> similarSongs;
    try {
      similarSongs = songService.getSimilarSongs(songId, songType, platform);
    } catch (Exception e) {
      return Result.fail(e.getMessage() + "\n#0\t" + e.getStackTrace()[0].toString());
    }
    return Result.ok(similarSongs, (long) similarSongs.size());
  }

  //  /**
  //   * Get song link with song mid {@code songMid}.
  //   *
  //   * @param songMid The song mid.
  //   * @param mediaMid The media mid.
  //   * @param type The quality(128, 320, flac, m4a, ogg) of song you want to get.
  //   * @param platform The platform id.
  //   * @return The url of your song with mid {@code songMid} and mediaMid {@code mediaMid} and
  // type
  //   *     {@code type}.
  //   * @apiNote GET /songLink/{@code songMid}?mediaMid={@code mediaMid}&type={@code
  //   *     type}&platform={@code platform}
  //   */
  //  @GetMapping("/songLink/{songMid}")
  //  public Result getSongLink(
  //      @PathVariable String songMid,
  //      @RequestParam String mediaMid,
  //      @RequestParam String type,
  //      @RequestParam int platform) {
  //    return Result.ok(songService.getSongsLink(songMid, type, mediaMid, platform));
  //  }

  /**
   * Get songs link.
   *
   * @param SongIds The song's id, multiple songs separated with comma.
   * @param platform The platform id.
   * @return The urls of your songs with {@code SongIds}, wrapped in Result DTO.
   * @apiNote GET /songsLink/{@code SongIds}?platform={@code platform}
   */
  @GetMapping("/songsLink/{SongIds}")
  @CacheEvict
  public Result getSongsLink(@PathVariable String SongIds, @RequestParam int platform) {
    try {
      return songService.getSongsLink(SongIds, "standard", platform);
    } catch (Exception e) {
      return Result.fail(e.getMessage() + "\n#0\t" + e.getStackTrace()[0].toString());
    }
  }

  /**
   * Search song by keyword.
   *
   * @param keyword The song name to search.
   * @param pageNo The page number.
   * @param pageSize The page size.
   * @param platform The platform id.
   * @return The search result with page.
   */
  @GetMapping("/search/song/{keyword}")
  public Result searchSong(
      @PathVariable String keyword,
      @RequestParam int pageNo,
      @RequestParam int pageSize,
      @RequestParam int platform) {
    try {
      return songService.searchResourcesByKeyword(keyword, pageNo, pageSize, 1, platform);
    } catch (Exception e) {
      return Result.fail(e.getMessage() + "\n#0\t" + e.getStackTrace()[0].toString());
    }
  }
}
