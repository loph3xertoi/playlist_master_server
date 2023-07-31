package com.daw.pms.Controller;

import com.daw.pms.DTO.Result;
import com.daw.pms.Entity.Basic.BasicLibrary;
import com.daw.pms.Service.PMS.LibraryService;
import java.util.List;
import java.util.Map;
import org.springframework.cache.annotation.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;

/**
 * Library controller.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 7/23/23
 */
@RestController
@CacheConfig(cacheNames = "library-cache")
@Cacheable(key = "#root.methodName + '(' + #root.args + ')'", unless = "!#result.success")
public class LibraryController {
  private final LibraryService libraryService;

  public LibraryController(LibraryService libraryService) {
    this.libraryService = libraryService;
  }

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
  public Result getLibraries(@RequestParam Long id, @RequestParam Integer platform) {
    List<BasicLibrary> libraries;
    try {
      libraries = libraryService.getLibraries(id, platform);
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
    return Result.ok(libraries, (long) libraries.size());
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
    if (detailLibrary == null && platform == 1) {
      return Result.fail("This library cannot access");
    }
    return Result.ok(detailLibrary);
  }

  /**
   * Create new library in {@code platform}.
   *
   * @param library A map that contains the name of library.
   * @param platform Which platform the library belongs to.
   * @return The response of request wrapped by Result DTO.
   * @apiNote POST /library?platform={@code platform} {"name": "{@code name}"}
   */
  @PostMapping("/library")
  @CacheEvict(key = "'getLibraries(0,'+#platform+')'")
  public Result createLibrary(
      @RequestBody Map<String, String> library, @RequestParam Integer platform) {
    try {
      return libraryService.createLibrary(library, platform);
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
  }

  /**
   * Delete the library in {@code platform}.
   *
   * @param libraryId The id of library, multiple libraries separated with comma.
   * @param platform Which platform the library belongs to.
   * @return The response of request wrapped by Result DTO.
   * @apiNote DELETE /library/{@code libraryId}?platform={@code platform}
   */
  @DeleteMapping("/library/{libraryId}")
  @CacheEvict(key = "'getLibraries(0,'+#platform+')'")
  public Result deleteLibrary(@PathVariable String libraryId, @RequestParam Integer platform) {
    try {
      return libraryService.deleteLibrary(libraryId, platform);
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
  }

  /**
   * Add songs {@code songsId} to library {@code libraryId} in platform {@code platform}, tid is
   * used to evict cache.
   *
   * @param requestBody A map that contains songs id and target library's id.
   * @param platform Which platform the library belongs to.
   * @return The response of request wrapped by Result DTO.
   * @apiNote POST /addSongsToLibrary?platform={@code platform}
   *     {"libraryId":"libraryId","songsId":"songsId","tid":"tid"}
   */
  @PostMapping("/addSongsToLibrary")
  @Caching(
      evict = {
        @CacheEvict(key = "'getDetailLibrary('+#requestBody.get('tid')+','+#platform+')'"),
        @CacheEvict(key = "'getLibraries(0,'+#platform+')'")
      })
  public Result addSongsToLibrary(
      @RequestBody Map<String, String> requestBody, @RequestParam Integer platform) {
    String libraryId = requestBody.get("libraryId");
    String songsId = requestBody.get("songsId");
    try {
      return libraryService.addSongsToLibrary(libraryId, songsId, platform);
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
  }

  /**
   * Move songs {@code songsId} from source library with {@code fromLibrary} to target library with
   * {@code toLibrary}.
   *
   * @param requestBody A map that contains parameters, fromTid and toTid are used to evict cache.
   * @param platform Which platform the library belongs to.
   * @return The response of request wrapped by Result DTO.
   * @apiNote PUT /moveSongsToOtherLibrary?platform={@code platform}
   *     {"songsId":"songsId","fromLibrary":"fromLibrary","toLibrary":"toLibrary","fromTid":"fromTid","toTid":"toTid"}
   */
  @PutMapping("/moveSongsToOtherLibrary")
  @Caching(
      evict = {
        @CacheEvict(key = "'getDetailLibrary('+#requestBody.get('fromTid')+','+#platform+')'"),
        @CacheEvict(key = "'getDetailLibrary('+#requestBody.get('toTid')+','+#platform+')'"),
        @CacheEvict(key = "'getLibraries(0,'+#platform+')'")
      })
  public Result moveSongsToOtherLibrary(
      @RequestBody Map<String, String> requestBody, @RequestParam Integer platform) {
    String songsId = requestBody.get("songsId");
    String fromLibrary = requestBody.get("fromLibrary");
    String toLibrary = requestBody.get("toLibrary");
    try {
      return libraryService.moveSongsToOtherLibrary(songsId, fromLibrary, toLibrary, platform);
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
  }

  /**
   * Remove songs {@code songsId} from library with {@code libraryId} in platform {@code platform}.
   *
   * @param libraryId The id of library.
   * @param songsId The id of songs, multiple songs id separated with comma.
   * @param platform Which platform the library belongs to.
   * @param tid The tid of library, used to evict cache.
   * @return The response of request wrapped by Result DTO.
   * @apiNote DELETE /removeSongsFromLibrary?libraryId={@code libraryId}&songsId={@code
   *     songsId}&platform={@code platform}&tid={@code tid}
   */
  @DeleteMapping("/removeSongsFromLibrary")
  @Caching(
      evict = {
        @CacheEvict(key = "'getDetailLibrary('+#tid+','+#platform+')'"),
        @CacheEvict(key = "'getLibraries(0,'+#platform+')'")
      })
  public Result removeSongsFromLibrary(
      @RequestParam String libraryId,
      @RequestParam String songsId,
      @RequestParam Integer platform,
      @RequestParam String tid) {
    try {
      return libraryService.removeSongsFromLibrary(libraryId, songsId, platform);
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
  }
}
