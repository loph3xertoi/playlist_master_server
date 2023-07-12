package com.daw.pms.Entity.QQMusic;

import com.daw.pms.Entity.Basic.BasicLibrary;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Basic playlist of qq music.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 6/1/23
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class QQMusicPlaylist extends BasicLibrary {
  /** The dirId(local id) of this playlist. */
  private Integer dirId;

  /** The tid(global id) of this playlist. */
  private String tid;
}
