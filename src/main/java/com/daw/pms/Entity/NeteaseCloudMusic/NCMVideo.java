package com.daw.pms.Entity.NeteaseCloudMusic;

import com.daw.pms.Entity.Basic.BasicVideo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class NCMVideo extends BasicVideo {
  /** The id of the mv, may be mvid or vid. */
  private String id;

  /** Viewed times of the video. */
  private int playCount;

  /** The duration of the mv. */
  private int duration;

  /** The published time of the mv. */
  private String publishTime;
}
