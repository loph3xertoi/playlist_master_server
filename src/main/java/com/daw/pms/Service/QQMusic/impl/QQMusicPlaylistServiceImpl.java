package com.daw.pms.Service.QQMusic.impl;

import com.daw.pms.Config.QQMusicAPI;
import com.daw.pms.DTO.Result;
import com.daw.pms.Entity.Basic.BasicSinger;
import com.daw.pms.Entity.QQMusic.QQMusicDetailPlaylist;
import com.daw.pms.Entity.QQMusic.QQMusicPlaylist;
import com.daw.pms.Entity.QQMusic.QQMusicSinger;
import com.daw.pms.Entity.QQMusic.QQMusicSong;
import com.daw.pms.Service.QQMusic.QQMusicPlaylistService;
import com.daw.pms.Service.QQMusic.QQMusicSongService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.Serializable;
import java.util.*;
import org.springframework.stereotype.Service;

/**
 * Service for deal with qq music playlists.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 6/1/23
 */
@Service
public class QQMusicPlaylistServiceImpl extends QQMusicBase
    implements QQMusicPlaylistService, Serializable {
  private final QQMusicSongService qqMusicSongService;

  public QQMusicPlaylistServiceImpl(QQMusicSongService qqMusicSongService) {
    this.qqMusicSongService = qqMusicSongService;
  }

  /**
   * Get all playlists of user {@code qid} in qq music platform.
   *
   * @param qid Your qq number.
   * @param cookie Your cookie for qq music.
   * @return All playlists created by {@code qid}.
   * @apiNote GET /user/playlist?id={@code qid}
   */
  @Override
  public List<QQMusicPlaylist> getPlaylists(String qid, String cookie) {
    return extractQQMusicPlaylists(
        requestGetAPI(
            QQMusicAPI.GET_PLAYLIST,
            new HashMap<String, String>() {
              {
                put("id", qid);
              }
            },
            Optional.of(cookie)));
  }

  /**
   * Extract raw playlists to a list of QQMusicPlaylist.
   *
   * @param rawAllPlaylists Raw json string returned by remote QQMusicAPI server.
   * @return A list of QQMusicPlaylist.
   */
  private List<QQMusicPlaylist> extractQQMusicPlaylists(String rawAllPlaylists) {
    List<QQMusicPlaylist> playlists = new ArrayList<>();
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(rawAllPlaylists);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    int playlistCount = jsonNode.get("data").get("creator").get("total").intValue();
    JsonNode listNode = jsonNode.get("data").get("list");
    for (int i = 0; i < playlistCount; i++) {
      QQMusicPlaylist playlist = new QQMusicPlaylist();
      JsonNode playlistNode = listNode.get(i);
      playlist.setName(playlistNode.get("diss_name").textValue());
      playlist.setCover(playlistNode.get("diss_cover").textValue());
      playlist.setItemCount(playlistNode.get("song_cnt").intValue());
      playlist.setDirId(playlistNode.get("dirid").intValue());
      playlist.setTid(playlistNode.get("tid").asText());
      playlists.add(playlist);
    }
    return playlists;
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
    QQMusicDetailPlaylist qqMusicDetailPlaylist =
        extractDetailPlaylist(
            requestGetAPI(
                QQMusicAPI.GET_DETAIL_PLAYLIST,
                new HashMap<String, String>() {
                  {
                    put("id", tid);
                  }
                },
                Optional.of(cookie)));

    if (qqMusicDetailPlaylist == null) {
      return null;
    }
    Integer songCount = qqMusicDetailPlaylist.getItemCount();
    if (songCount > 0) {
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
    return qqMusicDetailPlaylist;
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
      return null;
    }

    detailPlaylist.setName(dataNode.get("dissname").textValue());
    detailPlaylist.setCover(dataNode.get("logo").textValue());
    detailPlaylist.setItemCount(dataNode.get("songnum").intValue());
    detailPlaylist.setDesc(dataNode.get("desc").isNull() ? "" : dataNode.get("desc").textValue());
    detailPlaylist.setListenNum(dataNode.get("visitnum").intValue());
    detailPlaylist.setDirId(dataNode.get("dirid").intValue());
    detailPlaylist.setTid(dataNode.get("disstid").textValue());

    List<QQMusicSong> songs = new ArrayList<>();
    JsonNode songsNode = jsonNode.get("data").get("songlist");
    for (JsonNode song : songsNode) {
      QQMusicSong basicSong = new QQMusicSong();

      basicSong.setName(song.get("songname").textValue());

      JsonNode singersNode = song.get("singer");
      ArrayList<BasicSinger> singers = new ArrayList<>();
      for (JsonNode singerNode : singersNode) {
        QQMusicSinger singer = new QQMusicSinger();
        singer.setName(singerNode.get("name").textValue());
        singer.setId(singerNode.get("id").intValue());
        singer.setMid(singerNode.get("mid").textValue());
        singer.setHeadPic(
            "http://y.gtimg.cn/music/photo_new/T001R150x150M000" + singer.getMid() + "_8.jpg");
        singers.add(singer);
      }
      basicSong.setSingers(singers);

      String albumMid = song.get("albummid").textValue();
      String songCoverUri;
      if (!albumMid.isEmpty()) {
        songCoverUri = "https://y.qq.com/music/photo_new/T002R300x300M000" + albumMid + "_2.jpg";
      } else {
        QQMusicSinger singer = (QQMusicSinger) singers.get(0);
        String singerMid = singer.getMid();
        songCoverUri = "https://y.qq.com/music/photo_new/T001R300x300M000" + singerMid + "_3.jpg";
      }
      basicSong.setCover(songCoverUri);

      basicSong.setPayPlay(song.get("pay").get("payplay").intValue());
      basicSong.setSongId(song.get("songid").asText());
      basicSong.setSongMid(song.get("songmid").textValue());
      basicSong.setMediaMid(song.get("strMediaMid").textValue());

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
   * @return The response of request wrapped by Result DTO.
   * @apiNote GET /playlist/create?name={@code name}
   */
  @Override
  public Result createPlaylist(String name, String cookie) {
    return extractCreatingPlaylistResult(
        requestGetAPI(
            QQMusicAPI.CREATE_PLAYLIST,
            new HashMap<String, String>() {
              {
                put("name", name);
              }
            },
            Optional.of(cookie)));
  }

  Result extractCreatingPlaylistResult(String rawCreatingPlaylistResult) {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(rawCreatingPlaylistResult);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    int resultCode = jsonNode.get("result").intValue();
    if (resultCode == 100) {
      Integer dirId = jsonNode.get("data").get("dirid").intValue();
      return Result.ok(dirId);
    } else if (resultCode == 200) {
      return Result.fail("Library already exists");
    } else if (resultCode == 301) {
      return Result.fail("QQ music proxy server needs to login");
    } else {
      return Result.fail(rawCreatingPlaylistResult);
    }
  }

  /**
   * Delete playlist with dirId {@code dirId}.
   *
   * @param dirId The dirId of playlist you want to delete, multiple dirId separated with comma.
   * @param cookie Your cookie for qq music.
   * @return The response of request wrapped by Result DTO.
   * @apiNote GET /playlist/delete?dirid={@code dirId}
   */
  @Override
  public Result deletePlaylist(String dirId, String cookie) {
    return extractDeletingPlaylistResult(
        requestGetAPI(
            QQMusicAPI.DELETE_PLAYLIST,
            new HashMap<String, String>() {
              {
                put("dirid", dirId);
              }
            },
            Optional.of(cookie)));
  }

  Result extractDeletingPlaylistResult(String rawDeletingPlaylistResult) {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(rawDeletingPlaylistResult);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    int resultCode = jsonNode.get("result").intValue();
    if (resultCode == 100) {
      return Result.ok();
    } else if (resultCode == 200) {
      return Result.fail("Library delete failed: Invalid parameters");
    } else if (resultCode == 301) {
      return Result.fail("QQ music proxy server needs to login");
    } else {
      return Result.fail(rawDeletingPlaylistResult);
    }
  }

  /**
   * Add songs with mids {@code songsMid} to playlist with dirId {@code dirId}
   *
   * @param dirId The dirId of the playlist.
   * @param songsMid The mid of songs, multiple mid separated with comma.
   * @param cookie Your qq music cookie.
   * @return The response of request wrapped by Result DTO.
   * @apiNote GET /playlist/add?dirid={@code dirId}&mid={@code songsMid}
   */
  @Override
  public Result addSongsToPlaylist(Integer dirId, String songsMid, String cookie) {
    return extractAddingSongsToPlaylistResult(
        requestGetAPI(
            QQMusicAPI.ADD_SONGS_TO_PLAYLIST,
            new HashMap<String, String>() {
              {
                put("dirid", dirId.toString());
                put("mid", songsMid);
              }
            },
            Optional.of(cookie)));
  }

  Result extractAddingSongsToPlaylistResult(String rawAddingSongsToPlaylistResult) {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(rawAddingSongsToPlaylistResult);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    int resultCode = jsonNode.get("result").intValue();
    if (resultCode == 100) {
      return Result.ok();
    } else if (resultCode == 200) {
      return Result.fail("Add songs failed: Invalid parameters");
    } else if (resultCode == 301) {
      return Result.fail("QQ music proxy server needs to login");
    } else {
      return Result.fail(rawAddingSongsToPlaylistResult);
    }
  }

  /**
   * Move songs {@code songsId} from playlist with {@code fromDirId} to playlist with {@code
   * toDirId}.
   *
   * @param songsId Songs id to be moved, multiple songs id separated with comma.
   * @param fromDirId DirId of from-playlist.
   * @param toDirId DirId of to-playlist.
   * @param cookie Your qq music cookie.
   * @return The response of request wrapped by Result DTO.
   * @apiNote GET /move?id={@code songsId}&from_dir={@code fromDirId}&to_dir={@code toDirId}
   */
  @Override
  public Result moveSongsToOtherPlaylist(
      String songsId, Integer fromDirId, Integer toDirId, String cookie) {
    return extractMovingSongsToOtherPlaylist(
        requestGetAPI(
            QQMusicAPI.MOVE_SONGS_TO_OTHER_PLAYLIST,
            new HashMap<String, String>() {
              {
                put("id", songsId);
                put("from_dir", fromDirId.toString());
                put("to_dir", toDirId.toString());
              }
            },
            Optional.of(cookie)));
  }

  Result extractMovingSongsToOtherPlaylist(String rawMovingSongsToOtherPlaylistResult) {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(rawMovingSongsToOtherPlaylistResult);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    int resultCode = jsonNode.get("result").intValue();
    if (resultCode == 100) {
      return Result.ok();
    } else if (resultCode == 200) {
      return Result.fail("Move songs failed: Invalid parameters");
    } else if (resultCode == 301) {
      return Result.fail("QQ music proxy server needs to login");
    } else {
      return Result.fail(rawMovingSongsToOtherPlaylistResult);
    }
  }

  /**
   * Remove songs with song id {@code songsId} from playlist with dirId {@code dirId}.
   *
   * @param dirId The dirId of playlist that you want to remove songs from.
   * @param songsId The songs' id, multiple songs id separated with comma.
   * @param cookie Your qq music cookie.
   * @return The response of request wrapped by Result DTO.
   * @apiNote GET /playlist/remove?dirid={@code dirId}&id={@code songsId}
   */
  @Override
  public Result removeSongsFromPlaylist(Integer dirId, String songsId, String cookie) {
    return extractRemovingPlaylistResult(
        requestGetAPI(
            QQMusicAPI.REMOVE_SONGS_FROM_PLAYLIST,
            new HashMap<String, String>() {
              {
                put("dirid", dirId.toString());
                put("id", songsId);
              }
            },
            Optional.of(cookie)));
  }

  Result extractRemovingPlaylistResult(String rawRemovingPlaylistResult) {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(rawRemovingPlaylistResult);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    int resultCode = jsonNode.get("result").intValue();
    if (resultCode == 100) {
      return Result.ok();
    } else if (resultCode == 200) {
      return Result.fail("Remove songs failed: Invalid parameters");
    } else if (resultCode == 301) {
      return Result.fail("QQ music proxy server needs to login");
    } else {
      return Result.fail(rawRemovingPlaylistResult);
    }
  }
}
