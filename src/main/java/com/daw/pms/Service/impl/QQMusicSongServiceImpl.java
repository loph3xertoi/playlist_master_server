package com.daw.pms.Service.impl;

import com.daw.pms.Config.QQMusicAPI;
import com.daw.pms.Entity.*;
import com.daw.pms.Service.QQMusicSongService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import org.springframework.stereotype.Service;

/**
 * Service for handle songs of qq music.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 6/3/23
 */
@Service
public class QQMusicSongServiceImpl extends QQMusicBase implements QQMusicSongService {
  /**
   * Get songs id from playlist {@code dirId}.
   *
   * @param dirId Playlist's dirId.
   * @param cookie Your qq music cookie.
   * @return A list of songId for your playlist with {@code dirId}.
   * @apiNote GET /playlist/map?dirid={@code dirId}
   */
  @Override
  public List<String> getSongsIdFromPlaylist(String dirId, String cookie) {
    return extractSongsIdFromPlaylist(
        requestGetAPI(
            QQMusicAPI.GET_SONGS_ID_FROM_PLAYLIST,
            new HashMap<String, String>() {
              {
                put("dirid", dirId);
              }
            },
            Optional.of(cookie)));
  }

  /**
   * Extract raw songs id into a list of id string.
   *
   * @param rawSongsId Raw songsId returned by proxy qq music api server.
   * @return A list of String of songId.
   */
  private List<String> extractSongsIdFromPlaylist(String rawSongsId) {
    JsonNode jsonNode;
    try {
      jsonNode = new ObjectMapper().readTree(rawSongsId);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    List<String> songsId = new ArrayList<>();
    JsonNode idNode = jsonNode.get("data").get("id");
    Iterator<Map.Entry<String, JsonNode>> ids = idNode.fields();
    while (ids.hasNext()) {
      songsId.add(ids.next().getKey());
    }
    return songsId;
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
   * Get detail of song with mid {@code songMid}.
   *
   * @param songMid The mid of this song.
   * @param cookie Your qq music cookie.
   * @return The QQMusicSong with {@code songMid}.
   * @apiNote GET /song?songmid={@code songMid}
   */
  @Override
  public QQMusicSong getDetailSong(String songMid, String cookie) {
    QQMusicLyrics qqMusicLyrics = getLyrics(songMid, cookie);
    QQMusicSong qqMusicSong =
        extractDetailSong(
            requestGetAPI(
                QQMusicAPI.GET_SONG_DETAIL,
                new HashMap<String, String>() {
                  {
                    put("songmid", songMid);
                  }
                },
                Optional.of(cookie)));
    qqMusicSong.setLyrics(qqMusicLyrics);
    //    qqMusicSong.setPmPlaylists();
    return qqMusicSong;
  }

  /**
   * Extracted raw song detail to QQMusicSong.
   *
   * @param rawSongDetail Raw song's detail info returned by proxy qq music api server.
   * @return Wrapped QQMusicSong objected.
   */
  private QQMusicSong extractDetailSong(String rawSongDetail) {
    JsonNode jsonNode;
    try {
      jsonNode = new ObjectMapper().readTree(rawSongDetail);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    QQMusicSong qqMusicSong = new QQMusicSong();
    List<QQMusicSinger> singerList = new ArrayList<>();
    JsonNode trackInfoNode = jsonNode.get("data").get("track_info");
    JsonNode trackFileNode = jsonNode.get("data").get("track_info").get("file");
    JsonNode singersNode = trackInfoNode.get("singer");
    JsonNode infoNode = jsonNode.get("data").get("info");
    qqMusicSong.setSongName(trackInfoNode.get("name").textValue());

    qqMusicSong.setSubTitle(trackInfoNode.get("subtitle").textValue());

    qqMusicSong.setAlbumName(trackInfoNode.get("album").get("name").textValue());

    for (JsonNode singerNode : singersNode) {
      QQMusicSinger singer = new QQMusicSinger();
      singer.setName(singerNode.get("name").textValue());
      singer.setId(singerNode.get("id").asText());
      singer.setMid(singerNode.get("mid").textValue());
      singerList.add(singer);
    }
    qqMusicSong.setSingers(singerList);

    String albumMid = trackInfoNode.get("album").get("mid").textValue();
    String songCoverUri;
    if (!albumMid.isEmpty()) {
      songCoverUri = "https://y.qq.com/music/photo_new/T002R300x300M000" + albumMid + "_2.jpg";
    } else {
      String singerMid = singerList.get(0).getMid();
      songCoverUri = "https://y.qq.com/music/photo_new/T001R300x300M000" + singerMid + "_3.jpg";
    }
    qqMusicSong.setCoverUri(songCoverUri);

    qqMusicSong.setPayPlay(trackInfoNode.get("pay").get("pay_play").intValue());
    qqMusicSong.setSongId(trackInfoNode.get("id").asText());
    qqMusicSong.setSongMid(trackInfoNode.get("mid").textValue());
    qqMusicSong.setMediaMid(trackFileNode.get("media_mid").textValue());
    qqMusicSong.setVid(trackInfoNode.get("mv").get("vid").textValue());
    qqMusicSong.setDuration(trackInfoNode.get("interval").intValue());

    if (infoNode.has("intro")) {
      qqMusicSong.setSongDesc(
          infoNode.get("intro").get("content").iterator().next().get("value").textValue());
    } else {
      qqMusicSong.setSongDesc(trackInfoNode.get("album").get("title").textValue());
    }

    qqMusicSong.setPubTime(trackInfoNode.get("time_public").textValue());
    qqMusicSong.setSize128(trackFileNode.get("size_128mp3").longValue());
    qqMusicSong.setSize320(trackFileNode.get("size_320mp3").longValue());
    qqMusicSong.setSizeApe(trackFileNode.get("size_ape").longValue());
    qqMusicSong.setSizeFlac(trackFileNode.get("size_flac").longValue());

    return qqMusicSong;
  }

  /**
   * Get the similar songs according to {@code songId}.
   *
   * @param songId The id of song.
   * @param cookie Your qq music cookie.
   * @return A list of QQMusicSong that is similar to song {@code songId}.
   * @apiNote GET /song/similar?id={@code songId}
   */
  @Override
  public List<QQMusicBasicSong> getSimilarSongs(String songId, String cookie) {
    return extractSimilarSongs(
        requestGetAPI(
            QQMusicAPI.GET_SIMILAR_SONGS,
            new HashMap<String, String>() {
              {
                put("id", songId);
              }
            },
            Optional.of(cookie)));
  }

  /**
   * Extracted raw similar songs to a list of QQMusicBasicSong.
   *
   * @param rawSimilarSongs Raw similar songs returned by proxy qq music api server.
   * @return A list of QQMusicBasicSong.
   */
  private List<QQMusicBasicSong> extractSimilarSongs(String rawSimilarSongs) {
    JsonNode jsonNode;
    try {
      jsonNode = new ObjectMapper().readTree(rawSimilarSongs);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    List<QQMusicBasicSong> songList = new ArrayList<>();
    for (JsonNode songNode : jsonNode.get("data")) {
      JsonNode singersNode = songNode.get("singer");
      ArrayList<QQMusicSinger> qqMusicSingers = new ArrayList<>();
      QQMusicBasicSong qqMusicBasicSong = new QQMusicBasicSong();
      qqMusicBasicSong.setSongName(songNode.get("name").textValue());
      qqMusicBasicSong.setSongId(songNode.get("id").asText());
      qqMusicBasicSong.setSongMid(songNode.get("mid").textValue());
      qqMusicBasicSong.setMediaMid(songNode.get("file").get("media_mid").textValue());
      qqMusicBasicSong.setPayPlay(songNode.get("pay").get("pay_play").intValue());

      for (JsonNode singerNode : singersNode) {
        QQMusicSinger singer = new QQMusicSinger();
        singer.setName(singerNode.get("name").textValue());
        singer.setId(singerNode.get("id").asText());
        singer.setMid(singerNode.get("mid").textValue());
        qqMusicSingers.add(singer);
      }
      qqMusicBasicSong.setSingers(qqMusicSingers);
      songList.add(qqMusicBasicSong);
    }
    return songList;
  }

  /**
   * Get the lyrics of the song with {@code songMid}.
   *
   * @param songMid The mid of song.
   * @param cookie Your qq music cookie.
   * @return Lyrics of your song in QQMusicLyrics.
   * @apiNote GET /lyric?songmid={@code songMid}
   */
  @Override
  public QQMusicLyrics getLyrics(String songMid, String cookie) {
    return extractLyrics(
        requestGetAPI(
            QQMusicAPI.GET_LYRICS,
            new HashMap<String, String>() {
              {
                put("songmid", songMid);
              }
            },
            Optional.of(cookie)));
  }

  /**
   * Extract raw lyrics into QQMusicLyrics.
   *
   * @param rawLyrics Raw lyrics returned by proxy qq music api server.
   * @return The lyrics and trans wrapped in QQMusicLyrics.
   */
  private QQMusicLyrics extractLyrics(String rawLyrics) {
    JsonNode jsonNode;
    try {
      jsonNode = new ObjectMapper().readTree(rawLyrics);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    QQMusicLyrics qqMusicLyrics = new QQMusicLyrics();
    qqMusicLyrics.setLyric(jsonNode.get("data").get("lyric").textValue());
    qqMusicLyrics.setTrans(jsonNode.get("data").get("trans").textValue());
    return qqMusicLyrics;
  }

  /**
   * Get the cover uri of the song/album with {@code albumMid}.
   *
   * @param albumMid The albumMid of song.
   * @param cookie Your qq music cookie.
   * @return Cover uri of your song.
   * @apiNote GET /album?albummid={@code albumMid}
   */
  @Override
  public String getSongCoverUri(String albumMid, String cookie) {
    return extractCoverUri(
        requestGetAPI(
            QQMusicAPI.GET_ALBUM_INFO,
            new HashMap<String, String>() {
              {
                put("albummid", albumMid);
              }
            },
            Optional.of(cookie)));
  }

  /**
   * Extract cover uri of song/album.
   *
   * @param rawAlbumInfo Raw album information returned by proxy qq music api server.
   * @return The cover uri of song or album.
   */
  private String extractCoverUri(String rawAlbumInfo) {
    JsonNode jsonNode;
    try {
      jsonNode = new ObjectMapper().readTree(rawAlbumInfo);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    return "http:" + jsonNode.get("data").get("picurl").textValue();
  }

  /**
   * Get the url of song with songMid {@code songMid} and mediaMid {@code mediaMid} and type {@code
   * type}.
   *
   * @param songMid The song mid.
   * @param type The quality(128, 320, flac, m4a, ogg) of song you want to get.
   * @param mediaMid The mediaMid of the song.
   * @param cookie Your qq music cookie.
   * @return The url of your song with mid {@code songMid} and mediaMid {@code mediaMid} and type
   *     {@code type}
   * @apiNote GET /song/url?id={@code songMid}&type={@code type}&mediaId={@code mediaMid}
   */
  @Override
  public String getSongLink(String songMid, String type, String mediaMid, String cookie) {
    return extractSongLink(
        requestGetAPI(
            QQMusicAPI.GET_SONG_LINK,
            new HashMap<String, String>() {
              {
                put("id", songMid);
                put("type", type);
                put("mediaId", mediaMid);
              }
            },
            Optional.of(cookie)));
  }

  /**
   * Extract raw song links into string.
   *
   * @param rawSongLink Raw song link returned by the proxy qq music api server.
   * @return Song's play/download link.
   */
  private String extractSongLink(String rawSongLink) {
    JsonNode jsonNode;
    try {
      jsonNode = new ObjectMapper().readTree(rawSongLink);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    int result = jsonNode.get("result").intValue();
    // Success for getting the song link.
    if (result == 100) {
      return jsonNode.get("data").textValue();
    } else {
      return "";
    }
  }

  /**
   * Search and return paged songs according to the given keyword {@code name}.
   *
   * @param name The search keyword.
   * @param pageNo Page order.
   * @param pageSize Size one page.
   * @param cookie Your qq music cookie.
   * @return A list of paged QQMusicBasicSong wrapped by QQMusicSearchSongPagedResult.
   * @apiNote GET /search?key={@code name}&pageNo={@code pageNo}&pageSize={@code pageSize}
   */
  @Override
  public QQMusicSearchSongPagedResult searchSongByName(
      String name, Integer pageNo, Integer pageSize, String cookie) {
    return extractSearchSongPagedResult(
        requestGetAPI(
            QQMusicAPI.SEARCH_SONGS,
            new HashMap<String, String>() {
              {
                put("key", name);
                put("type", String.valueOf(pageNo));
                put("mediaId", String.valueOf(pageSize));
              }
            },
            Optional.of(cookie)));
  }

  /**
   * Extract raw search result with QQMusicSearchSongPagedResult.
   *
   * @param rawSearchSongPagedResult Raw paged search result returned by proxy qq music api server.
   * @return Wrapped with QQMusicSearchSongPagedResult.
   */
  private QQMusicSearchSongPagedResult extractSearchSongPagedResult(
      String rawSearchSongPagedResult) {
    JsonNode jsonNode;
    try {
      jsonNode = new ObjectMapper().readTree(rawSearchSongPagedResult);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    QQMusicSearchSongPagedResult pagedResult = new QQMusicSearchSongPagedResult();
    ArrayList<QQMusicBasicSong> basicSongList = new ArrayList<>();
    JsonNode dataNode = jsonNode.get("data");
    JsonNode listNode = dataNode.get("list");

    pagedResult.setPageSize(dataNode.get("pageSize").asInt());
    pagedResult.setPageNo(dataNode.get("pageNo").asInt());
    pagedResult.setTotal(dataNode.get("total").asInt());

    listNode.forEach(
        songNode ->
            basicSongList.add(
                new QQMusicBasicSong() {
                  {
                    setSongName(songNode.get("name").textValue());
                    setSongId(songNode.get("songid").asText());
                    setSongMid(songNode.get("songmid").textValue());
                    setMediaMid(songNode.get("strMediaMid").textValue());
                    setSingers(
                        new ArrayList<QQMusicSinger>() {
                          {
                            songNode
                                .get("singer")
                                .forEach(
                                    singerNode ->
                                        add(
                                            new QQMusicSinger() {
                                              {
                                                setName(singerNode.get("name").textValue());
                                                setId(singerNode.get("id").asText());
                                                setMid(singerNode.get("mid").textValue());
                                              }
                                            }));
                          }
                        });
                    setPayPlay(songNode.get("pay").get("pay_play").intValue());
                  }
                }));

    pagedResult.setBasicSongList(basicSongList);
    return pagedResult;
  }

  /**
   * Add songs with mids {@code songsMid} to playlist with dirId {@code dirId}
   *
   * @param dirId The dirId of the playlist.
   * @param songsMid The mid of songs, multiple mid separated with comma.
   * @param cookie Your qq music cookie.
   * @return 100 for success, 200 for failure.
   * @apiNote GET /playlist/add?dirid={@code dirId}&mid={@code songsMid}
   */
  @Override
  public String addSongsToPlaylist(String dirId, String songsMid, String cookie) {
    return requestGetAPI(
        QQMusicAPI.ADD_SONGS_TO_PLAYLIST,
        new HashMap<String, String>() {
          {
            put("dirid", dirId);
            put("mid", songsMid);
          }
        },
        Optional.of(cookie));
  }

  /**
   * Move songs {@code songsId} from playlist with {@code fromDirId} to playlist with {@code
   * toDirId}.
   *
   * @param songsId Songs id to be moved, multiple songs id separated with comma.
   * @param fromDirId DirId of from-playlist.
   * @param toDirId DirId of to-playlist.
   * @param cookie Your qq music cookie.
   * @return 100 for success, 200 for failure.
   * @apiNote GET /move?id={@code songsId}&from_dir={@code fromDirId}&to_dir={@code toDirId}
   */
  @Override
  public String moveSongsToOtherPlaylist(
      String songsId, String fromDirId, String toDirId, String cookie) {
    return requestGetAPI(
        QQMusicAPI.MOVE_SONGS_TO_OTHER_PLAYLIST,
        new HashMap<String, String>() {
          {
            put("id", songsId);
            put("from_dir", fromDirId);
            put("to_dir", toDirId);
          }
        },
        Optional.of(cookie));
  }

  /**
   * Remove songs with song id {@code songId} from playlist with dirId {@code dirId}.
   *
   * @param dirId The dirId of playlist that you want to remove songs from.
   * @param songId The songs' id, multiple songs id separated with comma.
   * @param cookie Your qq music cookie.
   * @return 100 for success.
   * @apiNote GET /playlist/remove?dirid={@code dirId}&id={@code songId}
   */
  @Override
  public String removeSongsFromPlaylist(String dirId, String songId, String cookie) {
    return requestGetAPI(
        QQMusicAPI.REMOVE_SONGS_FROM_PLAYLIST,
        new HashMap<String, String>() {
          {
            put("dirid", dirId);
            put("id", songId);
          }
        },
        Optional.of(cookie));
  }
}
