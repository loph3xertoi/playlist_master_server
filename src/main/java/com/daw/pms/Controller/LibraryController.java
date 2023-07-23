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
   *     * represents netease cloud music, 3 represents bilibili.
   * @return All libraries for specific platform.
   * @apiNote GET /libraries?id={@code id}&platform={@code platform}
   */
  @GetMapping("/libraries")
  @CacheEvict
  public Result getLibraries(@RequestParam Long id, @RequestParam Integer platform) {
    List<BasicLibrary> libraries;
    try {
      libraries = libraryService.getLibraries(id, platform);
    } catch (Exception e) {
      return Result.fail(e.getMessage());
    }
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
    BasicLibrary detailLibrary;
    try {
      detailLibrary = libraryService.getDetailLibrary(library, platform);
    } catch (Exception e) {
      return Result.fail(e.getMessage());
    }
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
   * @return If success, return the library id of created library; if failure, return failure tip.
   * @apiNote POST /library?platform={@code platform} {"name": "{@code name}"}
   */
  @PostMapping("/library")
  @CacheEvict(value = "library-cache", key = "'getLibraries(0,'+#platform+')'")
  public Result createLibrary(
      @RequestBody Map<String, String> library, @RequestParam Integer platform) {
    Long createdLibraryId;
    try {
      createdLibraryId = libraryService.createLibrary(library, platform);
    } catch (Exception e) {
      return Result.fail(e.getMessage());
    }
    if (createdLibraryId != null) {
      return Result.ok(createdLibraryId);
    } else {
      return Result.fail("Fail to create library.");
    }
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
    Map<String, Object> result;
    try {
      result = libraryService.deleteLibrary(libraryId, platform);
    } catch (Exception e) {
      return Result.fail(e.getMessage());
    }
    return Result.ok(result);
  }

  /**
   * Add songs {@code songsId} to library {@code libraryId} in platform {@code platform}, tid is
   * used to evict cache.
   *
   * @param requestBody A map that contains songs id and target library's id.
   * @param platform Which platform the library belongs to.
   * @return Map result for adding songs.
   * @apiNote POST /addSongsToLibrary?platform={@code platform}
   *     {"libraryId":"libraryId","songsId":"songsId","tid":"tid"}
   */
  @PostMapping("/addSongsToLibrary")
  @CacheEvict(
      value = "library-cache",
      key = "'getDetailLibrary('+#requestBody.get('tid')+','+#platform+')'")
  public Result addSongsToLibrary(
      @RequestBody Map<String, String> requestBody, @RequestParam Integer platform) {
    String libraryId = requestBody.get("libraryId");
    String songsId = requestBody.get("songsId");
    Map<String, Object> result;
    try {
      result = libraryService.addSongsToLibrary(libraryId, songsId, platform);
    } catch (Exception e) {
      return Result.fail(e.getMessage());
    }
    return Result.ok(result);
  }

  /**
   * Move songs {@code songsId} from source library with {@code fromLibrary} to target library with
   * {@code toLibrary}.
   *
   * @param requestBody A map that contains parameters, fromTid and toTid are used to evict cache.
   * @param platform Which platform the library belongs to.
   * @return Map result for moving songs.
   * @apiNote PUT /moveSongsToOtherLibrary?platform={@code platform}
   *     {"songsId":"songsId","fromLibrary":"fromLibrary","toLibrary":"toLibrary","fromTid":"fromTid","toTid":"toTid"}
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
    String fromLibrary = requestBody.get("fromLibrary");
    String toLibrary = requestBody.get("toLibrary");
    Map<String, Object> result;
    try {
      result = libraryService.moveSongsToOtherLibrary(songsId, fromLibrary, toLibrary, platform);
    } catch (Exception e) {
      return Result.fail(e.getMessage());
    }
    return Result.ok(result);
  }

  /**
   * Remove songs {@code songsId} from library with {@code libraryId} in platform {@code platform}.
   *
   * @param libraryId The id of library.
   * @param songsId The id of songs, multiple songs id separated with comma.
   * @param platform Which platform the library belongs to.
   * @param tid The tid of library, used to evict cache.
   * @return Map result for removing songs.
   * @apiNote DELETE /removeSongsFromLibrary?libraryId={@code libraryId}&songsId={@code
   *     songsId}&platform={@code platform}&tid={@code tid}
   */
  @DeleteMapping("/removeSongsFromLibrary")
  @CacheEvict(value = "library-cache", key = "'getDetailLibrary('+#tid+','+#platform+')'")
  public Result removeSongsFromLibrary(
      @RequestParam String libraryId,
      @RequestParam String songsId,
      @RequestParam Integer platform,
      @RequestParam String tid) {
    Map<String, Object> result;
    try {
      result = libraryService.removeSongsFromLibrary(libraryId, songsId, platform);
    } catch (Exception e) {
      return Result.fail(e.getMessage());
    }
    return Result.ok(result);
  }
}
