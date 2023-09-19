package com.daw.pms.Service.NeteaseCloudMusic.impl;

import com.daw.pms.Config.NCMAPI;
import com.daw.pms.DTO.PagedDataDTO;
import com.daw.pms.DTO.Result;
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

/**
 * Service for handle songs of netease cloud music.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 7/21/23
 */
@Service
public class NCMSongServiceImpl implements NCMSongService {

  private final HttpTools httpTools;
  private final String baseUrl;

  /**
   * Constructor for NCMSongServiceImpl.
   *
   * @param httpTools a {@link com.daw.pms.Utils.HttpTools} object.
   */
  public NCMSongServiceImpl(HttpTools httpTools) {
    this.httpTools = httpTools;
    this.baseUrl = httpTools.ncmHost + ":" + httpTools.ncmPort;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Get detail song with {@code ids}.
   *
   * @apiNote GET /song/detail?ids={@code ids}
   */
  @Override
  public Result getDetailSong(String ids, String cookie) {
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
    List<? extends BasicSinger> singers = detailSong.getSingers();
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
    Map<String, String> links;
    Result linksResult = getSongsLink(ids, "standard", cookie);
    if (linksResult.getSuccess()) {
      links = (Map<String, String>) linksResult.getData();
    } else {
      throw new RuntimeException(linksResult.getMessage());
    }
    detailSong.setSongLink(links.getOrDefault(detailSong.getId().toString(), ""));
    detailSong.setIsTakenDown(detailSong.getSongLink().isEmpty());
    return Result.ok(detailSong);
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
    detailSong.setHBr(songNode.get("h").isNull() ? 0 : songNode.get("h").get("br").intValue());
    detailSong.setHSize(songNode.get("h").isNull() ? 0 : songNode.get("h").get("size").longValue());
    detailSong.setMBr(songNode.get("m").isNull() ? 0 : songNode.get("m").get("br").intValue());
    detailSong.setMSize(songNode.get("m").isNull() ? 0 : songNode.get("m").get("size").longValue());
    detailSong.setLBr(songNode.get("l").isNull() ? 0 : songNode.get("l").get("br").intValue());
    detailSong.setLSize(songNode.get("l").isNull() ? 0 : songNode.get("l").get("size").longValue());
    detailSong.setSqBr(songNode.get("sq").isNull() ? 0 : songNode.get("sq").get("br").intValue());
    detailSong.setSqSize(
        songNode.get("sq").isNull() ? 0 : songNode.get("sq").get("size").longValue());
    return detailSong;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Get the similar songs of song {@code id}.
   *
   * @apiNote GET /simi/song?id={@code id}
   */
  @Override
  public List<NCMSong> getSimilarSongs(Long id, String cookie) {
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
    Map<String, String> links;
    Result linksResult = getSongsLink(ids, "standard", cookie);
    if (linksResult.getSuccess()) {
      links = (Map<String, String>) linksResult.getData();
    } else {
      throw new RuntimeException(linksResult.getMessage());
    }
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
   * {@inheritDoc}
   *
   * <p>Get the lyrics of the song with {@code id}.
   *
   * @apiNote GET /lyric/new?id={@code id}
   */
  @Override
  public NCMLyrics getLyrics(Long id, String cookie) {
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
   * {@inheritDoc}
   *
   * <p>Get the url of songs with {@code ids} and quality {@code level}.
   *
   * @apiNote GET /song/url/v1?id={@code ids}&amp;level={@code level}
   */
  @Override
  public Result getSongsLink(String ids, String level, String cookie) {
    Map<String, String> links =
        extractSongsLinks(
            httpTools.requestGetAPI(
                baseUrl + NCMAPI.NEW_SONGS_LINKS,
                new HashMap<String, String>() {
                  {
                    put("id", ids);
                    put("level", level);
                  }
                },
                Optional.of(cookie)));
    return Result.ok(links);
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
   * {@inheritDoc}
   *
   * <p>Search and return paged songs/resources according to the given keyword {@code name}.
   *
   * @apiNote GET /cloudsearch?keywords=as long as you love me&amp;offset=0&amp;limit=30&amp;type=1
   */
  @Override
  public Result searchResourcesByKeyword(
      String keyword, Integer pageNo, Integer pageSize, Integer type, String cookie) {
    String offset = String.valueOf((pageNo - 1) * pageSize);
    String limit = String.valueOf(pageSize);
    Result result =
        extractSearchedSongs(
            httpTools.requestGetAPI(
                baseUrl + NCMAPI.CLOUDSEARCH,
                new HashMap<String, String>() {
                  {
                    put("keywords", keyword);
                    put("offset", offset);
                    put("limit", limit);
                    put("type", type.toString());
                  }
                },
                Optional.of(cookie)));
    if (!result.getSuccess()) {
      return result;
    }
    PagedDataDTO<NCMSong> pagedDataDTO = (PagedDataDTO<NCMSong>) result.getData();
    int count = pagedDataDTO.getCount();
    Boolean hasMore = pageNo * pageSize < count;
    pagedDataDTO.setHasMore(hasMore);
    List<NCMSong> songs = pagedDataDTO.getList();
    String ids =
        songs.stream().map(song -> song.getId().toString()).collect(Collectors.joining(","));
    Map<String, String> links;
    Result linksResult = getSongsLink(ids, "standard", cookie);
    if (linksResult.getSuccess()) {
      links = (Map<String, String>) linksResult.getData();
    } else {
      throw new RuntimeException(linksResult.getMessage());
    }
    for (NCMSong song : songs) {
      song.setSongLink(links.getOrDefault(song.getId().toString(), ""));
      song.setIsTakenDown(song.getSongLink().isEmpty());
    }
    return result;
  }

  private Result extractSearchedSongs(String rawSearchedSongs) {
    PagedDataDTO<NCMSong> pagedSongs = new PagedDataDTO<>();
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(rawSearchedSongs);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    int resultCode = jsonNode.get("code").intValue();
    if (resultCode != 200) {
      throw new RuntimeException(String.valueOf(resultCode));
    }
    JsonNode resultNode = jsonNode.get("result");
    pagedSongs.setCount(resultNode.get("songCount").intValue());
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
    pagedSongs.setList(songs);
    return Result.ok(pagedSongs);
  }
}
