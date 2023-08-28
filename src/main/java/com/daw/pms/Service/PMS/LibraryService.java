package com.daw.pms.Service.PMS;

import com.daw.pms.DTO.Result;
import com.daw.pms.DTO.UpdateLibraryDTO;
import com.daw.pms.Entity.Basic.BasicSong;
import com.daw.pms.Entity.Bilibili.BiliResource;
import java.util.List;
import java.util.Map;

/**
 * Service for handle playlists or favorites.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 7/11/23
 */
public interface LibraryService {
  /**
   * Get all libraries for specific platform.
   *
   * @param id Your user id in pms.
   * @param pn The page number, only used in bilibili and pms, start from 1.
   * @param ps The page size, only used in bilibili and pms.
   * @param biliPlatform The platform of bilibili, default is web, only used in bilibili.
   * @param type The fav lists type of bilibili, 0 means get created fav lists, 1 means get
   *     collected fav lists, only used in bilibili.
   * @param platform Which platform the user belongs to. 0 represents pms, 1 represents qq music, 2
   *     represents netease cloud music, 3 represents bilibili.
   * @return All libraries for specific platform, wrapped in Result DTO.
   */
  Result getLibraries(
      Long id, Integer pn, Integer ps, String biliPlatform, Integer type, Integer platform);

  /**
   * Get detail library with {@code libraryId} in {@code platform}.
   *
   * @param libraryId The library id.
   * @param pn The page number, only used in bilibili.
   * @param ps The page size, only used in bilibili.
   * @param keyword The keyword to search, only used in bilibili.
   * @param order The sorting order of resources of this fav list, mtime: by collected time, view:
   *     by view time, pubtime: by published time, default is mtime.
   * @param range The range of searching, 0: current fav list, 1: all fav lists, default is 0.
   * @param type 0 for created fav list, 1 for collected fav list.
   * @param platform Which platform the library belongs to.
   * @return Detail library wrapped by Result DTO.
   */
  Result getDetailLibrary(
      String libraryId,
      Integer pn,
      Integer ps,
      String keyword,
      String order,
      Integer range,
      Integer type,
      Integer platform);

  /**
   * Create new library.
   *
   * @param library A map that contains the name of library, {"name"(required): name, "intro":intro,
   *     "privacy": privacy, "cover":cover}
   * @param platform Which platform the library belongs to.
   * @return The response of request wrapped by Result DTO.
   */
  Result createLibrary(Map<String, String> library, Integer platform);

  /**
   * Update library.
   *
   * @param library UpdateLibraryDTO that contains the name of library, {"name"(required):name,
   *     "intro":intro, "cover":cover} (PMSDetailLibrary)
   * @param platform Which platform this library belongs to.
   * @return The response of request wrapped by Result DTO.
   */
  Result updateLibrary(UpdateLibraryDTO library, Integer platform);

  /**
   * Delete the library.
   *
   * @param libraryId The id of library, multiple libraries separated with comma.
   * @param platform Which platform the library belongs to.
   * @return The response of request wrapped by Result DTO.
   */
  Result deleteLibrary(String libraryId, Integer platform);

  /**
   * Add songs {@code songsId} to library {@code libraryId} in platform {@code platform}. /** Add
   * songs {@code songsId} to library {@code libraryId} in platform {@code platform}.
   *
   * @param libraryId Target library id.
   * @param biliSourceFavListId The source media id of fav list, only used in bilibili.
   * @param songsIds Songs' id, multiple songs id separated with comma.
   * @param songs The songs list to be added to library in pms.
   * @param resources The bilibili resources list to be added to library in pms.
   * @param isAddToPMSLibrary Whether adding songs to pms library.
   * @param isFavoriteSearchedResource Whether favorite searched resource.
   * @param platform Which platform the library belongs to.
   * @return The response of request wrapped by Result DTO.
   */
  Result addSongsToLibrary(
      String libraryId,
      String biliSourceFavListId,
      String songsIds,
      List<BasicSong> songs,
      List<BiliResource> resources,
      Boolean isAddToPMSLibrary,
      Boolean isFavoriteSearchedResource,
      Integer platform);

  /**
   * Move songs {@code songsId} from source library with {@code fromLibrary} to target library with
   * {@code toLibrary}.
   *
   * @param songsId Songs id to be moved, multiple songs id separated with comma.
   * @param fromLibrary Source library's id.
   * @param toLibrary Target library's id.
   * @param platform Which platform these libraries belongs to.
   * @return The response of request wrapped by Result DTO.
   */
  Result moveSongsToOtherLibrary(
      String songsId, String fromLibrary, String toLibrary, Integer platform);

  /**
   * Remove songs {@code songsId} from library {@code libraryId}.
   *
   * @param libraryId Library's id.
   * @param songsId The songs' id, multiple songs id separated with comma.
   * @return The response of request wrapped by Result DTO.
   */
  Result removeSongsFromLibrary(String libraryId, String songsId, Integer platform);
}
