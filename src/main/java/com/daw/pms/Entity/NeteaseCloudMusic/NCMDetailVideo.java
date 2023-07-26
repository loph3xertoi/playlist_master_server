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
public class NCMDetailVideo extends BasicVideo {
  /** The id of the mv. */
  private String id;

  /** The MV's id of this song. */
  private Long mvId;

  /** The description of the mv. */
  private String desc;

  /** Viewed count of the video. */
  private Integer playCount;

  /** Subscribed count of the video. */
  private Integer subCount;

  /** Shared count of the video. */
  private Integer shareCount;

  /** Commented count of the video. */
  private Integer commentCount;

  /** The duration of the mv. * */
  private Integer duration;

  /** The published time of the mv. */
  private String publishTime;

  /** The bite rate and size of the mv. */
  private List<Map<String, String>> brs;
}
