package com.daw.pms.Entity.QQMusic;

import com.daw.pms.Entity.Basic.BasicSong;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Basic song in qq music returned by search api.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 6/2/23
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class QQMusicSong extends BasicSong {
  /** The song id. */
  private String songId;

  /** The song mid. */
  private String songMid;

  /** The media mid of the song. */
  private String mediaMid;
}
