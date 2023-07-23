package com.daw.pms.Service.PMS.impl;

import com.daw.pms.Entity.Basic.BasicLyrics;
import com.daw.pms.Entity.Basic.BasicPagedSongs;
import com.daw.pms.Entity.Basic.BasicSong;
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

  private final QQMusicSongService qqMusicSongService;
  private final NCMSongService ncmSongService;

  public SongServiceImpl(QQMusicSongService qqMusicSongService, NCMSongService ncmSongService) {
    this.qqMusicSongService = qqMusicSongService;
    this.ncmSongService = ncmSongService;
  }

  /**
   * Get the detail information of the song.
   *
   * @param ids The song mid.
   * @param platform The platform this song belongs to.
   * @return Detail song.
   */
  @Override
  public BasicSong getDetailSong(String ids, Integer platform) {
    BasicSong detailSong;
    if (platform == 0) {
      throw new RuntimeException("Not yet implement pms platform.");
    } else if (platform == 1) {
      detailSong = qqMusicSongService.getDetailSong(ids, qqMusicCookie);
    } else if (platform == 2) {
      detailSong = ncmSongService.getDetailSong(ids, ncmCookie);
    } else if (platform == 3) {
      throw new RuntimeException("Not yet implement bilibili platform.");
    } else {
      throw new RuntimeException("Invalid platform.");
    }
    return detailSong;
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
   * @param ids The song's id, multiple songs id separated with comma.
   * @param level Quality of song, include standard, higher, exhigh, lossless, hires, jyeffect, sky,
   *     jymaster.
   * @param platform The platform id.
   * @return The urls of your songs with ids {@code ids}.
   */
  @Override
  public Map<String, String> getSongsLink(String ids, String level, Integer platform) {
    Map<String, String> songsLink;
    if (platform == 0) {
      throw new RuntimeException("Not yet implement pms platform.");
    } else if (platform == 1) {
      songsLink = qqMusicSongService.getSongsLink(ids, qqMusicCookie);
    } else if (platform == 2) {
      songsLink = ncmSongService.getSongsLink(ids, level, ncmCookie);
    } else if (platform == 3) {
      throw new RuntimeException("Not yet implement bilibili platform.");
    } else {
      throw new RuntimeException("Invalid platform.");
    }
    return songsLink;
  }

  /**
   * Search resources of type {@code type} by {@code keywords}.
   *
   * @param keywords The keywords to search.
   * @param offset The offset with the first searched resource.
   * @param limit The mounts of the searched resources.
   * @param type The type of the searched resources.
   * @param platform The platform id.
   * @return Paged searched result.
   */
  @Override
  public BasicPagedSongs searchResourcesByKeywords(
      String keywords, Integer offset, Integer limit, Integer type, Integer platform) {
    BasicPagedSongs searchedResult;
    if (platform == 0) {
      throw new RuntimeException("Not yet implement pms platform.");
    } else if (platform == 1) {
      searchedResult =
          qqMusicSongService.searchResourcesByKeywords(keywords, offset, limit, qqMusicCookie);
    } else if (platform == 2) {
      searchedResult =
          ncmSongService.searchResourcesByKeywords(keywords, offset, limit, type, ncmCookie);
    } else if (platform == 3) {
      throw new RuntimeException("Not yet implement bilibili platform.");
    } else {
      throw new RuntimeException("Invalid platform.");
    }
    return searchedResult;
  }
}
