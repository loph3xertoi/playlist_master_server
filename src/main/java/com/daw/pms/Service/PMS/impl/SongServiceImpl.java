package com.daw.pms.Service.PMS.impl;

import com.daw.pms.DTO.Result;
import com.daw.pms.Entity.Basic.BasicLyrics;
import com.daw.pms.Entity.Basic.BasicSong;
import com.daw.pms.Entity.PMS.PMSSong;
import com.daw.pms.Mapper.SongMapper;
import com.daw.pms.Service.Bilibili.BiliResourceService;
import com.daw.pms.Service.NeteaseCloudMusic.NCMSongService;
import com.daw.pms.Service.PMS.SongService;
import com.daw.pms.Service.QQMusic.QQMusicSongService;
import com.daw.pms.Utils.HttpTools;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.Serializable;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SongServiceImpl implements SongService, Serializable {
  @Value("${qqmusic.cookie}")
  private String qqMusicCookie;

  @Value("${ncm.cookie}")
  private String ncmCookie;

  @Value("${bilibili.cookie}")
  private String biliCookie;

  private final HttpTools httpTools;
  private final QQMusicSongService qqMusicSongService;
  private final NCMSongService ncmSongService;
  private final BiliResourceService biliResourceService;
  private final SongMapper songMapper;

  public SongServiceImpl(
      HttpTools httpTools,
      QQMusicSongService qqMusicSongService,
      NCMSongService ncmSongService,
      BiliResourceService biliResourceService,
      SongMapper songMapper) {
    this.httpTools = httpTools;
    this.qqMusicSongService = qqMusicSongService;
    this.ncmSongService = ncmSongService;
    this.biliResourceService = biliResourceService;
    this.songMapper = songMapper;
  }

  /**
   * Get the detail information of the song.
   *
   * @param ids The song mid.
   * @param platform The platform this song belongs to.
   * @return Detail song, wrapped with Result DTO, the data is subclass of BasicSong.
   */
  @Override
  public Result getDetailSong(String ids, Integer platform) {
    Result result;
    if (platform == 0) {
      PMSSong song = songMapper.getSong(Long.valueOf(ids));
      Result linksResult = getSongsLink(ids, "standard", 0);
      Integer type = song.getType();
      if (linksResult.getSuccess()) {
        Object data = linksResult.getData();
        if (type == 1) {
          //          Map<String, String> links = (Map<String, String>) linksResult.getData();
          return Result.ok();
        } else if (type == 2) {
          //          Map<String, String> links = (Map<String, String>) linksResult.getData();
          return Result.ok();
        } else if (type == 3) {
          //          Map<String, String> links = (Map<String, String>) linksResult.getData();
          return Result.ok();
        } else {
          throw new RuntimeException("Invalid song type");
        }
      } else {
        throw new RuntimeException("Fail to get detail pms song: " + linksResult.getMessage());
      }
    } else if (platform == 1) {
      result = qqMusicSongService.getDetailSong(ids, qqMusicCookie);
    } else if (platform == 2) {
      result = ncmSongService.getDetailSong(ids, ncmCookie);
    } else if (platform == 3) {
      result = biliResourceService.getDetailResource(ids, biliCookie);
    } else {
      throw new RuntimeException("Invalid platform");
    }
    return result;
  }

  /**
   * Return a list of similar song with {@code songId}.
   *
   * @param songId The song id.
   * @param platform The platform the song belongs to.
   * @return A list of similar songs with {@code songId}.
   */
  @Override
  public List<BasicSong> getSimilarSongs(String songId, Integer platform) {
    List<BasicSong> similarSongs;
    if (platform == 0) {
      throw new RuntimeException("Not yet implement pms platform.");
    } else if (platform == 1) {
      similarSongs = new ArrayList<>(qqMusicSongService.getSimilarSongs(songId, qqMusicCookie));
    } else if (platform == 2) {
      similarSongs =
          new ArrayList<>(ncmSongService.getSimilarSongs(Long.valueOf(songId), ncmCookie));
    } else if (platform == 3) {
      throw new RuntimeException("Not yet implement bilibili platform.");
    } else {
      throw new RuntimeException("Invalid platform");
    }
    return similarSongs;
  }

  /**
   * Get the lyrics of the song with {@code id}.
   *
   * @param id The id of song.
   * @param platform The platform this song belongs to.
   * @return Lyrics of your song in netease cloud music.
   */
  @Override
  public BasicLyrics getLyrics(Long id, Integer platform) {
    BasicLyrics lyrics;
    if (platform == 0) {
      throw new RuntimeException("Not yet implement pms platform.");
    } else if (platform == 1) {
      lyrics = qqMusicSongService.getLyrics(id.toString(), qqMusicCookie);
    } else if (platform == 2) {
      lyrics = ncmSongService.getLyrics(id, ncmCookie);
    } else if (platform == 3) {
      throw new RuntimeException("Not yet implement bilibili platform.");
    } else {
      throw new RuntimeException("Invalid platform");
    }
    return lyrics;
  }

  /**
   * Get the songs' links.
   *
   * @param ids The song's id, multiple songs id separated with comma, in bilibili, ids == bvid:cid.
   * @param level Quality of song, include standard, higher, exhigh, lossless, hires, jyeffect, sky,
   *     jymaster.
   * @param platform The platform id.
   * @return The urls of your songs with ids {@code ids}, wrapped with Result DTO, the data is
   *     Map<String,String> (qqmusic and ncm platform) or BiliLinksDTO (bilibili platform).
   */
  @Override
  public Result getSongsLink(String ids, String level, Integer platform) {
    Result result;
    if (platform == 0) {
      String[] songsIds = ids.split(",");
      List<Long> songsIdsList = new ArrayList<>(songsIds.length);
      for (String songId : songsIds) {
        songsIdsList.add(Long.valueOf(songId));
      }
      List<PMSSong> songs = songMapper.getSongs(songsIdsList);
      PMSSong song = songs.get(0);
      Long songId = song.getId();
      Integer type = song.getType();
      if (type == 1) {
        Map<String, String> qqMusicSong = songMapper.getQQMusicSong(songId);
        String originalSongId = qqMusicSong.get("songId");
        result = qqMusicSongService.getSongsLink(originalSongId, qqMusicCookie);
      } else if (type == 2) {
        Map<String, String> ncmSong = songMapper.getNCMSong(songId);
        String originalSongId = ncmSong.get("ncmId");
        result = ncmSongService.getSongsLink(originalSongId, level, ncmCookie);
      } else if (type == 3) {
        Map<String, String> biliResource = songMapper.getBiliResource(songId);
        String aid = biliResource.get("aid");
        String bvid = biliResource.get("bvid");
        String url = "https://api.bilibili.com/x/player/pagelist?bvid=" + bvid;
        String response = httpTools.requestGetAPIByFinalUrl(url, Optional.ofNullable(biliCookie));
        Long cid = extractCid(response);
        result = biliResourceService.getResourceDashLink(bvid, cid, biliCookie);
      } else {
        throw new RuntimeException("Invalid song type");
      }

    } else if (platform == 1) {
      result = qqMusicSongService.getSongsLink(ids, qqMusicCookie);
    } else if (platform == 2) {
      result = ncmSongService.getSongsLink(ids, level, ncmCookie);
    } else if (platform == 3) {
      result =
          biliResourceService.getResourceDashLink(
              ids.split(":")[0],
              ids.split(":")[1] != null ? Long.valueOf(ids.split(":")[1]) : null,
              biliCookie);
    } else {
      throw new RuntimeException("Invalid platform");
    }
    return result;
  }

  private Long extractCid(String response) {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(response);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    int code = jsonNode.get("code").intValue();
    if (code == 0) {
      JsonNode dataNode = jsonNode.get("data");
      return dataNode.get(0).get("cid").longValue();
    } else {
      throw new RuntimeException(jsonNode.get("message").textValue());
    }
  }

  /**
   * Search resources of type {@code type} by {@code keyword}.
   *
   * @param keyword The keyword to search.
   * @param pageNo The page number.
   * @param pageSize The page size.
   * @param type The type of the searched resources.
   * @param platform The platform id.
   * @return Searched resources wrapped by Result DTO, the data is PagedDataDTO<T>.
   */
  @Override
  public Result searchResourcesByKeyword(
      String keyword, Integer pageNo, Integer pageSize, Integer type, Integer platform) {
    Result result;
    if (platform == 0) {
      throw new RuntimeException("Not yet implement pms platform.");
    } else if (platform == 1) {
      result = qqMusicSongService.searchSongsByKeyword(keyword, pageNo, pageSize, qqMusicCookie);
    } else if (platform == 2) {
      result = ncmSongService.searchResourcesByKeyword(keyword, pageNo, pageSize, type, ncmCookie);
    } else if (platform == 3) {
      result =
          biliResourceService.searchResources(
              "video", keyword, "totalrank", 0, 0, pageNo, biliCookie);
    } else {
      throw new RuntimeException("Invalid platform");
    }
    return result;
  }
}
