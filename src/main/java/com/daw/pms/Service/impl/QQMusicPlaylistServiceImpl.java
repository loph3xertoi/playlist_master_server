package com.daw.pms.Service.impl;

import com.daw.pms.Config.QQMusicAPI;
import com.daw.pms.Entity.QQMusicPlaylist;
import com.daw.pms.Service.QQMusicPlaylistService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Service for deal with qq music playlists.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 6/1/23
 */
@Service
public class QQMusicPlaylistServiceImpl extends QQMusicBase implements QQMusicPlaylistService {
  /**
   * Get all playlist of user {@code id}.
   *
   * @param id User's qq number.
   * @param cookie Your cookie for qq music.
   * @return All playlist created by {@code id}.
   * @apiNote GET /user/playlist?id={@code id}
   */
  @Override
  public List<QQMusicPlaylist> getPlaylist(String id, String cookie) {
    return extractQQMusicPlaylist(
        requestGetAPI(
            QQMusicAPI.GET_PLAYLIST,
            new HashMap<String, String>() {
              {
                put("id", id);
              }
            },
            Optional.of(cookie)));
  }

  /**
   * Create playlist.
   *
   * @param name The name of playlist.
   * @param cookie Your cookie for qq music.
   * @return Result for creating playlist.
   * @apiNote GET /playlist/create?name={@code name}
   */
  @Override
  public String createPlaylist(String name, String cookie) {
    return requestGetAPI(
        QQMusicAPI.CREATE_PLAYLIST,
        new HashMap<String, String>() {
          {
            put("name", name);
          }
        },
        Optional.of(cookie));
  }

  /**
   * Delete playlist with dirId {@code dirId}.
   *
   * @param dirId The dirId of playlist you want to delete.
   * @param cookie Your cookie for qq music.
   * @return Result for deleting playlist.
   * @apiNote /GET /playlist/delete?dirid={@code dirId}
   */
  @Override
  public String deletePlaylist(Integer dirId, String cookie) {
    return requestGetAPI(
        QQMusicAPI.DELETE_PLAYLIST,
        new HashMap<String, Integer>() {
          {
            put("dirid", dirId);
          }
        },
        Optional.of(cookie));
  }

  /**
   * Extract raw playlists to a list of QQMusicPlaylist.
   *
   * @param rawAllPlaylist Raw json string returned by remote QQMusicAPI server.
   * @return ArrayList of QQMusicPlaylist.
   */
  private List<QQMusicPlaylist> extractQQMusicPlaylist(String rawAllPlaylist) {
    JsonNode jsonNode;
    try {
      jsonNode = new ObjectMapper().readTree(rawAllPlaylist);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    List<QQMusicPlaylist> playlists = new ArrayList<>();
    int playlistCount = jsonNode.get("data").get("creator").get("total").intValue();
    JsonNode listNode = jsonNode.get("data").get("list");
    for (int i = 0; i < playlistCount; i++) {
      QQMusicPlaylist playlist = new QQMusicPlaylist();
      JsonNode playlistNode = listNode.get(i);
      playlist.setName(playlistNode.get("diss_name").textValue());
      playlist.setCoverImage(playlistNode.get("diss_cover").textValue());
      playlist.setSongCount(playlistNode.get("song_cnt").intValue());
      playlist.setListenNum(playlistNode.get("listen_num").intValue());
      playlist.setDirId(playlistNode.get("dirid").intValue());
      playlist.setTid(playlistNode.get("tid").asText());
      playlists.add(playlist);
    }
    return playlists;
  }
}
