package com.daw.pms.Controller;

import com.daw.pms.DTO.Result;
import com.daw.pms.Entity.Basic.BasicLibrary;
import com.daw.pms.Service.PMS.LibraryService;
import java.util.List;
import java.util.Map;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.web.bind.annotation.*;

@RestController
@CacheConfig(cacheNames = "library-cache")
@Cacheable(key = "#root.methodName + '(' + #root.args + ')'")
public class LibraryController {
  private final LibraryService libraryService;

  /**
   * Get all libraries with {@code platform}.
   *
   * @param id Your user id in pms.
   * @param platform Which platform the user belongs to. 0 represents pms, 1 represents qq music, 2
   *     * represents netease music, 3 represents bilibili.
   * @return All libraries for specific platform.
   * @apiNote GET /libraries?id={@code id}&platform={@code platform}
   */
  @GetMapping("/libraries")
  @CacheEvict
  public Result getLibraries(@RequestParam String id, @RequestParam Integer platform) {
    List<BasicLibrary> libraries = libraryService.getLibraries(id, platform);
    return Result.ok(libraries, (long) libraries.size());
  }

  public LibraryController(LibraryService libraryService) {
    this.libraryService = libraryService;
  }

  /**
   * Get detail library with {@code library}.
   *
   * @param library The library id.
   * @param platform Which platform the user belongs to.
   * @return Detail library.
   * @apiNote GET /library/{@code library}?platform={@code platform}
   */
  @GetMapping("/library/{library}")
  public Result getDetailLibrary(@PathVariable String library, @RequestParam Integer platform) {
    BasicLibrary detailLibrary = libraryService.getDetailLibrary(library, platform);
    if (detailLibrary == null && platform == 1) {
      return Result.fail("The tid of playlist doesn't exist.");
    }
    return Result.ok(detailLibrary);
  }

  /**
   * Create new library in {@code platform}.
   *
   * @param library A map that contains the name of library.
   * @param platform Which platform the library belongs to.
   * @return Map result for creating library.
   * @apiNote POST /library?platform={@code platform} {"name": "{@code name}"}
   */
  @PostMapping("/library")
  @CacheEvict(value = "library-cache", key = "'getLibraries(0,'+#platform+')'")
  public Result createLibrary(
      @RequestBody Map<String, String> library, @RequestParam Integer platform) {
    Map<String, Object> result = libraryService.createLibrary(library, platform);
    return Result.ok(result);
  }

  /**
   * Delete the library in {@code platform}.
   *
   * @param libraryId The id of library, multiple libraries separated with comma.
   * @param platform Which platform the library belongs to.
   * @return Map result for deleting library.
   * @apiNote DELETE /library/{@code libraryId}?platform={@code platform}
   */
  @DeleteMapping("/library/{libraryId}")
  @CacheEvict(value = "library-cache", key = "'getLibraries(0,'+#platform+')'")
  public Result deleteLibrary(@PathVariable String libraryId, @RequestParam Integer platform) {
    Map<String, Object> result = libraryService.deleteLibrary(libraryId, platform);
    return Result.ok(result);
  }

  /**
   * Add songs {@code songsMid} to library {@code dirId} in platform {@code platform}, tid is used
   * to evict cache.
   *
   * @param requestBody A map that contains songs mid and target library's dir id.
   * @param platform Which platform the library belongs to.
   * @return Map result for adding songs.
   * @apiNote POST /addSongsToLibrary?platform={@code platform}
   *     {"dirid":"dirId","mid":"songsMid","tid":"tid"}
   */
  @PostMapping("/addSongsToLibrary")
  @CacheEvict(
      value = "library-cache",
      key = "'getDetailLibrary('+#requestBody.get('tid')+','+#platform+')'")
  public Result addSongsToLibrary(
      @RequestBody Map<String, String> requestBody, @RequestParam Integer platform) {
    Integer dirId = Integer.parseInt(requestBody.get("dirid"));
    String songsMid = requestBody.get("mid");
    Map<String, Object> result = libraryService.addSongsToLibrary(dirId, songsMid, platform);
    return Result.ok(result);
  }

  /**
   * Move songs {@code songsId} from library with {@code fromDirId} to library with {@code toDirId}.
   *
   * @param requestBody A map that contains parameters, fromTid and toTid are used to evict cache.
   * @param platform Which platform the library belongs to.
   * @return Map result for moving songs.
   * @apiNote PUT /moveSongsToOtherLibrary?platform={@code platform}
   *     {"songsId":"songsId","fromDirId":"fromDirId","toDirId":"toDirId","fromTid":"fromTid","toTid":"toTid"}
   */
  @PutMapping("/moveSongsToOtherLibrary")
  @Caching(
      evict = {
        @CacheEvict(
            value = "library-cache",
            key = "'getDetailLibrary('+#requestBody.get('fromTid')+','+#platform+')'"),
        @CacheEvict(
            value = "library-cache",
            key = "'getDetailLibrary('+#requestBody.get('toTid')+','+#platform+')'")
      })
  public Result moveSongsToOtherLibrary(
      @RequestBody Map<String, String> requestBody, @RequestParam Integer platform) {
    String songsId = requestBody.get("songsId");
    Integer fromDirId = Integer.parseInt(requestBody.get("fromDirId"));
    Integer toDirId = Integer.parseInt(requestBody.get("toDirId"));
    Map<String, Object> result =
        libraryService.moveSongsToOtherLibrary(songsId, fromDirId, toDirId, platform);
    return Result.ok(result);
  }

  /**
   * Remove songs {@code songsId} from library with {@code libraryId} in platform {@code platform}.
   *
   * @param dirId The id of library.
   * @param tid The tid of library, used to evict cache.
   * @param songsId The id of songs, multiple songs id separated with comma.
   * @param platform Which platform the library belongs to.
   * @return Map result for removing songs.
   * @apiNote DELETE /removeSongsFromLibrary?dirId={@code dirId}&tid={@code tid}&songsId={@code
   *     songsId}&platform={@code platform}
   */
  @DeleteMapping("/removeSongsFromLibrary")
  @CacheEvict(value = "library-cache", key = "'getDetailLibrary('+#tid+','+#platform+')'")
  public Result removeSongsFromLibrary(
      @RequestParam Integer dirId,
      @RequestParam String tid,
      @RequestParam String songsId,
      @RequestParam Integer platform) {
    Map<String, Object> result = libraryService.removeSongsFromLibrary(dirId, songsId, platform);
    return Result.ok(result);
  }
}
