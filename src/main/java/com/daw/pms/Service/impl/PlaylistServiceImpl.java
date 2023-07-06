package com.daw.pms.Service.impl;

import com.daw.pms.Entity.QQMusicDetailPlaylist;
import com.daw.pms.Entity.QQMusicPlaylist;
import com.daw.pms.Service.PlaylistService;
import com.daw.pms.Service.QQMusicCookieService;
import com.daw.pms.Service.QQMusicPlaylistService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PlaylistServiceImpl implements PlaylistService {
  private final QQMusicPlaylistService qqMusicPlaylistService;

  private final QQMusicCookieService qqMusicCookieService;

  public PlaylistServiceImpl(
      QQMusicPlaylistService qqMusicPlaylistService, QQMusicCookieService qqMusicCookieService) {
    this.qqMusicPlaylistService = qqMusicPlaylistService;
    this.qqMusicCookieService = qqMusicCookieService;
  }

  /**
   * @param uid User id.
   * @param platformId Platform id(0 for local, 1 for qq music, 2 for netease music, 3 for
   *     bilibili).
   * @return All playlist created by {@code id} in platform {@code platformId}.
   */
  @Override
  public List<QQMusicPlaylist> getPlaylists(String uid, Integer platformId) {
    List<QQMusicPlaylist> qqMusicPlaylists = null;
    if (platformId == 1) {
      qqMusicPlaylists = qqMusicPlaylistService.getPlaylist(uid, qqMusicCookieService.getCookie(1));
    }
    return qqMusicPlaylists;
  }

  /**
   * @param playlistId The playlist id.
   * @param platformId The platform id.
   * @return A list of QQMusicBasicSong.
   */
  @Override
  public QQMusicDetailPlaylist getDetailPlaylist(String playlistId, Integer platformId) {
    QQMusicDetailPlaylist detailPlaylist = null;
    if (platformId == 1) {
      detailPlaylist =
          qqMusicPlaylistService.getDetailPlaylist(playlistId, qqMusicCookieService.getCookie(1));
    }
    return detailPlaylist;
  }
}
