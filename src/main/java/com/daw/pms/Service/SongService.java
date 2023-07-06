package com.daw.pms.Service;

import com.daw.pms.Entity.QQMusicBasicSong;
import com.daw.pms.Entity.QQMusicSong;
import java.util.List;

/**
 * Service for songs in pm server.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 6/3/23
 */
public interface SongService {
  QQMusicSong getDetailSong(String songMid, Integer platformId);

  List<QQMusicBasicSong> getSimilarSongs(String songId, Integer platformId);

  String getSongLink(String songMid, String type, String mediaMid, Integer platformId);
}
