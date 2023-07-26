package com.daw.pms.Service.PMS;

import com.daw.pms.DTO.Result;
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
   *     represents netease cloud music, 3 represents bilibili.
   * @return All libraries for specific platform.
   */
  List<BasicLibrary> getLibraries(Long id, Integer platform);

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
   * @return The response of request wrapped by Result DTO.
   */
  Result createLibrary(Map<String, String> library, Integer platform);

  /**
   * Delete the library.
   *
   * @param libraryId The id of library, multiple libraries separated with comma.
   * @param platform Which platform the library belongs to.
   * @return The response of request wrapped by Result DTO.
   */
  Result deleteLibrary(String libraryId, Integer platform);

  /**
   * Add songs {@code songsId} to library {@code libraryId} in platform {@code platform}.
   *
   * @param libraryId Library's id.
   * @param songsId Songs' id, multiple songs id separated with comma.
   * @param platform Which platform the library belongs to.
   * @return The response of request wrapped by Result DTO.
   */
  Result addSongsToLibrary(String libraryId, String songsId, Integer platform);

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
