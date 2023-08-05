package com.daw.pms.Service.QQMusic;

import com.daw.pms.DTO.Result;

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
   * @return All playlists created by {@code qid}, wrapped by Result DTO, the data is
   *     PagedDataDTO<QQMusicPlaylist>
   * @apiNote GET /user/playlist?id={@code qid}
   */
  Result getPlaylists(String qid, String cookie);

  /**
   * Get detail playlist with {@code tid}.
   *
   * @param tid The playlist's global tid.
   * @param cookie Your qq music cookie.
   * @return Detail playlist wrapped by Result DTO, the data is QQMusicDetailPlaylist.
   * @apiNote GET /playlist?id={@code tid}
   */
  Result getDetailPlaylist(String tid, String cookie);

  /**
   * Create playlist.
   *
   * @param name The name of playlist.
   * @param cookie Your cookie for qq music.
   * @return The response of request wrapped by Result DTO.
   * @apiNote GET /playlist/create?name={@code name}
   */
  Result createPlaylist(String name, String cookie);

  /**
   * Delete playlist with dirId {@code dirId}.
   *
   * @param dirId The dirId of playlist you want to delete, multiple dirId separated with comma.
   * @param cookie Your cookie for qq music.
   * @return The response of request wrapped by Result DTO.
   * @apiNote GET /playlist/delete?dirid={@code dirId}
   */
  Result deletePlaylist(String dirId, String cookie);

  /**
   * Add songs with mids {@code songsMid} to playlist with dirId {@code dirId}
   *
   * @param dirId The dirId of the playlist.
   * @param songsMid The mid of songs, multiple mid separated with comma.
   * @param cookie Your qq music cookie.
   * @return The response of request wrapped by Result DTO.
   * @apiNote GET /playlist/add?dirid={@code dirId}&mid={@code songsMid}
   */
  Result addSongsToPlaylist(Integer dirId, String songsMid, String cookie);

  /**
   * Move songs {@code songsId} from playlist with {@code fromDirId} to playlist with {@code
   * toDirId}.
   *
   * @param songsId Songs id to be moved, multiple songs id separated with comma.
   * @param fromDirId DirId of source playlist.
   * @param toDirId DirId of target playlist.
   * @param cookie Your qq music cookie.
   * @return The response of request wrapped by Result DTO.
   * @apiNote GET /move?id={@code songsId}&from_dir={@code fromDirId}&to_dir={@code toDirId}
   */
  Result moveSongsToOtherPlaylist(
      String songsId, Integer fromDirId, Integer toDirId, String cookie);

  /**
   * Remove songs with song id {@code songId} from playlist with dirId {@code dirId}.
   *
   * @param dirId The dirId of playlist that you want to remove songs from.
   * @param songsId The songs' id, multiple songs id separated with comma.
   * @param cookie Your qq music cookie.
   * @return The response of request wrapped by Result DTO.
   * @apiNote GET /playlist/remove?dirid={@code dirId}&id={@code songsId}
   */
  Result removeSongsFromPlaylist(Integer dirId, String songsId, String cookie);
}
