package com.daw.pms.Controller;

import com.daw.pms.DTO.Result;
import com.daw.pms.Entity.Basic.BasicLibrary;
import com.daw.pms.Service.PMS.LibraryService;
import java.util.List;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
   */
  @GetMapping("/libraries")
  public Result getLibraries(
      @RequestParam(value = "id") String id, @RequestParam(value = "platform") Integer platform) {
    List<BasicLibrary> libraries = libraryService.getLibraries(id, platform);
    return Result.ok(libraries, (long) libraries.size());
  }

  /**
   * Get detail library with {@code library}.
   *
   * @param library The library id.
   * @param platform Which platform the user belongs to.
   * @return Detail library.
   */
  @GetMapping("/detaillibrary/{library}")
  public Result getDetailLibrary(
      @PathVariable(name = "library") String library,
      @RequestParam(value = "platform") Integer platform) {
    BasicLibrary detailLibrary = libraryService.getDetailLibrary(library, platform);
    if (detailLibrary == null) {
      return Result.fail("The tid of playlist doesn't exist.");
    }
    return Result.ok(detailLibrary);
  }
}
