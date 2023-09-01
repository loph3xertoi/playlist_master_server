package com.daw.pms.Service.PMS.impl;

import com.daw.pms.DTO.BiliLinksDTO;
import com.daw.pms.DTO.Result;
import com.daw.pms.Entity.Basic.BasicLyrics;
import com.daw.pms.Entity.Basic.BasicSong;
import com.daw.pms.Entity.Bilibili.BiliDetailResource;
import com.daw.pms.Entity.NeteaseCloudMusic.NCMDetailSong;
import com.daw.pms.Entity.PMS.PMSDetailSong;
import com.daw.pms.Entity.PMS.PMSSinger;
import com.daw.pms.Entity.PMS.PMSSong;
import com.daw.pms.Entity.QQMusic.QQMusicDetailSong;
import com.daw.pms.Mapper.SingerMapper;
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
  private final SingerMapper singerMapper;

  public SongServiceImpl(
      HttpTools httpTools,
      QQMusicSongService qqMusicSongService,
      NCMSongService ncmSongService,
      BiliResourceService biliResourceService,
      SongMapper songMapper,
      SingerMapper singerMapper) {
    this.httpTools = httpTools;
    this.qqMusicSongService = qqMusicSongService;
    this.ncmSongService = ncmSongService;
    this.biliResourceService = biliResourceService;
    this.songMapper = songMapper;
    this.singerMapper = singerMapper;
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
      PMSDetailSong detailSong = new PMSDetailSong(song);
      Result linksResult = getSongsLink(ids, "standard", 0);
      int type = song.getType();
      if (linksResult.getSuccess()) {
        String songLink = String.valueOf(linksResult.getData());
        detailSong.setSongLink(songLink);
      } else {
        throw new RuntimeException("Fail to get detail pms song: " + linksResult.getMessage());
      }
      List<PMSSinger> singers = singerMapper.getAllSingersBySongId(song.getId());
      detailSong.setSingers(singers);
      // Set basic song or bilibili resource.
      if (type == 1) {
        Map<String, Object> qqMusicSongMap = songMapper.getQQMusicSong(Long.valueOf(ids));
        String qqMusicSongMid = String.valueOf(qqMusicSongMap.get("songMid"));
        Result qqmusicDetailSongResult = getDetailSong(qqMusicSongMid, 1);
        QQMusicDetailSong qqmusicDetailSong = (QQMusicDetailSong) qqmusicDetailSongResult.getData();
        detailSong.setBasicSong(qqmusicDetailSong);
      } else if (type == 2) {
        Map<String, Object> ncmSongMap = songMapper.getNCMSong(Long.valueOf(ids));
        String ncmSongId = String.valueOf(ncmSongMap.get("ncmId"));
        Result ncmDetailSongResult = getDetailSong(ncmSongId, 2);
        NCMDetailSong ncmDetailSong = (NCMDetailSong) ncmDetailSongResult.getData();
        detailSong.setBasicSong(ncmDetailSong);
      } else if (type == 3) {
        Map<String, Object> biliResourceMap = songMapper.getBiliResource(Long.valueOf(ids));
        String biliResourceBvid = String.valueOf(biliResourceMap.get("bvid"));
        Result biliDetailResourceResult = getDetailSong(biliResourceBvid, 3);
        BiliDetailResource biliDetailResource =
            (BiliDetailResource) biliDetailResourceResult.getData();
        detailSong.setBiliResource(biliDetailResource);
      } else {
        throw new RuntimeException("Unsupported song type");
      }
      result = Result.ok(detailSong);
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
   * @param songType The pms song type, only used in pms platform.
   * @param platform The platform the song belongs to.
   * @return A list of similar songs with {@code songId}.
   */
  @Override
  public List<? extends BasicSong> getSimilarSongs(
      String songId, Integer songType, Integer platform) {
    List<? extends BasicSong> similarSongs;
    if (platform == 0) {
      if (songType == 1) {
        Map<String, Object> qqMusicSong = songMapper.getQQMusicSong(Long.valueOf(songId));
        String qqmusicSongId = qqMusicSong.get("songId").toString();
        similarSongs =
            new ArrayList<>(qqMusicSongService.getSimilarSongs(qqmusicSongId, qqMusicCookie));
      } else if (songType == 2) {
        Map<String, Object> ncmSong = songMapper.getNCMSong(Long.valueOf(songId));
        Long ncmId = Long.valueOf(ncmSong.get("ncmId").toString());
        similarSongs = new ArrayList<>(ncmSongService.getSimilarSongs(ncmId, ncmCookie));
      } else {
        throw new RuntimeException("Invalid song type");
      }
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
   *     Map<String,String> (qqmusic and ncm platform) or BiliLinksDTO (bilibili platform), single
   *     song link in pms platform.
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
      int type = song.getType();
      if (type == 1) {
        Map<String, Object> qqMusicSong = songMapper.getQQMusicSong(songId);
        String songMid = String.valueOf(qqMusicSong.get("songMid"));
        Result rawSongsResult = qqMusicSongService.getSongsLink(songMid, qqMusicCookie);
        HashMap<String, String> songLinksMap = (HashMap<String, String>) rawSongsResult.getData();
        String songLink = songLinksMap.get(songMid);
        if (songLink != null) {
          result = Result.ok(songLink);
        } else {
          result = Result.fail("Fail to get qqmusic song link");
        }
      } else if (type == 2) {
        Map<String, Object> ncmSong = songMapper.getNCMSong(songId);
        String originalSongId = String.valueOf(ncmSong.get("ncmId"));
        Result rawSongsResult = ncmSongService.getSongsLink(originalSongId, level, ncmCookie);
        String songLink = rawSongsResult.getData().toString().split("=")[1];
        if (songLink != null) {
          songLink = songLink.substring(0, songLink.length() - 1);
          result = Result.ok(songLink);
        } else {
          result = Result.fail("Fail to get ncm song link");
        }
      } else if (type == 3) {
        Map<String, Object> biliResource = songMapper.getBiliResource(songId);
        String aid = String.valueOf(biliResource.get("aid"));
        String bvid = String.valueOf(biliResource.get("bvid"));
        String url = "https://api.bilibili.com/x/player/pagelist?bvid=" + bvid;
        String response = httpTools.requestGetAPIByFinalUrl(url, Optional.ofNullable(biliCookie));
        Long cid = extractCid(response);
        Result rawResourcesResult = biliResourceService.getResourceDashLink(bvid, cid, biliCookie);
        Map<String, String> audiosMap = ((BiliLinksDTO) rawResourcesResult.getData()).getAudio();
        int maxKey = Integer.MIN_VALUE;
        for (String str : audiosMap.keySet()) {
          int value = Integer.parseInt(str);
          if (value > maxKey) {
            maxKey = value;
          }
        }
        String resourceLink = audiosMap.get(String.valueOf(maxKey));
        if (resourceLink != null) {
          result = Result.ok(resourceLink);
        } else {
          result = Result.fail("Fail to get bilibili resource link");
        }
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
