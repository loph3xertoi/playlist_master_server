package com.daw.pms.Service.NeteaseCloudMusic.impl;

import com.daw.pms.Config.NCMAPI;
import com.daw.pms.Entity.Basic.BasicSinger;
import com.daw.pms.Entity.NeteaseCloudMusic.*;
import com.daw.pms.Service.NeteaseCloudMusic.NCMSongService;
import com.daw.pms.Utils.HttpTools;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class NCMSongServiceImpl implements NCMSongService {

  private final HttpTools httpTools;

  public NCMSongServiceImpl(HttpTools httpTools) {
    this.httpTools = httpTools;
  }

  /**
   * Get detail song with {@code ids}.
   *
   * @param ids The id of song, multiple songs separated by comma.
   * @param cookie Your cookie for netease cloud music.
   * @return Detail song with {@code ids}.
   * @apiNote GET /song/detail?ids={@code ids}
   */
  @Override
  public NCMDetailSong getDetailSong(String ids, String cookie) {
    String baseUrl = httpTools.ncmHost + ":" + httpTools.ncmPort;
    NCMDetailSong detailSong =
        extractDetailSong(
            httpTools.requestGetAPI(
                baseUrl + NCMAPI.SONG_DETAIL,
                new HashMap<String, String>() {
                  {
                    put("ids", ids);
                  }
                },
                Optional.of(cookie)));
    NCMLyrics lyrics = getLyrics(Long.valueOf(ids), cookie);
    detailSong.setLyrics(lyrics);
    List<BasicSinger> singers = detailSong.getSingers();
    for (BasicSinger singer : singers) {
      String singerId = ((NCMSinger) singer).getId().toString();
      if (singerId.equals("0")) {
        singer.setHeadPic("");
        continue;
      }
      singer.setHeadPic(
          extractSingerAvatar(
              httpTools.requestGetAPI(
                  baseUrl + NCMAPI.ARTIST_DETAIL,
                  new HashMap<String, String>() {
                    {
                      put("id", singerId);
                    }
                  },
                  Optional.of(cookie))));
    }
    Map<String, String> links = getSongsLink(ids, "standard", cookie);
    detailSong.setSongLink(links.getOrDefault(detailSong.getId().toString(), ""));
    detailSong.setIsTakenDown(detailSong.getSongLink().isEmpty());
    return detailSong;
  }

  private String extractSingerAvatar(String rawDetailSinger) {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(rawDetailSinger);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    return jsonNode.get("data").get("artist").get("avatar").textValue();
  }

  private NCMDetailSong extractDetailSong(String rawDetailSong) {
    NCMDetailSong detailSong = new NCMDetailSong();
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(rawDetailSong);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    JsonNode songsNode = jsonNode.get("songs");
    JsonNode songNode = songsNode.get(0);
    detailSong.setName(songNode.get("name").textValue());

    List<BasicSinger> singers = new ArrayList<>();
    JsonNode singersNode = songNode.get("ar");
    for (JsonNode singerNode : singersNode) {
      NCMSinger singer = new NCMSinger();
      singer.setId(singerNode.get("id").longValue());
      singer.setName(singerNode.get("name").textValue());
      singers.add(singer);
    }
    detailSong.setSingers(singers);

    detailSong.setCover(songNode.get("al").get("picUrl").textValue());
    detailSong.setPayPlay(songNode.get("fee").intValue());
    detailSong.setId(songNode.get("id").longValue());
    detailSong.setMvId(songNode.get("mv").longValue());
    detailSong.setAlbumName(songNode.get("al").get("name").textValue());
    detailSong.setDuration(songNode.get("dt").intValue());
    detailSong.setPublishTime(songNode.get("publishTime").asText());
    detailSong.setHBr(songNode.get("h").get("br").intValue());
    detailSong.setHSize(songNode.get("h").get("size").longValue());
    detailSong.setMBr(songNode.get("m").get("br").intValue());
    detailSong.setMSize(songNode.get("m").get("size").longValue());
    detailSong.setLBr(songNode.get("l").get("br").intValue());
    detailSong.setLSize(songNode.get("l").get("size").longValue());
    detailSong.setSqBr(songNode.get("sq").get("br").intValue());
    detailSong.setSqSize(songNode.get("sq").get("size").longValue());
    return detailSong;
  }

  /**
   * Get the similar songs of song {@code id}.
   *
   * @param id The id of song.
   * @param cookie Your cookie for netease cloud music.
   * @return A list of songs that is similar to song {@code id}.
   * @apiNote GET /simi/song?id={@code id}
   */
  @Override
  public List<NCMSong> getSimilarSongs(Long id, String cookie) {
    String baseUrl = httpTools.ncmHost + ":" + httpTools.ncmPort;
    List<NCMSong> songs =
        extractSimilarSongs(
            httpTools.requestGetAPI(
                baseUrl + NCMAPI.SIMI_SONG,
                new HashMap<String, String>() {
                  {
                    put("id", id.toString());
                  }
                },
                Optional.of(cookie)));

    String ids =
        songs.stream().map(song -> song.getId().toString()).collect(Collectors.joining(","));
    Map<String, String> links = getSongsLink(ids, "standard", cookie);
    for (NCMSong song : songs) {
      song.setSongLink(links.getOrDefault(song.getId().toString(), ""));
      song.setIsTakenDown(song.getSongLink().isEmpty());
    }
    return songs;
  }

  private List<NCMSong> extractSimilarSongs(String rawSimilarSongs) {
    List<NCMSong> similarSongs = new ArrayList<>();
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(rawSimilarSongs);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    JsonNode songsNode = jsonNode.get("songs");
    for (JsonNode songNode : songsNode) {
      NCMSong song = new NCMSong();
      song.setId(songNode.get("id").longValue());
      song.setMvId(songNode.get("mvid").longValue());
      song.setName(songNode.get("name").textValue());

      List<BasicSinger> singers = new ArrayList<>();
      JsonNode singersNode = songNode.get("artists");
      for (JsonNode singerNode : singersNode) {
        NCMSinger singer = new NCMSinger();
        singer.setId(singerNode.get("id").longValue());
        singer.setName(singerNode.get("name").textValue());
        singers.add(singer);
      }
      song.setSingers(singers);

      song.setCover(songNode.get("album").get("picUrl").textValue());
      song.setPayPlay(songNode.get("fee").intValue());
      similarSongs.add(song);
    }
    return similarSongs;
  }

  /**
   * Get the lyrics of the song with {@code id}.
   *
   * @param id The id of song.
   * @param cookie Your cookie for netease cloud music.
   * @return Lyrics of your song in netease cloud music.
   * @apiNote GET /lyric/new?id={@code id}
   */
  @Override
  public NCMLyrics getLyrics(Long id, String cookie) {
    String baseUrl = httpTools.ncmHost + ":" + httpTools.ncmPort;
    return extractNCMLyrics(
        httpTools.requestGetAPI(
            baseUrl + NCMAPI.LYRIC_NEW,
            new HashMap<String, String>() {
              {
                put("id", id.toString());
              }
            },
            Optional.of(cookie)));
  }

  private NCMLyrics extractNCMLyrics(String rawLyrics) {
    NCMLyrics lyrics = new NCMLyrics();
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(rawLyrics);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    if (jsonNode.has("lrc")) {
      String lyricValue = jsonNode.get("lrc").get("lyric").textValue();
      lyrics.setLyric(lyricValue);
    } else {
      lyrics.setLyric("");
    }
    if (jsonNode.has("klyric")) {
      lyrics.setKLyric(jsonNode.get("klyric").get("lyric").textValue());
    } else {
      lyrics.setKLyric("");
    }
    if (jsonNode.has("tlyric")) {
      lyrics.setTLyric(jsonNode.get("tlyric").get("lyric").textValue());
    } else {
      lyrics.setTLyric("");
    }
    if (jsonNode.has("romalrc")) {
      lyrics.setRomaLrc(jsonNode.get("romalrc").get("lyric").textValue());
    } else {
      lyrics.setRomaLrc("");
    }
    if (jsonNode.has("yrc")) {
      lyrics.setYrc(jsonNode.get("yrc").get("lyric").textValue());
    } else {
      lyrics.setYrc("");
    }
    if (jsonNode.has("ytlrc")) {
      lyrics.setYtLrc(jsonNode.get("ytlrc").get("lyric").textValue());
    } else {
      lyrics.setYtLrc("");
    }
    return lyrics;
  }

  /**
   * Get the url of songs with {@code ids} and quality {@code level}.
   *
   * @param ids The song's id, multiple songs separated by comma.
   * @param level Quality of song, include standard, higher, exhigh, lossless, hires, jyeffect, sky,
   *     jymaster.
   * @param cookie Your cookie for netease cloud music.
   * @return A map which the key is the song's id and the value is the url of songs with {@code ids}
   *     and quality {@code level}.
   * @apiNote GET /song/url/v1?id={@code ids}&level={@code level}
   */
  @Override
  public Map<String, String> getSongsLink(String ids, String level, String cookie) {
    String baseUrl = httpTools.ncmHost + ":" + httpTools.ncmPort;
    return extractSongsLinks(
        httpTools.requestGetAPI(
            baseUrl + NCMAPI.NEW_SONGS_LINKS,
            new HashMap<String, String>() {
              {
                put("id", ids);
                put("level", level);
              }
            },
            Optional.of(cookie)));
  }

  private Map<String, String> extractSongsLinks(String rawSongsLinks) {
    HashMap<String, String> links = new HashMap<>();
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(rawSongsLinks);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    JsonNode dataNode = jsonNode.get("data");
    for (JsonNode linkNode : dataNode) {
      String url = linkNode.get("url").textValue();
      links.put(linkNode.get("id").asText(), url == null ? "" : url);
    }
    return links;
  }

  /**
   * Search and return paged songs according to the given keywords {@code name}.
   *
   * @param keywords Your search keywords.
   * @param offset Offset from the first result.
   * @param limit Number of songs returned.
   * @param type Search type, 1 for song, 10 for album, 100 for singers, 1000 for playlists, 1002
   *     for user, 1004 for MV, 1006 for lyrics, 1009 for podcasts, 1014 for videos, 1018 for misc,
   *     2000 for voice.
   * @param cookie Your cookie for netease cloud music.
   * @return A list of paged NCMSong wrapped by NCMSearchSongsPagedResult.
   * @apiNote GET /cloudsearch?keywords=as long as you love me&offset=0&limit=30&type=1
   */
  @Override
  public NCMSearchSongsPagedResult searchResourcesByKeywords(
      String keywords, Integer offset, Integer limit, Integer type, String cookie) {
    String baseUrl = httpTools.ncmHost + ":" + httpTools.ncmPort;
    NCMSearchSongsPagedResult pagedSongs =
        extractSearchedSongs(
            httpTools.requestGetAPI(
                baseUrl + NCMAPI.CLOUDSEARCH,
                new HashMap<String, String>() {
                  {
                    put("keywords", keywords);
                    put("offset", offset.toString());
                    put("limit", limit.toString());
                    put("type", type.toString());
                  }
                },
                Optional.of(cookie)));
    List<NCMSong> songs = pagedSongs.getSongs();
    String ids =
        songs.stream().map(song -> song.getId().toString()).collect(Collectors.joining(","));
    Map<String, String> links = getSongsLink(ids, "standard", cookie);
    for (NCMSong song : songs) {
      song.setSongLink(links.getOrDefault(song.getId().toString(), ""));
      song.setIsTakenDown(song.getSongLink().isEmpty());
    }
    pagedSongs.setPageNo(offset);
    pagedSongs.setPageSize(limit);
    return pagedSongs;
  }

  private NCMSearchSongsPagedResult extractSearchedSongs(String rawSearchedSongs) {
    NCMSearchSongsPagedResult pagedSongs = new NCMSearchSongsPagedResult();
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(rawSearchedSongs);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    JsonNode resultNode = jsonNode.get("result");
    pagedSongs.setTotal(resultNode.get("songCount").intValue());
    JsonNode songsNode = resultNode.get("songs");
    List<NCMSong> songs = new ArrayList<>();
    for (JsonNode songNode : songsNode) {
      NCMSong song = new NCMSong();
      song.setId(songNode.get("id").longValue());
      song.setMvId(songNode.get("mv").longValue());
      song.setName(songNode.get("name").textValue());
      List<BasicSinger> singers = new ArrayList<>();
      JsonNode singersNode = songNode.get("ar");
      for (JsonNode singerNode : singersNode) {
        NCMSinger singer = new NCMSinger();
        singer.setId(singerNode.get("id").longValue());
        singer.setName(singerNode.get("name").textValue());
        singers.add(singer);
      }
      song.setSingers(singers);
      song.setCover(songNode.get("al").get("picUrl").textValue());
      song.setPayPlay(songNode.get("fee").intValue());
      songs.add(song);
    }
    pagedSongs.setSongs(songs);
    return pagedSongs;
  }
}
