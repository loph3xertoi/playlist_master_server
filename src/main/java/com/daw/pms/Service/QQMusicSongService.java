package com.daw.pms.Service;

import com.daw.pms.Entity.*;
import java.util.List;
import java.util.Map;

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
   * Get detail of song with mid {@code songMid}.
   *
   * @param songMid The mid of this song.
   * @param cookie Your qq music cookie.
   * @return The QQMusicSong with {@code songMid}.
   * @apiNote GET /song?songmid={@code songMid}
   */
  QQMusicSong getDetailSong(String songMid, String cookie);

  /**
   * Get the similar songs according to {@code songId}.
   *
   * @param songId The id of song.
   * @param cookie Your qq music cookie.
   * @return A list of QQMusicBasicSong that is similar to song {@code songId}.
   * @apiNote GET /song/similar?id={@code songId}
   */
  List<QQMusicBasicSong> getSimilarSongs(String songId, String cookie);

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
   * @return The urls of your songs with mid {@code songMids}.
   * @apiNote GET /song/urls?id={@code songMids}
   */
  Map<String, String> getSongsLink(String songMids, String cookie);

  /**
   * Search and return paged songs according to the given keyword {@code name}.
   *
   * @param name The search keyword.
   * @param pageNo Page order.
   * @param pageSize Size one page.
   * @param cookie Your qq music cookie.
   * @return A list of paged QQMusicBasicSong wrapped by QQMusicSearchSongPagedResult.
   * @apiNote GET /search?key={@code name}&pageNo={@code pageNo}&pageSize={@code pageSize}
   */
  QQMusicSearchSongPagedResult searchSongByName(
      String name, Integer pageNo, Integer pageSize, String cookie);

  /**
   * Add songs with mids {@code songsMid} to playlist with dirId {@code dirId}
   *
   * @param dirId The dirId of the playlist.
   * @param songsMid The mid of songs, multiple mid separated with comma.
   * @param cookie Your qq music cookie.
   * @return 100 for success, 200 for failure.
   * @apiNote GET /playlist/add?dirid={@code dirId}&mid={@code songsMid}
   */
  String addSongsToPlaylist(String dirId, String songsMid, String cookie);

  /**
   * Move songs {@code songsId} from playlist with {@code fromDirId} to playlist with {@code
   * toDirId}.
   *
   * @param songsId Songs id to be moved, multiple songs id separated with comma.
   * @param fromDirId DirId of from-playlist.
   * @param toDirId DirId of to-playlist.
   * @param cookie Your qq music cookie.
   * @return 100 for success, 200 for failure.
   * @apiNote GET /move?id={@code songsId}&from_dir={@code fromDirId}&to_dir={@code toDirId}
   */
  String moveSongsToOtherPlaylist(String songsId, String fromDirId, String toDirId, String cookie);

  /**
   * Remove songs with song id {@code songId} from playlist with dirId {@code dirId}.
   *
   * @param dirId The dirId of playlist that you want to remove songs from.
   * @param songId The songs' id, multiple songs id separated with comma.
   * @param cookie Your qq music cookie.
   * @return 100 for success.
   * @apiNote GET /playlist/remove?dirid={@code dirId}&id={@code songId}
   */
  String removeSongsFromPlaylist(String dirId, String songId, String cookie);
}
