package com.daw.pms.Service;

import com.daw.pms.Entity.QQMusicPlaylist;
import java.util.List;

/**
 * Service for handle playlists.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 6/28/23
 */
public interface PlaylistService {
  List<QQMusicPlaylist> getPlaylists(String uid, Integer platformId);
  //  Integer createPlaylist()
}
