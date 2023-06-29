package com.daw.pms.Service.impl;

import com.daw.pms.Entity.QQMusicBasicSong;
import com.daw.pms.Entity.QQMusicSong;
import com.daw.pms.Service.QQMusicSongService;
import com.daw.pms.Service.SongService;
import java.util.ArrayList;
import java.util.List;
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
   * @param playlistId The playlist id.
   * @param platformId The platform id.
   * @return A list of QQMusicBasicSong.
   */
  @Override
  public List<QQMusicBasicSong> getBasicSongsFromPlaylist(String playlistId, Integer platformId) {
    List<QQMusicBasicSong> songs = null;
    if (platformId == 1) {
      songs =
          qqMusicSongService.getBasicSongsFromPlaylist(
              playlistId, qqMusicCookieService.getCookie(1));
    }
    return songs;
  }

  @Override
  public QQMusicSong getDetailSong(String songMid, Integer platformId) {
    QQMusicSong detailSong = null;
    if (platformId == 1) {
      detailSong = qqMusicSongService.getDetailSong(songMid, qqMusicCookieService.getCookie(1));
    }
    return detailSong;
  }

  @Override
  public List<QQMusicBasicSong> getSimilarSongs(String songId, Integer platformId) {
    List<QQMusicBasicSong> similarSongs = new ArrayList<>();
    if (platformId == 1) {
      similarSongs = qqMusicSongService.getSimilarSongs(songId, qqMusicCookieService.getCookie(1));
    }
    return similarSongs;
  }

  @Override
  public String getSongLink(String songMid, String type, String mediaId, Integer platformId) {
    String songLink = null;
    if (platformId == 1) {
      songLink =
          qqMusicSongService.getSongLink(songMid, type, mediaId, qqMusicCookieService.getCookie(1));
    }
    return songLink;
  }
}
