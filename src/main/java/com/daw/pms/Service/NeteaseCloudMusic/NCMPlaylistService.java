package com.daw.pms.Service.NeteaseCloudMusic;

import com.daw.pms.Entity.NeteaseCloudMusic.NCMDetailPlaylist;
import com.daw.pms.Entity.NeteaseCloudMusic.NCMPlaylist;
import com.daw.pms.Entity.NeteaseCloudMusic.NCMSong;
import java.util.List;
import java.util.Optional;

/**
 * Playlist service for netease cloud music.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 7/22/23
 */
public interface NCMPlaylistService {
  /**
   * Get all playlists of user {@code uid} in netease cloud music.
   *
   * @param uid Your user id in netease cloud music.
   * @param offset Offset from the first song.
   * @param limit The number of songs returned by this query.
   * @param cookie Your cookie for netease cloud music.
   * @return All playlists created by {@code uid}.
   * @apiNote GET /user/playlist?uid={@code uid}&offset={@code offset}&limit={@code limit}
   */
  List<NCMPlaylist> getPlaylists(Long uid, Integer offset, Integer limit, String cookie);

  /**
   * Get detail playlist with {@code id}.
   *
   * @param id The playlist's global id.
   * @param cookie Your cookie for netease cloud music.
   * @return Detail playlist.
   * @apiNote GET /playlist/detail?id={@code id}
   */
  NCMDetailPlaylist getDetailPlaylist(Long id, String cookie);

  /**
   * Get all songs from playlist with {@code id}.
   *
   * @param id The playlist's global id.
   * @param offset Offset from the first song.
   * @param limit The number of songs returned by this query.
   * @param cookie Your cookie for netease cloud music.
   * @return The first {@code limit} songs start from {@code offset} of playlist.
   */
  List<NCMSong> getAllSongsFromPlaylist(
      Long id, Optional<Integer> offset, Optional<Integer> limit, String cookie);

  /**
   * Create playlist.
   *
   * @param name The name of playlist.
   * @param cookie Your cookie for netease cloud music.
   * @return Created playlist's id.
   * @apiNote GET /playlist/create?name={@code name}
   */
  Long createPlaylist(String name, String cookie);

  /**
   * Delete playlist with ids {@code ids}.
   *
   * @param ids The id of playlist you want to delete, multiple id separated with comma.
   * @param cookie Your cookie for netease cloud music.
   * @return Result for deleting playlist.
   * @apiNote GET /playlist/delete?id={@code ids}
   */
  String deletePlaylist(String ids, String cookie);

  /**
   * Add songs with tracks {@code tracks} to playlist with pid {@code pid}
   *
   * @param pid The id of the playlist.
   * @param tracks The id of songs, multiple id separated with comma.
   * @param cookie Your cookie for netease cloud music.
   * @return Operation result.
   * @apiNote GET /playlist/tracks?op=add&pid={@code pid}&tracks={@code tracks}
   */
  String addSongsToPlaylist(Long pid, String tracks, String cookie);

  /**
   * Move songs {@code tracks} from playlist with {@code fromPid} to playlist with {@code toPid}.
   *
   * @param tracks Songs id to be moved, multiple songs id separated with comma.
   * @param fromPid Source playlist's pid.
   * @param toPid Target playlist's pid.
   * @param cookie Your cookie for netease cloud music.
   * @return Operation result.
   * @apiNote GET /playlist/tracks?op=mov&fromPid={@code fromPid}&toPid={@code toPid}&tracks={@code
   *     tracks}
   */
  String moveSongsToOtherPlaylist(String tracks, Long fromPid, Long toPid, String cookie);

  /**
   * Remove songs with song id {@code tracks} from playlist with pid {@code pid}.
   *
   * @param pid The id of playlist that you want to remove songs from.
   * @param tracks The songs' id, multiple songs id separated with comma.
   * @param cookie Your cookie for netease cloud music.
   * @return Operation result.
   * @apiNote GET /playlist/tracks?op=del&pid={@code pid}&tracks={@code tracks}
   */
  String removeSongsFromPlaylist(Long pid, String tracks, String cookie);
}
