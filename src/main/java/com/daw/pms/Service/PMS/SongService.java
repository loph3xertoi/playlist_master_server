package com.daw.pms.Service.PMS;

import com.daw.pms.Entity.Basic.BasicSong;
import java.util.List;
import java.util.Map;

/**
 * Service for songs in pms.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 6/3/23
 */
public interface SongService {
  /**
   * Get the detail information of the song.
   *
   * @param songMid The song mid.
   * @param platform The platform this song belongs to.
   * @return Detail song.
   */
  BasicSong getDetailSong(String songMid, Integer platform);

  /**
   * Return a list of similar song with {@code songId}.
   *
   * @param songId The song id.
   * @param platform The platform the song belongs to.
   * @return A list of similar songs with {@code songId}.
   */
  List<BasicSong> getSimilarSongs(String songId, Integer platform);

  String getSongLink(String songMid, String type, String mediaMid, Integer platform);

  Map<String, String> getSongsLink(String songMids, Integer platform);
}
