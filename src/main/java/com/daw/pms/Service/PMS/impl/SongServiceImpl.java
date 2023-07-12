package com.daw.pms.Service.PMS.impl;

import com.daw.pms.Entity.Basic.BasicSong;
import com.daw.pms.Entity.QQMusic.QQMusicDetailSong;
import com.daw.pms.Entity.QQMusic.QQMusicSong;
import com.daw.pms.Service.PMS.SongService;
import com.daw.pms.Service.QQMusic.QQMusicSongService;
import com.daw.pms.Service.QQMusic.impl.QQMusicCookieServiceImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class SongServiceImpl implements SongService {
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

  @Override
  public Map<String, String> getSongsLink(String songMids, Integer platform) {
    Map<String, String> map = new HashMap<>();
    if (platform == 1) {
      map = qqMusicSongService.getSongsLink(songMids, qqMusicCookieService.getCookie(1));
    }
    return map;
  }
}
