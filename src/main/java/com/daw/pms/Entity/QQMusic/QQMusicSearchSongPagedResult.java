package com.daw.pms.Entity.QQMusic;

import com.daw.pms.Entity.Basic.BasicPagedSongs;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Paged qq music songs.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 6/2/23
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class QQMusicSearchSongPagedResult extends BasicPagedSongs {
  /** Songs returned by one page. */
  private List<QQMusicSong> songs;
}
