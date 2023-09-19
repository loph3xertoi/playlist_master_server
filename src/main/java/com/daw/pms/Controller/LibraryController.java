package com.daw.pms.Controller;

import com.daw.pms.DTO.Result;
import com.daw.pms.DTO.UpdateLibraryDTO;
import com.daw.pms.Entity.Basic.BasicSong;
import com.daw.pms.Entity.Bilibili.BiliResource;
import com.daw.pms.Entity.NeteaseCloudMusic.NCMSong;
import com.daw.pms.Entity.PMS.PMSSong;
import com.daw.pms.Entity.QQMusic.QQMusicSong;
import com.daw.pms.Service.PMS.LibraryService;
import com.daw.pms.Utils.PmsUserDetailsUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.cache.annotation.*;
import org.springframework.web.bind.annotation.*;

/**
 * Library controller.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 7/23/23
 */
@RestController
@CacheConfig(cacheNames = "library-cache")
public class LibraryController {
  private final LibraryService libraryService;
  private final PmsUserDetailsUtil pmsUserDetailsUtil;

  public LibraryController(LibraryService libraryService, PmsUserDetailsUtil pmsUserDetailsUtil) {
    this.libraryService = libraryService;
    this.pmsUserDetailsUtil = pmsUserDetailsUtil;
  }

  /**
   * Get all libraries with {@code platform}.
   *
   * @param id Your user id in pms, used for cache evicting.
   * @param pn The page number, only used in bilibili.
   * @param ps The page size, only used in bilibili.
   * @param biliPlatform The platform of bilibili, default is web, only used in bilibili.
   * @param type The fav lists type of bilibili, 0 means get created fav lists, 1 means get
   *     collected fav lists, only used in bilibili.
   * @param platform Which platform the user belongs to. 0 represents pms, 1 represents qq music, 2
   *     represents netease cloud music, 3 represents bilibili.
   * @return All libraries for specific platform.
   * @apiNote GET /libraries?id={@code id}&platform={@code platform}
   */
  @Cacheable(key = "#root.methodName + '(' + #root.args + ')'", unless = "!#result.success")
  @GetMapping("/libraries")
  public Result getLibraries(
      @RequestParam Long id,
      @RequestParam(required = false) Integer pn,
      @RequestParam(required = false) Integer ps,
      @RequestParam(required = false) String biliPlatform,
      @RequestParam(required = false) Integer type,
      @RequestParam int platform) {
    Long pmsUserId = pmsUserDetailsUtil.getCurrentLoginUserId();
    return libraryService.getLibraries(pmsUserId, pn, ps, biliPlatform, type, platform);
  }

  /**
   * Create new library in {@code platform}.
   *
   * @param library A map that contains the name of library.
   * @param platform Which platform the library belongs to.
   * @return The response of request wrapped by Result DTO.
   * @apiNote POST /library?platform={@code platform} {"name"(required): "{@code name}",
   *     "intro":intro, "privacy":privacy, "cover":cover}
   */
  @PostMapping("/library")
  public Result createLibrary(
      @RequestBody Map<String, String> library, @RequestParam int platform) {
    return libraryService.createLibrary(library, platform);
  }

  /**
   * Update library in {@code platform}.
   *
   * @param library A map contains fields to update.
   * @param platform Which platform the library belongs to.
   * @return The response of request wrapped by Result DTO.
   * @apiNote PUT /library?platform={@code platform} {"name"(required):name, "intro":intro,
   *     "cover":cover} (PMSDetailLibrary)
   */
  @PutMapping("/library")
  public Result updateLibrary(
      @ModelAttribute UpdateLibraryDTO library, @RequestParam int platform) {
    return libraryService.updateLibrary(library, platform);
  }

  /**
   * Get detail library with {@code library}.
   *
   * @param library The library id.
   * @param pn The page number of library, required in bilibili.
   * @param ps The page size of library, required in bilibili.
   * @param keyword The searching keyword of resources in fav list, only used in bilibili.
   * @param order The sorting order of resources of this fav list, mtime: by collected time, view:
   *     by view time, pubtime: by published time, only used in bilibili.
   * @param range The range of searching, 0: current fav list, 1: all fav lists, only used in
   *     bilibili.
   * @param type 0 for created fav list, 1 for collected fav list, required in bilibili.
   * @param platform Which platform the user belongs to.
   * @return Detail library.
   * @apiNote GET /library/{@code library}?platform={@code platform}
   */
  @Cacheable(key = "#root.methodName + '(' + #root.args + ')'", unless = "!#result.success")
  @GetMapping("/library/{library}")
  public Result getDetailLibrary(
      @PathVariable String library,
      @RequestParam(required = false) Integer pn,
      @RequestParam(required = false) Integer ps,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) String order,
      @RequestParam(required = false) Integer range,
      @RequestParam(required = false) Integer type,
      @RequestParam int platform) {
    return libraryService.getDetailLibrary(library, pn, ps, keyword, order, range, type, platform);
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
  public Result deleteLibrary(@PathVariable String libraryId, @RequestParam int platform) {
    return libraryService.deleteLibrary(libraryId, platform);
  }

  /**
   * Add songs {@code songsId} to library {@code libraryId} in platform {@code platform}, tid is
   * used to evict cache.
   *
   * @param requestBody A map that contains songs id and target library's id.
   * @param platform Which platform the library belongs to.
   * @return The response of request wrapped by Result DTO.
   * @apiNote POST /addSongsToLibrary?platform={@code platform}
   *     {"libraryId":"libraryId","songsId":"songsId","tid":"tid"}, in bilibili, still need
   *     biliSourceFavListId and isFavoriteSearchedResource field, biliSourceFavListId for add
   *     resources that already in fav list to other fav lists, isFavoriteSearchedResource for add
   *     searched resource to fav list; If add songs to pms library, still need songs field and
   *     isAddToPMSLibrary set to true; If add bilibili resources, still need resources field and
   *     isAddToPMSLibrary set to true.
   */
  @PostMapping("/addSongsToLibrary")
  public Result addSongsToLibrary(
      @RequestBody Map<String, Object> requestBody, @RequestParam int platform) {
    String libraryId = String.valueOf(requestBody.get("libraryId"));
    String biliSourceFavListId = String.valueOf(requestBody.get("biliSourceFavListId"));
    String songsIds = String.valueOf(requestBody.get("songsIds"));
    List<Map<String, Object>> songs = (List<Map<String, Object>>) requestBody.get("songs");
    List<BasicSong> basicSongs = null;
    if (songs != null && !songs.isEmpty()) {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      basicSongs = new ArrayList<>(songs.size());
      Class<? extends BasicSong> basicSongClass;
      if (platform == 0) {
        basicSongClass = PMSSong.class;
      } else if (platform == 1) {
        basicSongClass = QQMusicSong.class;
      } else if (platform == 2) {
        basicSongClass = NCMSong.class;
      } else {
        throw new RuntimeException("Invalid platform");
      }
      for (Map<String, Object> song : songs) {
        BasicSong basicSong = objectMapper.convertValue(song, basicSongClass);
        basicSongs.add(basicSong);
      }
    }
    List<Map<String, Object>> resources = (List<Map<String, Object>>) requestBody.get("resources");
    List<BiliResource> biliResources = null;
    if (resources != null && !resources.isEmpty()) {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      biliResources = new ArrayList<>(resources.size());
      for (Map<String, Object> resource : resources) {
        BiliResource biliResource = objectMapper.convertValue(resource, BiliResource.class);
        biliResources.add(biliResource);
      }
    }
    Boolean isAddToPMSLibrary = (Boolean) requestBody.get("isAddToPMSLibrary");
    Boolean isFavoriteSearchedResource = (Boolean) requestBody.get("isFavoriteSearchedResource");
    return libraryService.addSongsToLibrary(
        libraryId,
        biliSourceFavListId,
        songsIds,
        basicSongs,
        biliResources,
        isAddToPMSLibrary,
        isFavoriteSearchedResource,
        platform);
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
  public Result moveSongsToOtherLibrary(
      @RequestBody Map<String, String> requestBody, @RequestParam int platform) {
    String songsId = requestBody.get("songsId");
    String fromLibrary = requestBody.get("fromLibrary");
    String toLibrary = requestBody.get("toLibrary");
    return libraryService.moveSongsToOtherLibrary(songsId, fromLibrary, toLibrary, platform);
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
  public Result removeSongsFromLibrary(
      @RequestParam String libraryId,
      @RequestParam String songsId,
      @RequestParam String tid,
      @RequestParam int platform) {
    return libraryService.removeSongsFromLibrary(libraryId, songsId, platform);
  }
}
