package com.daw.pms.Entity.QQMusic;

import com.daw.pms.Entity.Basic.BasicVideo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Basic video for qq music.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 6/2/23
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class QQMusicVideo extends BasicVideo {
  /** The vid of the mv. */
  private String vid;

  /** Viewed times of the video. */
  private Integer playCnt;
}
