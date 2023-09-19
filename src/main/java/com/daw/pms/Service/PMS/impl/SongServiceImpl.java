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
import com.daw.pms.Service.BiliBili.BiliResourceService;
import com.daw.pms.Service.NeteaseCloudMusic.NCMSongService;
import com.daw.pms.Service.PMS.SongService;
import com.daw.pms.Service.QQMusic.QQMusicSongService;
import com.daw.pms.Utils.HttpTools;
import com.daw.pms.Utils.PmsUserDetailsUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.Serializable;
import java.util.*;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

/**
 * Service for songs in pms.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 6/3/23
 */
@Service
public class SongServiceImpl implements SongService, Serializable {
  private final HttpTools httpTools;
  private final PmsUserDetailsUtil pmsUserDetailsUtil;
  private final QQMusicSongService qqMusicSongService;
  private final NCMSongService ncmSongService;
  private final BiliResourceService biliResourceService;
  private final SongMapper songMapper;
  private final SingerMapper singerMapper;

  /**
   * Constructor for SongServiceImpl.
   *
   * @param httpTools a {@link com.daw.pms.Utils.HttpTools} object.
   * @param pmsUserDetailsUtil a {@link com.daw.pms.Utils.PmsUserDetailsUtil} object.
   * @param qqMusicSongService a {@link com.daw.pms.Service.QQMusic.QQMusicSongService} object.
   * @param ncmSongService a {@link com.daw.pms.Service.NeteaseCloudMusic.NCMSongService} object.
   * @param biliResourceService a {@link com.daw.pms.Service.BiliBili.BiliResourceService} object.
   * @param songMapper a {@link com.daw.pms.Mapper.SongMapper} object.
   * @param singerMapper a {@link com.daw.pms.Mapper.SingerMapper} object.
   */
  public SongServiceImpl(
      HttpTools httpTools,
      PmsUserDetailsUtil pmsUserDetailsUtil,
      QQMusicSongService qqMusicSongService,
      NCMSongService ncmSongService,
      BiliResourceService biliResourceService,
      SongMapper songMapper,
      SingerMapper singerMapper) {
    this.httpTools = httpTools;
    this.pmsUserDetailsUtil = pmsUserDetailsUtil;
    this.qqMusicSongService = qqMusicSongService;
    this.ncmSongService = ncmSongService;
    this.biliResourceService = biliResourceService;
    this.songMapper = songMapper;
    this.singerMapper = singerMapper;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Get the detail information of the song.
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
      result =
          qqMusicSongService.getDetailSong(
              ids, pmsUserDetailsUtil.getCurrentLoginUserQqMusicCookie());
    } else if (platform == 2) {
      result = ncmSongService.getDetailSong(ids, pmsUserDetailsUtil.getCurrentLoginUserNcmCookie());
    } else if (platform == 3) {
      result =
          biliResourceService.getDetailResource(
              ids, pmsUserDetailsUtil.getCurrentLoginUserBiliCookie());
    } else {
      throw new RuntimeException("Invalid platform");
    }
    return result;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Return a list of similar song with {@code songId}.
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
            new ArrayList<>(
                qqMusicSongService.getSimilarSongs(
                    qqmusicSongId, pmsUserDetailsUtil.getCurrentLoginUserQqMusicCookie()));
      } else if (songType == 2) {
        Map<String, Object> ncmSong = songMapper.getNCMSong(Long.valueOf(songId));
        Long ncmId = Long.valueOf(ncmSong.get("ncmId").toString());
        similarSongs =
            new ArrayList<>(
                ncmSongService.getSimilarSongs(
                    ncmId, pmsUserDetailsUtil.getCurrentLoginUserNcmCookie()));
      } else {
        throw new RuntimeException("Invalid song type");
      }
    } else if (platform == 1) {
      similarSongs =
          new ArrayList<>(
              qqMusicSongService.getSimilarSongs(
                  songId, pmsUserDetailsUtil.getCurrentLoginUserQqMusicCookie()));
    } else if (platform == 2) {
      similarSongs =
          new ArrayList<>(
              ncmSongService.getSimilarSongs(
                  Long.valueOf(songId), pmsUserDetailsUtil.getCurrentLoginUserNcmCookie()));
    } else if (platform == 3) {
      throw new RuntimeException("Not yet implement bilibili platform.");
    } else {
      throw new RuntimeException("Invalid platform");
    }
    return similarSongs;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Get the lyrics of the song with {@code id}.
   */
  @Override
  public BasicLyrics getLyrics(Long id, Integer platform) {
    BasicLyrics lyrics;
    if (platform == 0) {
      throw new RuntimeException("Not yet implement pms platform.");
    } else if (platform == 1) {
      lyrics =
          qqMusicSongService.getLyrics(
              id.toString(), pmsUserDetailsUtil.getCurrentLoginUserQqMusicCookie());
    } else if (platform == 2) {
      lyrics = ncmSongService.getLyrics(id, pmsUserDetailsUtil.getCurrentLoginUserNcmCookie());
    } else if (platform == 3) {
      throw new RuntimeException("Not yet implement bilibili platform.");
    } else {
      throw new RuntimeException("Invalid platform");
    }
    return lyrics;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Get the songs' links.
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
        Result rawSongsResult =
            qqMusicSongService.getSongsLink(
                songMid, pmsUserDetailsUtil.getCurrentLoginUserQqMusicCookie());
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
        Result rawSongsResult =
            ncmSongService.getSongsLink(
                originalSongId, level, pmsUserDetailsUtil.getCurrentLoginUserNcmCookie());
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
        String response =
            httpTools.requestGetAPIByFinalUrl(
                url,
                new HttpHeaders(),
                Optional.ofNullable(pmsUserDetailsUtil.getCurrentLoginUserBiliCookie()));
        Long cid = extractCid(response);
        Result rawResourcesResult =
            biliResourceService.getResourceDashLink(
                bvid, cid, pmsUserDetailsUtil.getCurrentLoginUserBiliCookie());
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
      result =
          qqMusicSongService.getSongsLink(
              ids, pmsUserDetailsUtil.getCurrentLoginUserQqMusicCookie());
    } else if (platform == 2) {
      result =
          ncmSongService.getSongsLink(
              ids, level, pmsUserDetailsUtil.getCurrentLoginUserNcmCookie());
    } else if (platform == 3) {
      result =
          biliResourceService.getResourceDashLink(
              ids.split(":")[0],
              ids.split(":")[1] != null ? Long.valueOf(ids.split(":")[1]) : null,
              pmsUserDetailsUtil.getCurrentLoginUserBiliCookie());
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
   * {@inheritDoc}
   *
   * <p>Search resources of type {@code type} by {@code keyword}.
   */
  @Override
  public Result searchResourcesByKeyword(
      String keyword, Integer pageNo, Integer pageSize, Integer type, Integer platform) {
    Result result;
    if (platform == 0) {
      throw new RuntimeException("Not yet implement pms platform.");
    } else if (platform == 1) {
      result =
          qqMusicSongService.searchSongsByKeyword(
              keyword, pageNo, pageSize, pmsUserDetailsUtil.getCurrentLoginUserQqMusicCookie());
    } else if (platform == 2) {
      result =
          ncmSongService.searchResourcesByKeyword(
              keyword, pageNo, pageSize, type, pmsUserDetailsUtil.getCurrentLoginUserNcmCookie());
    } else if (platform == 3) {
      result =
          biliResourceService.searchResources(
              "video",
              keyword,
              "totalrank",
              0,
              0,
              pageNo,
              pmsUserDetailsUtil.getCurrentLoginUserBiliCookie());
    } else {
      throw new RuntimeException("Invalid platform");
    }
    return result;
  }
}
