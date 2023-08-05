package com.daw.pms.Service.QQMusic;

import com.daw.pms.DTO.Result;
import com.daw.pms.Entity.QQMusic.QQMusicLyrics;
import com.daw.pms.Entity.QQMusic.QQMusicSearchSongPagedResult;
import com.daw.pms.Entity.QQMusic.QQMusicSong;
import java.util.List;

/**
 * Service for handle songs of qq music.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 6/1/23
 */
public interface QQMusicSongService {
  /**
   * Get songs id from playlist {@code dirId}.
   *
   * @param dirId Playlist's dirId.
   * @param cookie Your qq music cookie.
   * @return A list of songId for your playlist with {@code dirId}.
   * @apiNote GET /playlist/map?dirid={@code dirId}
   */
  List<String> getSongsIdFromPlaylist(String dirId, String cookie);

  /**
   * Get detail song with mid {@code songMid}.
   *
   * @param songMid The mid of this song.
   * @param cookie Your qq music cookie.
   * @return Detail song with {@code songMid}, wrapped with Result DTO, the data is
   *     QQMusicDetailSong.
   * @apiNote GET /song?songmid={@code songMid}
   */
  Result getDetailSong(String songMid, String cookie);

  /**
   * Get the similar songs according to {@code songId}.
   *
   * @param songId The id of song.
   * @param cookie Your qq music cookie.
   * @return A list of songs that is similar to song {@code songId}.
   * @apiNote GET /song/similar?id={@code songId}
   */
  List<QQMusicSong> getSimilarSongs(String songId, String cookie);

  /**
   * Get the lyrics of the song with {@code songMid}.
   *
   * @param songMid The mid of song.
   * @param cookie Your qq music cookie.
   * @return Lyrics of your song in QQMusicLyrics.
   * @apiNote GET /lyric?songmid={@code songMid}
   */
  QQMusicLyrics getLyrics(String songMid, String cookie);

  /**
   * Get the cover uri of the song/album with {@code albumMid}.
   *
   * @param albumMid The albumMid of song.
   * @param cookie Your qq music cookie.
   * @return Cover uri of your song.
   * @apiNote GET /album?albummid={@code albumMid}
   */
  String getSongCoverUri(String albumMid, String cookie);

  /**
   * Get the url of song with songMid {@code songMid} and mediaMid {@code mediaMid} and type {@code
   * type}.
   *
   * @param songMid The song mid.
   * @param type The quality(128, 320, flac, m4a, ogg) of song you want to get.
   * @param mediaMid The mediaMid of song.
   * @param cookie Your qq music cookie.
   * @return The url of your song with mid {@code songMid} and mediaMid {@code mediaMid} and type
   *     {@code type}
   * @apiNote GET /song/url?id={@code songMid}&type={@code type}&mediaId={@code mediaMid}
   */
  String getSongLink(String songMid, String type, String mediaMid, String cookie);

  /**
   * Get the url of songs with songMids {@code songMids}.
   *
   * @param songMids The songMid, separated with comma.
   * @param cookie Your qq music cookie.
   * @return The urls of your songs with mid {@code songMids}, wrapped with Result DTO, the data is
   *     Map<String, String>.
   * @apiNote GET /song/urls?id={@code songMids}
   */
  Result getSongsLink(String songMids, String cookie);

  /**
   * Search and return paged songs according to the given keyword {@code name}.
   *
   * @param name The search keyword.
   * @param pageNo Page order.
   * @param pageSize Size one page.
   * @param cookie Your qq music cookie.
   * @return A list of paged QQMusicSong wrapped by QQMusicSearchSongPagedResult.
   * @apiNote GET /search?key={@code name}&pageNo={@code pageNo}&pageSize={@code pageSize}
   */
  QQMusicSearchSongPagedResult searchResourcesByKeywords(
      String name, Integer pageNo, Integer pageSize, String cookie);
}
