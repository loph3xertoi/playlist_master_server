package com.daw.pms.Service.PMS;

import com.daw.pms.Entity.Basic.BasicLibrary;
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
   * @param platform Which platform the user belongs to. 0 represents pms, 1 represents qq music, 2
   *     represents netease music, 3 represents bilibili.
   * @return All libraries for specific platform.
   */
  List<BasicLibrary> getLibraries(String id, Integer platform);

  /**
   * Get detail library with {@code libraryId} in {@code platform}.
   *
   * @param libraryId The library id.
   * @param platform Which platform the library belongs to.
   * @return Detail library.
   */
  BasicLibrary getDetailLibrary(String libraryId, Integer platform);

  /**
   * Create new library.
   *
   * @param library A map that contains the name of library.
   * @param platform Which platform the library belongs to.
   * @return Map result for creating library, need to be parsed.
   */
  Map<String, Object> createLibrary(Map<String, String> library, Integer platform);

  /**
   * Delete the library.
   *
   * @param libraryId The id of library, multiple libraries separated with comma.
   * @param platform Which platform the library belongs to.
   * @return Map result for deleting library, need to be parsed.
   */
  Map<String, Object> deleteLibrary(String libraryId, Integer platform);

  /**
   * Add songs {@code songsMid} to library {@code dirId} in platform {@code platform}.
   *
   * @param dirId The dirId of the library.
   * @param songsMid The mid of songs, multiple mid separated with comma.
   * @param platform Which platform the library belongs to.
   * @return 100 for success, 200 for failure.
   */
  Map<String, Object> addSongsToLibrary(Integer dirId, String songsMid, Integer platform);

  /**
   * Move songs {@code songsId} from library with {@code fromDirId} to library with {@code toDirId}.
   *
   * @param songsId Songs id to be moved, multiple songs id separated with comma.
   * @param fromDirId DirId of source library.
   * @param toDirId DirId of target library.
   * @param platform Which platform these libraries belongs to.
   * @return 100 for success, 200 for failure.
   */
  Map<String, Object> moveSongsToOtherLibrary(
      String songsId, Integer fromDirId, Integer toDirId, Integer platform);

  /**
   * Remove songs with song id {@code songsId} from library with dirId {@code dirId}.
   *
   * @param dirId The dirId of the target library.
   * @param songsId The songs' id, multiple songs id separated with comma.
   * @return 100 for success.
   */
  Map<String, Object> removeSongsFromLibrary(Integer dirId, String songsId, Integer platform);
}
