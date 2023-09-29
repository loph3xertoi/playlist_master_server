package com.daw.pms.Controller;

import com.daw.pms.DTO.Result;
import com.daw.pms.Entity.Basic.BasicSong;
import com.daw.pms.Service.PMS.SongService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

  /**
   * Constructor for SongController.
   *
   * @param songService a {@link com.daw.pms.Service.PMS.SongService} object.
   */
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
  @Operation(summary = "Get detail song.")
  @ApiResponse(description = "The detail song, wrapped with Result DTO, the data is subclass of BasicSong.")
  @GetMapping("/song/{songId}")
  public Result getDetailSong(
      @Parameter(description = "The song id.") @PathVariable String songId,
      @Parameter(description = "The platform id.") @RequestParam int platform) {
    return songService.getDetailSong(songId, platform);
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
  @Operation(summary = "Get similar songs.")
  @ApiResponse(description = "The similar songs.")
  @GetMapping("/similarSongs/{songId}")
  public Result getSimilarSongs(
      @Parameter(description = "The song id.") @PathVariable String songId,
      @Parameter(description = "The pms song type, only used in pms platform.") @RequestParam(required = false) Integer songType,
      @Parameter(description = "The platform id.") @RequestParam int platform) {
    List<? extends BasicSong> similarSongs =
        songService.getSimilarSongs(songId, songType, platform);
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
  @Operation(summary = "Get songs link.")
  @ApiResponse(description = "Songs' links.")
  @GetMapping("/songsLink/{SongIds}")
  @CacheEvict
  public Result getSongsLink(
      @Parameter(description = "The song's id, multiple songs separated with comma.") @PathVariable String SongIds,
      @Parameter(description = "The platform id.") @RequestParam int platform) {
    return songService.getSongsLink(SongIds, "standard", platform);
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
  @Operation(summary = "Search song by keyword.")
  @ApiResponse(description = "The search result with page.")
  @GetMapping("/search/song/{keyword}")
  public Result searchSong(
      @Parameter(description = "The song name to search.") @PathVariable String keyword,
      @Parameter(description = "The page number.") @RequestParam int pageNo,
      @Parameter(description = "The page size.") @RequestParam int pageSize,
      @Parameter(description = "The platform id.") @RequestParam int platform) {
    return songService.searchResourcesByKeyword(keyword, pageNo, pageSize, 1, platform);
  }
}
