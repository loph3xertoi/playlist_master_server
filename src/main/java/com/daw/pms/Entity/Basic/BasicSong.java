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
  private String name;

  /** The singers of this song. */
  private List<BasicSinger> singers;

  /** The cover uri of the song. */
  private String cover;

  /**
   * Need vip to play(QQMusic: 1: need vip, 0: no need vip; NeteaseCloudMusic: 1: need vip, 0: no
   * need vip, 4: need vip, 8: free.).
   */
  private Integer payPlay;

  /** Whether this song is taken down. */
  private Boolean isTakenDown;

  /** The link of this song. */
  private String songLink;
}
