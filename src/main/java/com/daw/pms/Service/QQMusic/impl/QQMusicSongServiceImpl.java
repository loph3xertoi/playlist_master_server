package com.daw.pms.Service.QQMusic.impl;

import com.daw.pms.Config.QQMusicAPI;
import com.daw.pms.Entity.Basic.BasicSinger;
import com.daw.pms.Entity.QQMusic.*;
import com.daw.pms.Service.QQMusic.QQMusicSongService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.Serializable;
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
public class QQMusicSongServiceImpl extends QQMusicBase
    implements QQMusicSongService, Serializable {
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
   * Get detail song with mid {@code songMid}.
   *
   * @param songMid The mid of this song.
   * @param cookie Your qq music cookie.
   * @return Detail song with {@code songMid}, songLink and isTakenDown needs to be completed.
   * @apiNote GET /song?songmid={@code songMid}
   */
  @Override
  public QQMusicDetailSong getDetailSong(String songMid, String cookie) {
    QQMusicLyrics qqMusicLyrics = getLyrics(songMid, cookie);
    QQMusicDetailSong qqMusicSong =
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
  private QQMusicDetailSong extractDetailSong(String rawSongDetail) {
    JsonNode jsonNode;
    try {
      jsonNode = new ObjectMapper().readTree(rawSongDetail);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    QQMusicDetailSong qqMusicSong = new QQMusicDetailSong();
    JsonNode trackInfoNode = jsonNode.get("data").get("track_info");
    JsonNode trackFileNode = jsonNode.get("data").get("track_info").get("file");
    JsonNode singersNode = trackInfoNode.get("singer");
    JsonNode infoNode = jsonNode.get("data").get("info");
    qqMusicSong.setName(trackInfoNode.get("name").textValue());

    qqMusicSong.setSubTitle(trackInfoNode.get("subtitle").textValue());

    qqMusicSong.setAlbumName(trackInfoNode.get("album").get("name").textValue());

    List<BasicSinger> singerList = new ArrayList<>();
    for (JsonNode singerNode : singersNode) {
      QQMusicSinger singer = new QQMusicSinger();
      singer.setName(singerNode.get("name").textValue());
      singer.setId(singerNode.get("id").intValue());
      singer.setMid(singerNode.get("mid").textValue());
      singer.setHeadPic(
          "http://y.gtimg.cn/music/photo_new/T001R150x150M000" + singer.getMid() + "_8.jpg");
      singerList.add(singer);
    }
    qqMusicSong.setSingers(singerList);

    String albumMid = trackInfoNode.get("album").get("mid").textValue();
    String songCoverUri;
    if (!albumMid.isEmpty()) {
      songCoverUri = "https://y.qq.com/music/photo_new/T002R300x300M000" + albumMid + "_2.jpg";
    } else {
      BasicSinger singer = singerList.get(0);
      QQMusicSinger qqMusicSinger = (QQMusicSinger) singer;
      songCoverUri =
          "https://y.qq.com/music/photo_new/T001R300x300M000" + qqMusicSinger.getMid() + "_3.jpg";
    }
    qqMusicSong.setCover(songCoverUri);

    qqMusicSong.setPayPlay(trackInfoNode.get("pay").get("pay_play").intValue());
    qqMusicSong.setSongId(trackInfoNode.get("id").asText());
    qqMusicSong.setSongMid(trackInfoNode.get("mid").textValue());
    qqMusicSong.setMediaMid(trackFileNode.get("media_mid").textValue());
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
   * @return A list of songs that is similar to song {@code songId}, songLink and isTakenDown needs
   *     to be completed.
   * @apiNote GET /song/similar?id={@code songId}
   */
  @Override
  public List<QQMusicSong> getSimilarSongs(String songId, String cookie) {
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
   * Resolve raw similar songs.
   *
   * @param rawSimilarSongs Raw similar songs returned by proxy qq music api server.
   * @return A list of basic songs.
   */
  private List<QQMusicSong> extractSimilarSongs(String rawSimilarSongs) {
    JsonNode jsonNode;
    try {
      jsonNode = new ObjectMapper().readTree(rawSimilarSongs);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    List<QQMusicSong> songList = new ArrayList<>();
    for (JsonNode songNode : jsonNode.get("data")) {
      JsonNode singersNode = songNode.get("singer");
      ArrayList<BasicSinger> qqMusicSingers = new ArrayList<>();
      QQMusicSong qqMusicSong = new QQMusicSong();
      qqMusicSong.setName(songNode.get("name").textValue());
      qqMusicSong.setSongId(songNode.get("id").asText());
      qqMusicSong.setSongMid(songNode.get("mid").textValue());
      qqMusicSong.setMediaMid(songNode.get("file").get("media_mid").textValue());
      qqMusicSong.setPayPlay(songNode.get("pay").get("pay_play").intValue());
      for (JsonNode singerNode : singersNode) {
        QQMusicSinger singer = new QQMusicSinger();
        singer.setName(singerNode.get("name").textValue());
        singer.setId(singerNode.get("id").intValue());
        singer.setMid(singerNode.get("mid").textValue());
        singer.setHeadPic(
            "http://y.gtimg.cn/music/photo_new/T001R150x150M000" + singer.getMid() + "_8.jpg");
        qqMusicSingers.add(singer);
      }
      qqMusicSong.setSingers(qqMusicSingers);

      String albumMid = songNode.get("album").get("mid").textValue();
      String songCoverUri;
      if (!albumMid.isEmpty()) {
        songCoverUri = "https://y.qq.com/music/photo_new/T002R300x300M000" + albumMid + "_2.jpg";
      } else {
        QQMusicSinger qqMusicSinger = (QQMusicSinger) qqMusicSingers.get(0);
        String singerMid = qqMusicSinger.getMid();
        songCoverUri = "https://y.qq.com/music/photo_new/T001R300x300M000" + singerMid + "_3.jpg";
      }
      qqMusicSong.setCover(songCoverUri);

      songList.add(qqMusicSong);
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
   * @deprecated Not used.
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
   * @param mediaMid The mediaMid of song.
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
   * @return PMSSong's play/download link.
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
   * Get the url of songs with songMids {@code songMids}.
   *
   * @param songMids The songMid, separated with comma.
   * @param cookie Your qq music cookie.
   * @return The urls of your songs with mid {@code songMids}.
   * @apiNote GET /song/urls?id={@code songMids}
   */
  @Override
  public Map<String, String> getSongsLink(String songMids, String cookie) {
    return extractSongLinks(
        requestGetAPI(
            QQMusicAPI.GET_SONGS_LINK,
            new HashMap<String, String>() {
              {
                put("id", songMids);
              }
            },
            Optional.of(cookie)));
  }

  /**
   * Extract raw song links into map.
   *
   * @param rawSongLinks Raw songs link returned by the proxy qq music api server.
   * @return Map containing all songs link.
   */
  private Map<String, String> extractSongLinks(String rawSongLinks) {
    JsonNode jsonNode;
    try {
      jsonNode = new ObjectMapper().readTree(rawSongLinks);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    int result = jsonNode.get("result").intValue();
    // Success for getting the song links.
    if (result == 100) {
      jsonNode = jsonNode.get("data");
      Map<String, String> map = new HashMap<>();

      Iterator<String> fieldNames = jsonNode.fieldNames();
      while (fieldNames.hasNext()) {
        String fieldName = fieldNames.next();
        JsonNode fieldValue = jsonNode.get(fieldName);
        map.put(fieldName, fieldValue.asText());
      }
      return map;
    } else {
      return null;
    }
  }

  /**
   * Search and return paged songs according to the given keyword {@code name}.
   *
   * @param name The search keyword.
   * @param pageNo Page order.
   * @param pageSize Size one page.
   * @param cookie Your qq music cookie.
   * @return A list of paged QQMusicSong wrapped by QQMusicSearchSongPagedResult.
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
                put("pageNo", String.valueOf(pageNo));
                put("pageSize", String.valueOf(pageSize));
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
    List<QQMusicSong> basicSongList = new ArrayList<>();
    JsonNode dataNode = jsonNode.get("data");
    JsonNode listNode = dataNode.get("list");

    pagedResult.setPageSize(dataNode.get("pageSize").asInt());
    pagedResult.setPageNo(dataNode.get("pageNo").asInt());
    pagedResult.setTotal(dataNode.get("total").asInt());

    listNode.forEach(
        songNode ->
            basicSongList.add(
                new QQMusicSong() {
                  {
                    setName(songNode.get("name").textValue());
                    setSongId(songNode.get("songid").asText());
                    setSongMid(songNode.get("songmid").textValue());
                    setMediaMid(songNode.get("strMediaMid").textValue());
                    List<BasicSinger> singers =
                        new ArrayList<BasicSinger>() {
                          {
                            songNode
                                .get("singer")
                                .forEach(
                                    singerNode ->
                                        add(
                                            new QQMusicSinger() {
                                              {
                                                setName(singerNode.get("name").textValue());
                                                setId(singerNode.get("id").intValue());
                                                setMid(singerNode.get("mid").textValue());
                                                setHeadPic(
                                                    "http://y.gtimg.cn/music/photo_new/T001R150x150M000"
                                                        + getMid()
                                                        + "_8.jpg");
                                              }
                                            }));
                          }
                        };
                    setSingers(singers);
                    String albumMid = songNode.get("albummid").textValue();
                    String songCoverUri;
                    if (!albumMid.isEmpty()) {
                      songCoverUri =
                          "https://y.qq.com/music/photo_new/T002R300x300M000" + albumMid + "_2.jpg";
                    } else {
                      QQMusicSinger singer = (QQMusicSinger) singers.get(0);
                      String singerMid = singer.getMid();
                      songCoverUri =
                          "https://y.qq.com/music/photo_new/T001R300x300M000"
                              + singerMid
                              + "_3.jpg";
                    }
                    setCover(songCoverUri);
                    setPayPlay(songNode.get("pay").get("pay_play").intValue());
                  }
                }));

    pagedResult.setSongs(basicSongList);
    return pagedResult;
  }
}
