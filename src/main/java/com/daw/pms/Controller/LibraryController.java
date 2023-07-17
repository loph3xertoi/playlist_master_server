package com.daw.pms.Controller;

import com.daw.pms.DTO.Result;
import com.daw.pms.Entity.Basic.BasicLibrary;
import com.daw.pms.Service.PMS.LibraryService;
import java.util.List;
import java.util.Map;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

@RestController
@CacheConfig(cacheNames = "library-cache")
@Cacheable(key = "#root.methodName + '(' + #root.args + ')'")
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
   *     * represents netease music, 3 represents bilibili.
   * @return All libraries for specific platform.
   * @apiNote GET /libraries?id={@code id}&platform={@code platform}
   */
  @GetMapping("/libraries")
  public Result getLibraries(@RequestParam String id, @RequestParam Integer platform) {
    List<BasicLibrary> libraries = libraryService.getLibraries(id, platform);
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
  @CacheEvict
  public Result createLibrary(
      @RequestBody Map<String, String> library, @RequestParam Integer platform) {
    Map<String, Object> result = libraryService.createLibrary(library, platform);
    return Result.ok(result);
  }

  /**
   * Delete the library in {@code platform}.
   *
   * @param libraryId The id of library.
   * @param platform Which platform the library belongs to.
   * @return Map result for deleting library.
   * @apiNote DELETE /library/{@code libraryId}?platform={@code platform}
   */
  @DeleteMapping("/library/{libraryId}")
  @CacheEvict
  public Result deleteLibrary(@PathVariable String libraryId, @RequestParam Integer platform) {
    Map<String, Object> result = libraryService.deleteLibrary(libraryId, platform);
    return Result.ok(result);
  }
}
