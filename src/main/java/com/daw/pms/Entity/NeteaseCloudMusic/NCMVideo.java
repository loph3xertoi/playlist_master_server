package com.daw.pms.Entity.NeteaseCloudMusic;

import com.daw.pms.Entity.Basic.BasicVideo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class NCMVideo extends BasicVideo {
  /** The id of the mv. */
  private String id;

  /** Viewed times of the video. */
  private Integer playCount;
}
