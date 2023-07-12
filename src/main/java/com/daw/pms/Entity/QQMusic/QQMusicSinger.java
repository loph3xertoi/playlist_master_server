package com.daw.pms.Entity.QQMusic;

import com.daw.pms.Entity.Basic.BasicSinger;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Singer of qq music.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 6/2/23
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class QQMusicSinger extends BasicSinger {
  /** Singer's id. */
  private Integer id;

  /** Mid of the singer. */
  private String mid;
}
