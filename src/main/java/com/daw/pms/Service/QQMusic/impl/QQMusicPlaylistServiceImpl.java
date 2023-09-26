package com.daw.pms.Service.QQMusic.impl;

import com.daw.pms.Config.QQMusicAPI;
import com.daw.pms.DTO.PagedDataDTO;
import com.daw.pms.DTO.Result;
import com.daw.pms.Entity.Basic.BasicSinger;
import com.daw.pms.Entity.QQMusic.QQMusicDetailPlaylist;
import com.daw.pms.Entity.QQMusic.QQMusicPlaylist;
import com.daw.pms.Entity.QQMusic.QQMusicSinger;
import com.daw.pms.Entity.QQMusic.QQMusicSong;
import com.daw.pms.Service.QQMusic.QQMusicPlaylistService;
import com.daw.pms.Service.QQMusic.QQMusicSongService;
import com.daw.pms.Utils.HttpTools;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class QQMusicPlaylistServiceImpl implements QQMusicPlaylistService {
  private final QQMusicSongService qqMusicSongService;
  private final HttpTools httpTools;
  private final String baseUrl;

  /**
   * Constructor for QQMusicPlaylistServiceImpl.
   *
   * @param qqMusicSongService a {@link com.daw.pms.Service.QQMusic.QQMusicSongService} object.
   * @param httpTools a {@link com.daw.pms.Utils.HttpTools} object.
   */
  public QQMusicPlaylistServiceImpl(QQMusicSongService qqMusicSongService, HttpTools httpTools) {
    this.qqMusicSongService = qqMusicSongService;
    this.httpTools = httpTools;
    this.baseUrl = "http://" + httpTools.qqmusicHost + ":" + httpTools.qqmusicPort;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Get all playlists of user {@code qid} in qq music platform.
   *
   * @apiNote GET /user/playlist?id={@code qid}
   */
  @Override
  public Result getPlaylists(String qid, String cookie) {
    PagedDataDTO<QQMusicPlaylist> data = new PagedDataDTO<>();
    List<QQMusicPlaylist> playlists =
        extractQQMusicPlaylists(
            httpTools.requestGetAPI(
                baseUrl + QQMusicAPI.GET_PLAYLIST,
                new HashMap<String, String>() {
                  {
                    put("id", qid);
                  }
                },
                Optional.of(cookie)));
    // TODO: the count and hasMore need to be repaired.
    data.setCount(playlists.size());
    data.setHasMore(false);
    data.setList(playlists);
    return Result.ok(data);
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
   * {@inheritDoc}
   *
   * <p>Get detail playlist with {@code tid}.
   *
   * @apiNote GET /playlist?id={@code tid}
   */
  @Override
  public Result getDetailPlaylist(String tid, String cookie) {
    QQMusicDetailPlaylist qqMusicDetailPlaylist =
        extractDetailPlaylist(
            httpTools.requestGetAPI(
                baseUrl + QQMusicAPI.GET_DETAIL_PLAYLIST,
                new HashMap<String, String>() {
                  {
                    put("id", tid);
                  }
                },
                Optional.of(cookie)));
    if (qqMusicDetailPlaylist == null) {
      throw new RuntimeException("This library isn't accessible");
    }
    Integer songCount = qqMusicDetailPlaylist.getItemCount();
    if (songCount > 0) {
      List<String> songMids = new ArrayList<>(qqMusicDetailPlaylist.getItemCount());
      qqMusicDetailPlaylist.getSongs().forEach(song -> songMids.add(song.getSongMid()));
      Collections.shuffle(songMids);
      Map<String, String> songsLink;
      Result linksResult = qqMusicSongService.getSongsLink(String.join(",", songMids), cookie);
      if (linksResult.getSuccess()) {
        songsLink = (Map<String, String>) linksResult.getData();
      } else {
        throw new RuntimeException(linksResult.getMessage());
      }
      qqMusicDetailPlaylist
          .getSongs()
          .forEach(
              song -> {
                song.setSongLink(songsLink.getOrDefault(song.getSongMid(), ""));
                song.setIsTakenDown(song.getSongLink().isEmpty());
              });
    }
    return Result.ok(qqMusicDetailPlaylist);
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
   * {@inheritDoc}
   *
   * <p>Create playlist.
   *
   * @apiNote GET /playlist/create?name={@code name}
   */
  @Override
  public Result createPlaylist(String name, String cookie) {
    return extractCreatingPlaylistResult(
        httpTools.requestGetAPI(
            baseUrl + QQMusicAPI.CREATE_PLAYLIST,
            new HashMap<String, String>() {
              {
                put("name", name);
              }
            },
            Optional.of(cookie)));
  }

  private Result extractCreatingPlaylistResult(String rawCreatingPlaylistResult) {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(rawCreatingPlaylistResult);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    int resultCode = jsonNode.get("result").intValue();
    if (resultCode == 100) {
      int dirId = jsonNode.get("data").get("dirid").intValue();
      return Result.ok(dirId);
    } else if (resultCode == 200) {
      throw new RuntimeException("Library already exists");
    } else if (resultCode == 301) {
      throw new RuntimeException("QQ music proxy server needs to login");
    } else {
      throw new RuntimeException(rawCreatingPlaylistResult);
    }
  }

  /**
   * {@inheritDoc}
   *
   * <p>Delete playlist with dirIds {@code dirIds}.
   *
   * @apiNote GET /playlist/delete?dirid={@code dirIds}
   */
  @Override
  public Result deletePlaylist(String dirIds, String cookie) {
    return extractDeletingPlaylistResult(
        httpTools.requestGetAPI(
            baseUrl + QQMusicAPI.DELETE_PLAYLIST,
            new HashMap<String, String>() {
              {
                put("dirid", dirIds);
              }
            },
            Optional.of(cookie)));
  }

  private Result extractDeletingPlaylistResult(String rawDeletingPlaylistResult) {
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
      throw new RuntimeException("Library delete failed: Invalid parameters");
    } else if (resultCode == 301) {
      throw new RuntimeException("QQ music proxy server needs to login");
    } else {
      throw new RuntimeException(rawDeletingPlaylistResult);
    }
  }

  /**
   * {@inheritDoc}
   *
   * <p>Add songs with mids {@code songsMid} to playlist with dirId {@code dirId}
   *
   * @apiNote GET /playlist/add?dirid={@code dirId}&amp;mid={@code songsMid}
   */
  @Override
  public Result addSongsToPlaylist(int dirId, String songsMid, String cookie) {
    return extractAddingSongsToPlaylistResult(
        httpTools.requestGetAPI(
            baseUrl + QQMusicAPI.ADD_SONGS_TO_PLAYLIST,
            new HashMap<String, String>() {
              {
                put("dirid", String.valueOf(dirId));
                put("mid", songsMid);
              }
            },
            Optional.of(cookie)));
  }

  private Result extractAddingSongsToPlaylistResult(String rawAddingSongsToPlaylistResult) {
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
      throw new RuntimeException("Add songs failed: Invalid parameters");
    } else if (resultCode == 301) {
      throw new RuntimeException("QQ music proxy server needs to login");
    } else {
      throw new RuntimeException(rawAddingSongsToPlaylistResult);
    }
  }

  /**
   * {@inheritDoc}
   *
   * <p>Move songs {@code songsId} from playlist with {@code fromDirId} to playlist with {@code
   * toDirId}.
   *
   * @apiNote GET /move?id={@code songsId}&amp;from_dir={@code fromDirId}&amp;to_dir={@code toDirId}
   */
  @Override
  public Result moveSongsToOtherPlaylist(
      String songsId, int fromDirId, int toDirId, String cookie) {
    return extractMovingSongsToOtherPlaylist(
        httpTools.requestGetAPI(
            baseUrl + QQMusicAPI.MOVE_SONGS_TO_OTHER_PLAYLIST,
            new HashMap<String, String>() {
              {
                put("id", songsId);
                put("from_dir", String.valueOf(fromDirId));
                put("to_dir", String.valueOf(toDirId));
              }
            },
            Optional.of(cookie)));
  }

  private Result extractMovingSongsToOtherPlaylist(String rawMovingSongsToOtherPlaylistResult) {
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
      throw new RuntimeException("Move songs failed: Invalid parameters");
    } else if (resultCode == 301) {
      throw new RuntimeException("QQ music proxy server needs to login");
    } else {
      throw new RuntimeException(rawMovingSongsToOtherPlaylistResult);
    }
  }

  /**
   * {@inheritDoc}
   *
   * <p>Remove songs with song id {@code songsId} from playlist with dirId {@code dirId}.
   *
   * @apiNote GET /playlist/remove?dirid={@code dirId}&amp;id={@code songsId}
   */
  @Override
  public Result removeSongsFromPlaylist(int dirId, String songsId, String cookie) {
    return extractRemovingPlaylistResult(
        httpTools.requestGetAPI(
            baseUrl + QQMusicAPI.REMOVE_SONGS_FROM_PLAYLIST,
            new HashMap<String, String>() {
              {
                put("dirid", String.valueOf(dirId));
                put("id", songsId);
              }
            },
            Optional.of(cookie)));
  }

  private Result extractRemovingPlaylistResult(String rawRemovingPlaylistResult) {
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
      throw new RuntimeException("Remove songs failed: Invalid parameters");
    } else if (resultCode == 301) {
      throw new RuntimeException("QQ music proxy server needs to login");
    } else {
      throw new RuntimeException(rawRemovingPlaylistResult);
    }
  }
}
