package com.daw.pms.Entity;

import java.util.List;
import lombok.Data;

/**
 * POJO for MV of song in qq music.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 6/2/23
 */
@Data
public class QQMusicMV {
  /** The name of mv. */
  private String name;

  /** The singers of the mv. */
  private List<QQMusicSinger> singers;

  /** The cover of the mv. */
  private String coverPic;

  /** The vid of the mv. */
  private String vid;

  /** The duration the mv. */
  private Integer duration;

  /** Play times of the video. */
  private Integer playCnt;

  /** Description of the mv. */
  private String desc;
}
