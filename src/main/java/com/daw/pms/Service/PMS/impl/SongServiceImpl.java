package com.daw.pms.Service.PMS.impl;

import com.daw.pms.DTO.Result;
import com.daw.pms.Entity.Basic.BasicLyrics;
import com.daw.pms.Entity.Basic.BasicSong;
import com.daw.pms.Service.Bilibili.BiliResourceService;
import com.daw.pms.Service.NeteaseCloudMusic.NCMSongService;
import com.daw.pms.Service.PMS.SongService;
import com.daw.pms.Service.QQMusic.QQMusicSongService;
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

  private final QQMusicSongService qqMusicSongService;
  private final NCMSongService ncmSongService;
  private final BiliResourceService biliResourceService;

  public SongServiceImpl(
      QQMusicSongService qqMusicSongService,
      NCMSongService ncmSongService,
      BiliResourceService biliResourceService) {
    this.qqMusicSongService = qqMusicSongService;
    this.ncmSongService = ncmSongService;
    this.biliResourceService = biliResourceService;
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
      throw new RuntimeException("Not yet implement pms platform.");
    } else if (platform == 1) {
      result = qqMusicSongService.getDetailSong(ids, qqMusicCookie);
    } else if (platform == 2) {
      result = ncmSongService.getDetailSong(ids, ncmCookie);
    } else if (platform == 3) {
      result = biliResourceService.getDetailResource(ids, biliCookie);
    } else {
      throw new RuntimeException("Invalid platform.");
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
      throw new RuntimeException("Invalid platform.");
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
      throw new RuntimeException("Invalid platform.");
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
   *     Map<String,String>.
   */
  @Override
  public Result getSongsLink(String ids, String level, Integer platform) {
    Result result;
    if (platform == 0) {
      throw new RuntimeException("Not yet implement pms platform.");
    } else if (platform == 1) {
      result = qqMusicSongService.getSongsLink(ids, qqMusicCookie);
    } else if (platform == 2) {
      result = ncmSongService.getSongsLink(ids, level, ncmCookie);
    } else if (platform == 3) {
      result =
          biliResourceService.getResourceDashLink(
              ids.split(":")[0],
              ids.split(":")[1] != null ? Long.valueOf(ids.split(":")[1]) : null,
              ncmCookie);
    } else {
      throw new RuntimeException("Invalid platform.");
    }
    return result;
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
      throw new RuntimeException("Invalid platform.");
    }
    return result;
  }
}
