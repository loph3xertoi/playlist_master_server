package com.daw.pms.Service.PMS;

import com.daw.pms.Entity.Basic.BasicLibrary;
import java.util.List;

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
   * @param platform Which platform the user belongs to.
   * @return Detail library.
   */
  BasicLibrary getDetailLibrary(String libraryId, Integer platform);
  //  Integer createLibrary()
}
