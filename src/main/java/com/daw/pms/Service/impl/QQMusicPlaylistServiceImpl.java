package com.daw.pms.Service.impl;

import com.daw.pms.Config.QQMusicAPI;
import com.daw.pms.Entity.QQMusicBasicSong;
import com.daw.pms.Entity.QQMusicDetailPlaylist;
import com.daw.pms.Entity.QQMusicPlaylist;
import com.daw.pms.Entity.QQMusicSinger;
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
   * Get all playlists of user {@code id}.
   *
   * @param id User's qq number.
   * @param cookie Your cookie for qq music.
   * @return All playlists created by {@code id}.
   * @apiNote GET /user/playlist?id={@code id}
   */
  @Override
  public List<QQMusicPlaylist> getPlaylists(String id, String cookie) {
    return extractQQMusicPlaylists(
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
   * Get detail playlist with {@code tid}.
   *
   * @param tid The playlist's global tid.
   * @param cookie Your qq music cookie.
   * @return Detail playlist.
   * @apiNote GET /playlist?id={@code tid}
   */
  @Override
  public QQMusicDetailPlaylist getDetailPlaylist(String tid, String cookie) {
    return extractDetailPlaylist(
        requestGetAPI(
            QQMusicAPI.GET_DETAIL_PLAYLIST,
            new HashMap<String, String>() {
              {
                put("id", tid);
              }
            },
            Optional.of(cookie)));
  }

  /**
   * Extract detail playlist from raw detail playlist.
   *
   * @param rawDetailPlaylist Raw detail playlist returned by proxy qq music api server.
   * @return Detail playlist.
   */
  private QQMusicDetailPlaylist extractDetailPlaylist(String rawDetailPlaylist) {
    JsonNode jsonNode;
    QQMusicDetailPlaylist detailPlaylist = new QQMusicDetailPlaylist();
    try {
      jsonNode = new ObjectMapper().readTree(rawDetailPlaylist);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

    JsonNode dataNode = jsonNode.get("data");
    if (dataNode.isObject() && dataNode.size() == 0) {
      return detailPlaylist;
    }
    detailPlaylist.setName(dataNode.get("dissname").textValue());
    detailPlaylist.setDesc(dataNode.get("desc").textValue());
    detailPlaylist.setCoverImage(dataNode.get("logo").textValue());
    detailPlaylist.setSongCount(dataNode.get("songnum").intValue());
    detailPlaylist.setListenNum(dataNode.get("visitnum").intValue());
    detailPlaylist.setDirId(dataNode.get("dirid").intValue());
    detailPlaylist.setTid(dataNode.get("disstid").textValue());

    List<QQMusicBasicSong> songs = new ArrayList<>();
    JsonNode songsNode = jsonNode.get("data").get("songlist");
    for (JsonNode song : songsNode) {
      JsonNode singersNode = song.get("singer");
      ArrayList<QQMusicSinger> singers = new ArrayList<>();
      QQMusicBasicSong basicSong = new QQMusicBasicSong();
      basicSong.setSongName(song.get("songname").textValue());
      basicSong.setSongId(song.get("songid").asText());
      basicSong.setSongMid(song.get("songmid").textValue());
      basicSong.setMediaMid(song.get("strMediaMid").textValue());
      basicSong.setVid(song.get("vid").textValue());

      for (JsonNode singerNode : singersNode) {
        QQMusicSinger singer = new QQMusicSinger();
        singer.setName(singerNode.get("name").textValue());
        singer.setId(singerNode.get("id").asText());
        singer.setMid(singerNode.get("mid").textValue());
        singers.add(singer);
      }
      basicSong.setSingers(singers);

      String albumMid = song.get("albummid").textValue();
      String songCoverUri;
      if (!albumMid.isEmpty()) {
        songCoverUri = "https://y.qq.com/music/photo_new/T002R300x300M000" + albumMid + "_2.jpg";
      } else {
        String singerMid = singers.get(0).getMid();
        songCoverUri = "https://y.qq.com/music/photo_new/T001R300x300M000" + singerMid + "_3.jpg";
      }
      basicSong.setCoverUri(songCoverUri);

      basicSong.setPayPlay(song.get("pay").get("payplay").intValue());
      songs.add(basicSong);
      detailPlaylist.setSongs(songs);
    }
    return detailPlaylist;
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
   * @param rawAllPlaylists Raw json string returned by remote QQMusicAPI server.
   * @return ArrayList of QQMusicPlaylist.
   */
  private List<QQMusicPlaylist> extractQQMusicPlaylists(String rawAllPlaylists) {
    JsonNode jsonNode;
    try {
      jsonNode = new ObjectMapper().readTree(rawAllPlaylists);
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
      playlist.setDirId(playlistNode.get("dirid").intValue());
      playlist.setTid(playlistNode.get("tid").asText());
      playlists.add(playlist);
    }
    return playlists;
  }
}
