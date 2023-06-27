package com.daw.pms.Entity;

import lombok.Data;

/**
 * POJO for singer of qq music.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 6/2/23
 */
@Data
public class QQMusicSinger {
  /** Singer's id. */
  private String id;

  /** Mid of the singer. */
  private String mid;

  /** Singer's name. */
  private String name;
}
