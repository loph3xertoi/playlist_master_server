package com.daw.pms.Entity.NeteaseCloudMusic;

import com.daw.pms.Entity.Basic.BasicVideo;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class NCMVideo extends BasicVideo {
  /** The id of the mv. */
  private Long id;

  /** The description of the mv. */
  private String desc;

  /** Viewed times of the video. */
  private Integer playCount;

  /** The duration of the mv. * */
  private Integer duration;

  /** The published time of the mv. */
  private String publishTime;

  /** The bite rate and size of the mv. */
  private List<Map<String, String>> brs;
}
