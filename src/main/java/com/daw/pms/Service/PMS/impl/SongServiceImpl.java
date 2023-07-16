package com.daw.pms.Service.PMS.impl;

import com.daw.pms.Entity.Basic.BasicPagedSongs;
import com.daw.pms.Entity.Basic.BasicSong;
import com.daw.pms.Entity.QQMusic.QQMusicDetailSong;
import com.daw.pms.Entity.QQMusic.QQMusicSearchSongPagedResult;
import com.daw.pms.Entity.QQMusic.QQMusicSong;
import com.daw.pms.Service.PMS.SongService;
import com.daw.pms.Service.QQMusic.QQMusicSongService;
import com.daw.pms.Service.QQMusic.impl.QQMusicCookieServiceImpl;
import java.io.Serializable;
import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class SongServiceImpl implements SongService, Serializable {
  private final QQMusicSongService qqMusicSongService;
  private final QQMusicCookieServiceImpl qqMusicCookieService;

  public SongServiceImpl(
      QQMusicSongService qqMusicSongService, QQMusicCookieServiceImpl qqMusicCookieService) {
    this.qqMusicSongService = qqMusicSongService;
    this.qqMusicCookieService = qqMusicCookieService;
  }

  /**
   * Get the detail information of the song.
   *
   * @param songMid The song mid.
   * @param platform The platform this song belongs to.
   * @return Detail song.
   */
  @Override
  public BasicSong getDetailSong(String songMid, Integer platform) {
    BasicSong detailSong = null;
    if (platform == 1) {
      String cookie = qqMusicCookieService.getCookie(1);
      QQMusicDetailSong song = qqMusicSongService.getDetailSong(songMid, cookie);
      String songLink =
          qqMusicSongService.getSongLink(song.getSongMid(), "128", song.getMediaMid(), cookie);
      song.setSongLink(songLink);
      song.setIsTakenDown(songLink.isEmpty());
      detailSong = song;
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
    List<BasicSong> similarSongs = new ArrayList<>();
    if (platform == 1) {
      String cookie = qqMusicCookieService.getCookie(1);
      List<QQMusicSong> songs = qqMusicSongService.getSimilarSongs(songId, cookie);
      // Set the song link.
      List<String> midsList = new ArrayList<>(songs.size());
      for (QQMusicSong song : songs) {
        midsList.add(song.getSongMid());
      }
      String mids = String.join(",", midsList);
      Map<String, String> songsLink = qqMusicSongService.getSongsLink(mids, cookie);
      for (QQMusicSong song : songs) {
        song.setSongLink(songsLink.get(song.getSongMid()));
        song.setIsTakenDown(song.getSongLink().isEmpty());
        similarSongs.add(song);
      }
    }
    return similarSongs;
  }

  /**
   * Get song's link.
   *
   * @param songMid The song mid.
   * @param type The quality(128, 320, flac, m4a, ogg) of song you want to get.
   * @param mediaMid The media mid.
   * @param platform The platform id.
   * @return The url of your song with mid {@code songMid} and mediaMid {@code mediaMid} and type
   *     {@code type}.
   */
  @Override
  public String getSongLink(String songMid, String type, String mediaMid, Integer platform) {
    String songLink = null;
    if (platform == 1) {
      songLink =
          qqMusicSongService.getSongLink(
              songMid, type, mediaMid, qqMusicCookieService.getCookie(1));
    }
    return songLink;
  }

  /**
   * Get the songs' links.
   *
   * @param songMids The song mids.
   * @param platform The platform id.
   * @return The urls of your songs with mid {@code songMids}.
   */
  @Override
  public Map<String, String> getSongsLink(String songMids, Integer platform) {
    Map<String, String> map = new HashMap<>();
    if (platform == 1) {
      map = qqMusicSongService.getSongsLink(songMids, qqMusicCookieService.getCookie(1));
    }
    return map;
  }

  /**
   * Search song by name.
   *
   * @param songName The song name to search.
   * @param pageNo The page number.
   * @param pageSize The page size.
   * @param platform The platform id.
   * @return The search result with page.
   */
  @Override
  public BasicPagedSongs searchSongByName(
      String songName, Integer pageNo, Integer pageSize, Integer platform) {
    BasicPagedSongs pagedSongs = new BasicPagedSongs();
    if (platform == 1) {
      String cookie = qqMusicCookieService.getCookie(1);
      pagedSongs = qqMusicSongService.searchSongByName(songName, pageNo, pageSize, cookie);
      QQMusicSearchSongPagedResult qqMusicSongs = (QQMusicSearchSongPagedResult) pagedSongs;
      // Get all songs' mid for get the song's link in one http request.
      List<String> songMids = new ArrayList<>(qqMusicSongs.getPageSize());
      qqMusicSongs.getSongs().forEach(song -> songMids.add(song.getSongMid()));
      Collections.shuffle(songMids);
      Map<String, String> songsLink =
          qqMusicSongService.getSongsLink(String.join(",", songMids), cookie);
      qqMusicSongs
          .getSongs()
          .forEach(
              song -> {
                song.setSongLink(songsLink.getOrDefault(song.getSongMid(), ""));
                song.setIsTakenDown(song.getSongLink().isEmpty());
              });
    }
    return pagedSongs;
  }
}
