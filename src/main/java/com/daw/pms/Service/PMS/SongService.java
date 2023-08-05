package com.daw.pms.Service.PMS;

import com.daw.pms.DTO.Result;
import com.daw.pms.Entity.Basic.BasicLyrics;
import com.daw.pms.Entity.Basic.BasicPagedSongs;
import com.daw.pms.Entity.Basic.BasicSong;
import java.util.List;

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
   * @param ids The song mid.
   * @param platform The platform this song belongs to.
   * @return Detail song, wrapped with Result DTO, the data is subclass of BasicSong.
   */
  Result getDetailSong(String ids, Integer platform);

  /**
   * Return a list of similar song with {@code songId}.
   *
   * @param songId The song id.
   * @param platform The platform the song belongs to.
   * @return A list of similar songs with {@code songId}.
   */
  List<BasicSong> getSimilarSongs(String songId, Integer platform);

  /**
   * Get the lyrics of the song with {@code id}.
   *
   * @param id The id of song.
   * @param platform The platform this song belongs to.
   * @return Lyrics of your song in netease cloud music.
   */
  BasicLyrics getLyrics(Long id, Integer platform);

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
  Result getSongsLink(String ids, String level, Integer platform);

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
  BasicPagedSongs searchResourcesByKeywords(
      String keywords, Integer offset, Integer limit, Integer type, Integer platform);
}
