package com.daw.pms.Service.PMS.impl;

import com.daw.pms.Entity.Basic.BasicLibrary;
import com.daw.pms.Entity.QQMusic.QQMusicDetailPlaylist;
import com.daw.pms.Service.PMS.LibraryService;
import com.daw.pms.Service.QQMusic.QQMusicCookieService;
import com.daw.pms.Service.QQMusic.QQMusicPlaylistService;
import com.daw.pms.Service.QQMusic.QQMusicSongService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class LibraryServiceImpl implements LibraryService, Serializable {
  private final QQMusicPlaylistService qqMusicPlaylistService;
  private final QQMusicCookieService qqMusicCookieService;
  private final QQMusicSongService qqMusicSongService;

  public LibraryServiceImpl(
      QQMusicPlaylistService qqMusicPlaylistService,
      QQMusicCookieService qqMusicCookieService,
      QQMusicSongService qqMusicSongService) {
    this.qqMusicPlaylistService = qqMusicPlaylistService;
    this.qqMusicCookieService = qqMusicCookieService;
    this.qqMusicSongService = qqMusicSongService;
  }

  /**
   * Get all libraries for specific platform.
   *
   * @param id Your user id in pms.
   * @param platform Which platform the user belongs to. 0 represents pms, 1 represents qq music, 2
   *     represents netease music, 3 represents bilibili.
   * @return All libraries for specific platform.
   */
  @Override
  public List<BasicLibrary> getLibraries(String id, Integer platform) {
    List<BasicLibrary> libraries = null;
    if (platform == 1 && "0".equals(id)) {
      libraries =
          qqMusicPlaylistService.getPlaylists("2804161589", qqMusicCookieService.getCookie(1));
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
    BasicLibrary detailLibrary = null;
    if (platform == 1) {
      String cookie = qqMusicCookieService.getCookie(1);
      // The library return by qqMusicPlaylistService is not complete, its songs field need to
      // complete.
      detailLibrary = qqMusicPlaylistService.getDetailPlaylist(libraryId, cookie);
      QQMusicDetailPlaylist qqMusicDetailPlaylist = (QQMusicDetailPlaylist) detailLibrary;
      if (qqMusicDetailPlaylist.getTid() == null) {
        return null;
      }
      Integer songCount = qqMusicDetailPlaylist.getItemCount();
      if (songCount > 0) {
        // Get all songs' mid for get the song's link in one http request.
        List<String> songMids = new ArrayList<>(qqMusicDetailPlaylist.getItemCount());
        qqMusicDetailPlaylist.getSongs().forEach(song -> songMids.add(song.getSongMid()));
        Collections.shuffle(songMids);
        Map<String, String> songsLink =
            qqMusicSongService.getSongsLink(String.join(",", songMids), cookie);
        qqMusicDetailPlaylist
            .getSongs()
            .forEach(
                song -> {
                  song.setSongLink(songsLink.getOrDefault(song.getSongMid(), ""));
                  song.setIsTakenDown(song.getSongLink().isEmpty());
                });
      }
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
  public Map<String, Object> createLibrary(Map<String, String> library, Integer platform) {
    Map<String, Object> result;
    ObjectMapper objectMapper = new ObjectMapper();
    TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {};
    if (platform == 1) {
      String cookie = qqMusicCookieService.getCookie(1);
      String jsonString = qqMusicPlaylistService.createPlaylist(library.get("name"), cookie);
      try {
        result = objectMapper.readValue(jsonString, typeRef);
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    } else {
      throw new RuntimeException("Only implement qq music platform");
    }
    return result;
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
    ObjectMapper objectMapper = new ObjectMapper();
    TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {};
    if (platform == 1) {
      String cookie = qqMusicCookieService.getCookie(1);
      String jsonString = qqMusicPlaylistService.deletePlaylist(libraryId, cookie);
      try {
        result = objectMapper.readValue(jsonString, typeRef);
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    } else {
      throw new RuntimeException("Only implement qq music platform");
    }
    return result;
  }

  @Override
  public Map<String, Object> addSongsToLibrary(Integer dirId, String songsMid, Integer platform) {
    Map<String, Object> result;
    ObjectMapper objectMapper = new ObjectMapper();
    TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {};
    if (platform == 1) {
      String cookie = qqMusicCookieService.getCookie(1);
      String jsonString = qqMusicPlaylistService.addSongsToPlaylist(dirId, songsMid, cookie);
      try {
        result = objectMapper.readValue(jsonString, typeRef);
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    } else {
      throw new RuntimeException("Only implement qq music platform");
    }
    return result;
  }

  @Override
  public Map<String, Object> moveSongsToOtherLibrary(
      String songsId, Integer fromDirId, Integer toDirId, Integer platform) {
    Map<String, Object> result;
    ObjectMapper objectMapper = new ObjectMapper();
    TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {};
    if (platform == 1) {
      String cookie = qqMusicCookieService.getCookie(1);
      String jsonString =
          qqMusicPlaylistService.moveSongsToOtherPlaylist(songsId, fromDirId, toDirId, cookie);
      try {
        result = objectMapper.readValue(jsonString, typeRef);
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    } else {
      throw new RuntimeException("Only implement qq music platform");
    }
    return result;
  }

  @Override
  public Map<String, Object> removeSongsFromLibrary(
      Integer dirId, String songsId, Integer platform) {
    Map<String, Object> result;
    ObjectMapper objectMapper = new ObjectMapper();
    TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {};
    if (platform == 1) {
      String cookie = qqMusicCookieService.getCookie(1);
      String jsonString = qqMusicPlaylistService.removeSongsFromPlaylist(dirId, songsId, cookie);
      try {
        result = objectMapper.readValue(jsonString, typeRef);
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    } else {
      throw new RuntimeException("Only implement qq music platform");
    }
    return result;
  }
}
