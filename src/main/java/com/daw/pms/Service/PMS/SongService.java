package com.daw.pms.Service.PMS;

import com.daw.pms.Entity.Basic.BasicPagedSongs;
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
  String getSongLink(String songMid, String type, String mediaMid, Integer platform);

  /**
   * Get the songs' links.
   *
   * @param songMids The song mids.
   * @param platform The platform id.
   * @return The urls of your songs with mid {@code songMids}.
   */
  Map<String, String> getSongsLink(String songMids, Integer platform);

  /**
   * Search song by name.
   *
   * @param songName The song name to search.
   * @param pageNo The page number.
   * @param pageSize The page size.
   * @param platform The platform id.
   * @return The search result with page.
   */
  BasicPagedSongs searchSongByName(
      String songName, Integer pageNo, Integer pageSize, Integer platform);
}
