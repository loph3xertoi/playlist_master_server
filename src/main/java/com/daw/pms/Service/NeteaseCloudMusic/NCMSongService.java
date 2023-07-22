package com.daw.pms.Service.NeteaseCloudMusic;

import com.daw.pms.Entity.NeteaseCloudMusic.NCMDetailSong;
import com.daw.pms.Entity.NeteaseCloudMusic.NCMLyrics;
import com.daw.pms.Entity.NeteaseCloudMusic.NCMSearchSongsPagedResult;
import com.daw.pms.Entity.NeteaseCloudMusic.NCMSong;
import java.util.List;
import java.util.Map;

/**
 * Service for handle songs of netease cloud music.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 7/21/23
 */
public interface NCMSongService {
  /**
   * Get detail song with {@code ids}.
   *
   * @param ids The id of song, multiple songs separated by comma.
   * @param cookie Your cookie for netease cloud music.
   * @return Detail song with {@code ids}.
   * @apiNote GET /song/detail?ids={@code ids}
   */
  NCMDetailSong getDetailSong(String ids, String cookie);

  /**
   * Get the similar songs of song {@code id}.
   *
   * @param id The id of song.
   * @param cookie Your cookie for netease cloud music.
   * @return A list of songs that is similar to song {@code id}.
   * @apiNote GET /simi/song?id={@code id}
   */
  List<NCMSong> getSimilarSongs(Long id, String cookie);

  /**
   * Get the lyrics of the song with {@code id}.
   *
   * @param id The id of song.
   * @param cookie Your cookie for netease cloud music.
   * @return Lyrics of your song in netease cloud music.
   * @apiNote GET /lyric/new?id={@code id}
   */
  NCMLyrics getLyrics(Long id, String cookie);

  /**
   * Get the url of songs with {@code ids} and quality {@code level}.
   *
   * @param ids The song's id, multiple songs separated by comma.
   * @param level Quality of song, include standard, higher, exhigh, lossless, hires, jyeffect, sky,
   *     jymaster.
   * @param cookie Your cookie for netease cloud music.
   * @return A map which the key is the song's id and the value is the url of songs with {@code ids}
   *     and quality {@code level}.
   * @apiNote GET /song/url/v1?id={@code ids}&level={@code level}
   */
  Map<String, String> getSongsLink(String ids, String level, String cookie);

  /**
   * Search and return paged songs according to the given keywords {@code name}.
   *
   * @param keywords Your search keywords.
   * @param offset Offset from the first result.
   * @param limit Number of songs returned.
   * @param type Search type, 1 for song, 10 for album, 100 for singers, 1000 for playlists, 1002
   *     for user, 1004 for MV, 1006 for lyrics, 1009 for podcasts, 1014 for videos, 1018 for misc,
   *     2000 for voice.
   * @param cookie Your cookie for netease cloud music.
   * @return A list of paged NCMSong wrapped by NCMSearchSongsPagedResult.
   * @apiNote GET /cloudsearch?keywords=as long as you love me&offset=0&limit=30&type=1
   */
  NCMSearchSongsPagedResult searchSongs(
      String keywords, Integer offset, Integer limit, Integer type, String cookie);
}
