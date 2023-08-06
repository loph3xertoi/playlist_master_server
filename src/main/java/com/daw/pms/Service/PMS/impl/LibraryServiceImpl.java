package com.daw.pms.Service.PMS.impl;

import com.daw.pms.DTO.Result;
import com.daw.pms.Service.Bilibili.BiliFavListService;
import com.daw.pms.Service.NeteaseCloudMusic.NCMPlaylistService;
import com.daw.pms.Service.PMS.LibraryService;
import com.daw.pms.Service.QQMusic.QQMusicPlaylistService;
import java.io.Serializable;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LibraryServiceImpl implements LibraryService, Serializable {
  @Value("${qqmusic.id}")
  private Long qqMusicId;

  @Value("${qqmusic.cookie}")
  private String qqMusicCookie;

  @Value("${ncm.id}")
  private Long ncmId;

  @Value("${ncm.cookie}")
  private String ncmCookie;

  @Value("${bilibili.id}")
  private Long biliId;

  @Value("${bilibili.cookie}")
  private String biliCookie;

  private final QQMusicPlaylistService qqMusicPlaylistService;
  private final NCMPlaylistService ncmPlaylistService;
  private final BiliFavListService biliFavListService;

  public LibraryServiceImpl(
      QQMusicPlaylistService qqMusicPlaylistService,
      NCMPlaylistService ncmPlaylistService,
      BiliFavListService biliFavListService) {
    this.qqMusicPlaylistService = qqMusicPlaylistService;
    this.ncmPlaylistService = ncmPlaylistService;
    this.biliFavListService = biliFavListService;
  }

  /**
   * Get all libraries for specific platform.
   *
   * @param id Your user id in pms.
   * @param pn The page number, only used in bilibili.
   * @param ps The page size, only used in bilibili.
   * @param biliPlatform The platform of bilibili, default is web, only used in bilibili.
   * @param type The fav lists type of bilibili, 0 means get created fav lists, 1 means get
   *     collected fav lists, only used in bilibili.
   * @param platform Which platform the user belongs to. 0 represents pms, 1 represents qq music, 2
   *     represents netease cloud music, 3 represents bilibili.
   * @return All libraries for specific platform, wrapped in Result DTO.
   */
  @Override
  public Result getLibraries(
      Long id, Integer pn, Integer ps, String biliPlatform, Integer type, Integer platform) {
    // TODO: find user id by pms id in specific platform.
    Result result;
    if (platform == 0) {
      throw new RuntimeException("Not yet implement pms platform.");
    } else if (platform == 1) {
      result = qqMusicPlaylistService.getPlaylists(qqMusicId.toString(), qqMusicCookie);
    } else if (platform == 2) {
      result = ncmPlaylistService.getPlaylists(ncmId, 0, 1000, ncmCookie);
    } else if (platform == 3) {
      result = biliFavListService.getFavLists(pn, ps, biliId, biliPlatform, type, biliCookie);
    } else {
      throw new RuntimeException("Invalid platform.");
    }
    return result;
  }

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
  @Override
  public Result getDetailLibrary(
      String libraryId,
      Integer pn,
      Integer ps,
      String keyword,
      String order,
      Integer range,
      Integer type,
      Integer platform) {
    Result result;
    if (platform == 0) {
      throw new RuntimeException("Not yet implement pms platform.");
    } else if (platform == 1) {
      result = qqMusicPlaylistService.getDetailPlaylist(libraryId, qqMusicCookie);
    } else if (platform == 2) {
      result = ncmPlaylistService.getDetailPlaylist(Long.valueOf(libraryId), ncmCookie);
    } else if (platform == 3) {
      result =
          biliFavListService.getDetailFavList(
              Long.valueOf(libraryId), pn, ps, keyword, order, range, type, biliCookie);
    } else {
      throw new RuntimeException("Invalid platform.");
    }
    return result;
  }

  /**
   * Create new library.
   *
   * @param library A map that contains the name of library, {"name"(required): name, "intro":intro,
   *     "privacy": privacy, "cover":cover}
   * @param platform Which platform the library belongs to.
   * @return The response of request wrapped by Result DTO.
   */
  @Override
  public Result createLibrary(Map<String, String> library, Integer platform) {
    Result result;
    if (platform == 0) {
      throw new RuntimeException("Not yet implement pms platform.");
    } else if (platform == 1) {
      result = qqMusicPlaylistService.createPlaylist(library.get("name"), qqMusicCookie);
    } else if (platform == 2) {
      result = ncmPlaylistService.createPlaylist(library.get("name"), ncmCookie);
    } else if (platform == 3) {
      result = biliFavListService.createFavList(library, biliCookie);
    } else {
      throw new RuntimeException("Invalid platform.");
    }
    return result;
  }

  /**
   * Delete the library.
   *
   * @param libraryId The id of library, multiple libraries separated with comma.
   * @param platform Which platform the library belongs to.
   * @return The response of request wrapped by Result DTO.
   */
  @Override
  public Result deleteLibrary(String libraryId, Integer platform) {
    Result result;
    if (platform == 0) {
      throw new RuntimeException("Not yet implement pms platform.");
    } else if (platform == 1) {
      result = qqMusicPlaylistService.deletePlaylist(libraryId, qqMusicCookie);
    } else if (platform == 2) {
      result = ncmPlaylistService.deletePlaylist(libraryId, ncmCookie);
    } else if (platform == 3) {
      result = biliFavListService.deleteFavList(libraryId, biliCookie);
    } else {
      throw new RuntimeException("Invalid platform.");
    }
    return result;
  }

  /**
   * Add songs {@code songsId} to library {@code libraryId} in platform {@code platform}.
   *
   * @param libraryId Target library id.
   * @param biliSourceFavListId The source media id of fav list, only used in bilibili.
   * @param songsId Songs' id, multiple songs id separated with comma.
   * @param platform Which platform the library belongs to.
   * @return The response of request wrapped by Result DTO.
   */
  @Override
  public Result addSongsToLibrary(
      String libraryId, String biliSourceFavListId, String songsId, Integer platform) {
    Result result;
    if (platform == 0) {
      throw new RuntimeException("Not yet implement pms platform.");
    } else if (platform == 1) {
      result =
          qqMusicPlaylistService.addSongsToPlaylist(
              Integer.valueOf(libraryId), songsId, qqMusicCookie);
    } else if (platform == 2) {
      result = ncmPlaylistService.addSongsToPlaylist(Long.valueOf(libraryId), songsId, ncmCookie);
    } else if (platform == 3) {
      if (biliSourceFavListId == null) {
        return Result.fail("biliSourceFavListId is null");
      }
      result =
          biliFavListService.multipleAddResources(
              Long.valueOf(biliSourceFavListId),
              Long.valueOf(libraryId),
              biliId,
              songsId,
              "web",
              biliCookie);
    } else {
      throw new RuntimeException("Invalid platform.");
    }
    return result;
  }

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
  @Override
  public Result moveSongsToOtherLibrary(
      String songsId, String fromLibrary, String toLibrary, Integer platform) {
    Result result;
    if (platform == 0) {
      throw new RuntimeException("Not yet implement pms platform.");
    } else if (platform == 1) {
      result =
          qqMusicPlaylistService.moveSongsToOtherPlaylist(
              songsId, Integer.valueOf(fromLibrary), Integer.valueOf(toLibrary), qqMusicCookie);
    } else if (platform == 2) {
      result =
          ncmPlaylistService.moveSongsToOtherPlaylist(
              songsId, Long.valueOf(fromLibrary), Long.valueOf(toLibrary), ncmCookie);
    } else if (platform == 3) {
      result =
          biliFavListService.multipleMoveResources(
              Long.valueOf(fromLibrary),
              Long.valueOf(toLibrary),
              biliId,
              songsId,
              "web",
              biliCookie);
    } else {
      throw new RuntimeException("Invalid platform.");
    }
    return result;
  }

  /**
   * Remove songs {@code songsId} from library {@code libraryId}.
   *
   * @param libraryId Library's id.
   * @param songsId The songs' id, multiple songs id separated with comma.
   * @return The response of request wrapped by Result DTO.
   */
  @Override
  public Result removeSongsFromLibrary(String libraryId, String songsId, Integer platform) {
    Result result;
    if (platform == 0) {
      throw new RuntimeException("Not yet implement pms platform.");
    } else if (platform == 1) {
      result =
          qqMusicPlaylistService.removeSongsFromPlaylist(
              Integer.valueOf(libraryId), songsId, qqMusicCookie);
    } else if (platform == 2) {
      result =
          ncmPlaylistService.removeSongsFromPlaylist(Long.valueOf(libraryId), songsId, ncmCookie);
    } else if (platform == 3) {
      result =
          biliFavListService.multipleDeleteResources(
              songsId, Long.valueOf(libraryId), "web", biliCookie);
    } else {
      throw new RuntimeException("Invalid platform.");
    }
    return result;
  }
}
