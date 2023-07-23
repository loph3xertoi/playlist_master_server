package com.daw.pms.Service.PMS.impl;

import com.daw.pms.Entity.Basic.BasicLibrary;
import com.daw.pms.Entity.NeteaseCloudMusic.NCMPlaylist;
import com.daw.pms.Entity.QQMusic.QQMusicPlaylist;
import com.daw.pms.Service.NeteaseCloudMusic.NCMPlaylistService;
import com.daw.pms.Service.PMS.LibraryService;
import com.daw.pms.Service.QQMusic.QQMusicPlaylistService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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

  private final QQMusicPlaylistService qqMusicPlaylistService;
  private final NCMPlaylistService ncmPlaylistService;

  public LibraryServiceImpl(
      QQMusicPlaylistService qqMusicPlaylistService, NCMPlaylistService ncmPlaylistService) {
    this.qqMusicPlaylistService = qqMusicPlaylistService;
    this.ncmPlaylistService = ncmPlaylistService;
  }

  /**
   * Get all libraries for specific platform.
   *
   * @param id Your user id in pms.
   * @param platform Which platform the user belongs to. 0 represents pms, 1 represents qq music, 2
   *     represents netease cloud music, 3 represents bilibili.
   * @return All libraries for specific platform.
   */
  @Override
  public List<BasicLibrary> getLibraries(Long id, Integer platform) {
    List<BasicLibrary> libraries = new ArrayList<>();
    if (platform == 0) {
      throw new RuntimeException("Not yet implement pms platform.");
    } else if (platform == 1) {
      List<QQMusicPlaylist> playlists =
          qqMusicPlaylistService.getPlaylists(qqMusicId.toString(), qqMusicCookie);
      libraries.addAll(playlists);
    } else if (platform == 2) {
      List<NCMPlaylist> playlists = ncmPlaylistService.getPlaylists(ncmId, 0, 1000, ncmCookie);
      libraries.addAll(playlists);
    } else if (platform == 3) {
      throw new RuntimeException("Not yet implement bilibili platform.");
    } else {
      throw new RuntimeException("Invalid platform.");
    }
    return libraries;
  }

  /**
   * Get detail library with {@code libraryId} in {@code platform}.
   *
   * @param libraryId The library id.
   * @param platform Which platform the library belongs to.
   * @return Detail library.
   */
  @Override
  public BasicLibrary getDetailLibrary(String libraryId, Integer platform) {
    BasicLibrary detailLibrary;
    if (platform == 0) {
      throw new RuntimeException("Not yet implement pms platform.");
    } else if (platform == 1) {
      detailLibrary = qqMusicPlaylistService.getDetailPlaylist(libraryId, qqMusicCookie);
    } else if (platform == 2) {
      detailLibrary = ncmPlaylistService.getDetailPlaylist(Long.valueOf(libraryId), ncmCookie);
    } else if (platform == 3) {
      throw new RuntimeException("Not yet implement bilibili platform.");
    } else {
      throw new RuntimeException("Invalid platform.");
    }
    return detailLibrary;
  }

  /**
   * Create new library.
   *
   * @param library A map that contains the name of library.
   * @param platform Which platform the library belongs to.
   * @return Map result for creating library, need to be parsed.
   */
  @Override
  public Long createLibrary(Map<String, String> library, Integer platform) {
    Long libraryId;
    if (platform == 0) {
      throw new RuntimeException("Not yet implement pms platform.");
    } else if (platform == 1) {
      libraryId = qqMusicPlaylistService.createPlaylist(library.get("name"), qqMusicCookie);
    } else if (platform == 2) {
      libraryId = ncmPlaylistService.createPlaylist(library.get("name"), ncmCookie);
    } else if (platform == 3) {
      throw new RuntimeException("Not yet implement bilibili platform.");
    } else {
      throw new RuntimeException("Invalid platform.");
    }
    return libraryId;
  }

  /**
   * Delete the library.
   *
   * @param libraryId The id of library, multiple libraries separated with comma.
   * @param platform Which platform the library belongs to.
   * @return Map result for deleting library, need to be parsed.
   */
  @Override
  public Map<String, Object> deleteLibrary(String libraryId, Integer platform) {
    Map<String, Object> result;
    String resultJson;
    TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {};
    ObjectMapper objectMapper = new ObjectMapper();
    if (platform == 0) {
      throw new RuntimeException("Not yet implement pms platform.");
    } else if (platform == 1) {
      resultJson = qqMusicPlaylistService.deletePlaylist(libraryId, qqMusicCookie);
    } else if (platform == 2) {
      resultJson = ncmPlaylistService.deletePlaylist(libraryId, ncmCookie);
    } else if (platform == 3) {
      throw new RuntimeException("Not yet implement bilibili platform.");
    } else {
      throw new RuntimeException("Invalid platform.");
    }
    try {
      result = objectMapper.readValue(resultJson, typeRef);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    return result;
  }

  /**
   * Add songs {@code songsId} to library {@code libraryId} in platform {@code platform}.
   *
   * @param libraryId Library's id.
   * @param songsId Songs' id, multiple songs id separated with comma.
   * @param platform Which platform the library belongs to.
   * @return 100 for success, 200 for failure.
   */
  @Override
  public Map<String, Object> addSongsToLibrary(String libraryId, String songsId, Integer platform) {
    Map<String, Object> result;
    String resultJson;
    TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {};
    ObjectMapper objectMapper = new ObjectMapper();
    if (platform == 0) {
      throw new RuntimeException("Not yet implement pms platform.");
    } else if (platform == 1) {
      resultJson =
          qqMusicPlaylistService.addSongsToPlaylist(
              Integer.valueOf(libraryId), songsId, qqMusicCookie);
    } else if (platform == 2) {
      resultJson =
          ncmPlaylistService.addSongsToPlaylist(Long.valueOf(libraryId), songsId, ncmCookie);
    } else if (platform == 3) {
      throw new RuntimeException("Not yet implement bilibili platform.");
    } else {
      throw new RuntimeException("Invalid platform.");
    }
    try {
      result = objectMapper.readValue(resultJson, typeRef);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
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
   * @return 100 for success, 200 for failure.
   */
  @Override
  public Map<String, Object> moveSongsToOtherLibrary(
      String songsId, String fromLibrary, String toLibrary, Integer platform) {
    Map<String, Object> result;
    String resultJson;
    TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {};
    ObjectMapper objectMapper = new ObjectMapper();
    if (platform == 0) {
      throw new RuntimeException("Not yet implement pms platform.");
    } else if (platform == 1) {
      resultJson =
          qqMusicPlaylistService.moveSongsToOtherPlaylist(
              songsId, Integer.valueOf(fromLibrary), Integer.valueOf(toLibrary), qqMusicCookie);
    } else if (platform == 2) {
      resultJson =
          ncmPlaylistService.moveSongsToOtherPlaylist(
              songsId, Long.valueOf(fromLibrary), Long.valueOf(toLibrary), ncmCookie);
    } else if (platform == 3) {
      throw new RuntimeException("Not yet implement bilibili platform.");
    } else {
      throw new RuntimeException("Invalid platform.");
    }
    try {
      result = objectMapper.readValue(resultJson, typeRef);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    return result;
  }

  /**
   * Remove songs {@code songsId} from library {@code libraryId}.
   *
   * @param libraryId Library's id.
   * @param songsId The songs' id, multiple songs id separated with comma.
   * @return 100 for success.
   */
  @Override
  public Map<String, Object> removeSongsFromLibrary(
      String libraryId, String songsId, Integer platform) {
    Map<String, Object> result;
    String resultJson;
    TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {};
    ObjectMapper objectMapper = new ObjectMapper();
    if (platform == 0) {
      throw new RuntimeException("Not yet implement pms platform.");
    } else if (platform == 1) {
      resultJson =
          qqMusicPlaylistService.removeSongsFromPlaylist(
              Integer.valueOf(libraryId), songsId, qqMusicCookie);
    } else if (platform == 2) {
      resultJson =
          ncmPlaylistService.removeSongsFromPlaylist(Long.valueOf(libraryId), songsId, ncmCookie);
    } else if (platform == 3) {
      throw new RuntimeException("Not yet implement bilibili platform.");
    } else {
      throw new RuntimeException("Invalid platform.");
    }
    try {
      result = objectMapper.readValue(resultJson, typeRef);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    return result;
  }
}
