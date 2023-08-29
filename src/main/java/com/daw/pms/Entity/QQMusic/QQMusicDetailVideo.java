package com.daw.pms.Entity.QQMusic;

import com.daw.pms.Entity.Basic.BasicVideo;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Detail video for qq music.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 7/12/23
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class QQMusicDetailVideo extends BasicVideo {
  /** The published date of the mv. */
  private int pubDate;

  /** The vid of the mv. */
  private String vid;

  /** The duration the mv. */
  private int duration;

  /** Viewed times of the video. */
  private int playCnt;

  /** Description of the mv. */
  private String desc;

  /** The links of the mv. */
  private List<String> links;
}
