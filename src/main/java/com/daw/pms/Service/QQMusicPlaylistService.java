package com.daw.pms.Service;

import com.daw.pms.Entity.QQMusicDetailPlaylist;
import com.daw.pms.Entity.QQMusicPlaylist;

import java.util.List;

/**
 * Service for deal with qq music playlists.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 6/1/23
 */
public interface QQMusicPlaylistService {
  /**
   * Get all playlist of user {@code id}.
   *
   * @param id User's qq number.
   * @param cookie Your cookie for qq music.
   * @return All playlist created by {@code id}.
   * @apiNote GET /user/playlist?id={@code id}
   */
  List<QQMusicPlaylist> getPlaylists(String id, String cookie);

  /**
   * Get detail playlist with {@code tid}.
   *
   * @param tid The playlist's global tid.
   * @param cookie Your qq music cookie.
   * @return Detail playlist.
   * @apiNote GET /playlist?id={@code tid}
   */
  QQMusicDetailPlaylist getDetailPlaylist(String tid, String cookie);

  /**
   * Create playlist.
   *
   * @param name The name of playlist.
   * @param cookie Your cookie for qq music.
   * @return Result for creating playlist.
   * @apiNote GET /playlist/create?name={@code name}
   */
  String createPlaylist(String name, String cookie);

  /**
   * Delete playlist with dirId {@code dirId}.
   *
   * @param dirId The dirId of playlist you want to delete.
   * @param cookie Your cookie for qq music.
   * @return Result for deleting playlist.
   * @apiNote /GET /playlist/delete?dirid={@code dirId}
   */
  String deletePlaylist(Integer dirId, String cookie);
}
