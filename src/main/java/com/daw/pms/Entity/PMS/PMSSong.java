package com.daw.pms.Entity.PMS;

import com.daw.pms.Entity.Basic.BasicSong;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Basic song in pms.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 8/24/23
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PMSSong extends BasicSong {
  /** The id of pms song. */
  private Long id;

  /** The type of this song, 1 for qqmusic, 2 for ncm, 3 for bilibili. */
  private Integer type;
}
