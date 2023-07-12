package com.daw.pms.Service.QQMusic;

import com.daw.pms.Entity.Basic.BasicLibrary;
import com.daw.pms.Entity.QQMusic.QQMusicDetailPlaylist;
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
   * Get all playlists of user {@code qid} in qq music platform.
   *
   * @param qid Your qq number.
   * @param cookie Your cookie for qq music.
   * @return All playlists created by {@code qid}.
   * @apiNote GET /user/playlist?id={@code qid}
   */
  List<BasicLibrary> getPlaylists(String qid, String cookie);

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
