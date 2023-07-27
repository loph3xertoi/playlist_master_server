package com.daw.pms.Controller;

import com.daw.pms.DTO.Result;
import com.daw.pms.Entity.Basic.BasicPagedSongs;
import com.daw.pms.Entity.Basic.BasicSong;
import com.daw.pms.Service.PMS.SongService;
import java.util.List;
import java.util.Map;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;

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
   * @return The detail song.
   * @apiNote GET /song/{@code songId}?platform={@code platform}
   */
  @GetMapping("/song/{songId}")
  public Result getDetailSong(@PathVariable String songId, @RequestParam Integer platform) {
    BasicSong detailSong;
    try {
      detailSong = songService.getDetailSong(songId, platform);
    } catch (ResourceAccessException e) {
      String remoteServer =
          platform == 0
              ? "pms"
              : platform == 1
                  ? "proxy qqmusic server"
                  : platform == 2 ? "proxy ncm server" : "proxy bilibili server";
      String errorMsg = "Fail to connect to " + remoteServer;
      return Result.fail(errorMsg);
    } catch (Exception e) {
      return Result.fail(e.getMessage());
    }
    return Result.ok(detailSong);
  }

  /**
   * Get similar songs with song id {@code songId}.
   *
   * @param songId The song id.
   * @param platform The platform id.
   * @return The similar songs.
   * @apiNote GET /similarSongs/{@code songId}?platform={@code platform}
   */
  @GetMapping("/similarSongs/{songId}")
  public Result getSimilarSongs(@PathVariable String songId, @RequestParam Integer platform) {
    List<BasicSong> similarSongs;
    try {
      similarSongs = songService.getSimilarSongs(songId, platform);
    } catch (ResourceAccessException e) {
      String remoteServer =
          platform == 0
              ? "pms"
              : platform == 1
                  ? "proxy qqmusic server"
                  : platform == 2 ? "proxy ncm server" : "proxy bilibili server";
      String errorMsg = "Fail to connect to " + remoteServer;
      return Result.fail(errorMsg);
    } catch (Exception e) {
      return Result.fail(e.getMessage());
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
  //      @RequestParam Integer platform) {
  //    return Result.ok(songService.getSongsLink(songMid, type, mediaMid, platform));
  //  }

  /**
   * Get songs link.
   *
   * @param SongIds The song's id, multiple songs separated with comma.
   * @param platform The platform id.
   * @return The urls of your songs with {@code SongIds}.
   * @apiNote GET /songsLink/{@code SongIds}?platform={@code platform}
   */
  @GetMapping("/songsLink/{SongIds}")
  public Result getSongsLink(@PathVariable String SongIds, @RequestParam Integer platform) {
    Map<String, String> songsLink;
    try {
      songsLink = songService.getSongsLink(SongIds, "standard", platform);
    } catch (ResourceAccessException e) {
      String remoteServer =
          platform == 0
              ? "pms"
              : platform == 1
                  ? "proxy qqmusic server"
                  : platform == 2 ? "proxy ncm server" : "proxy bilibili server";
      String errorMsg = "Fail to connect to " + remoteServer;
      return Result.fail(errorMsg);
    } catch (Exception e) {
      return Result.fail(e.getMessage());
    }
    return Result.ok(songsLink);
  }

  /**
   * Search song by keywords.
   *
   * @param keywords The song name to search.
   * @param offset The page number.
   * @param limit The page size.
   * @param platform The platform id.
   * @return The search result with page.
   */
  @GetMapping("/search/song/{keywords}")
  public Result searchSong(
      @PathVariable String keywords,
      @RequestParam Integer offset,
      @RequestParam Integer limit,
      @RequestParam Integer platform) {
    BasicPagedSongs pagedSongs;
    try {
      pagedSongs = songService.searchResourcesByKeywords(keywords, offset, limit, 1, platform);
    } catch (ResourceAccessException e) {
      String remoteServer =
          platform == 0
              ? "pms"
              : platform == 1
                  ? "proxy qqmusic server"
                  : platform == 2 ? "proxy ncm server" : "proxy bilibili server";
      String errorMsg = "Fail to connect to " + remoteServer;
      return Result.fail(errorMsg);
    } catch (Exception e) {
      return Result.fail(e.getMessage());
    }
    return Result.ok(pagedSongs);
  }
}
