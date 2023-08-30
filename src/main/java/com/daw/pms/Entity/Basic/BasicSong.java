package com.daw.pms.Entity.Basic;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * Basic song.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 7/11/23
 */
@Data
public class BasicSong implements Serializable {
  /** Song name. */
  protected String name;

  /** The singers of this song. */
  protected List<? extends BasicSinger> singers;

  /** The cover uri of the song. */
  protected String cover;

  /**
   * Need vip to play(QQMusic: 1: need vip, 0: no need vip; NeteaseCloudMusic: 1: need vip, 0: no
   * need vip, 4: need vip, 8: free.).
   */
  protected Integer payPlay;

  /** Whether this song is taken down. */
  protected Boolean isTakenDown;

  /** The link of this song. */
  protected String songLink;
}
