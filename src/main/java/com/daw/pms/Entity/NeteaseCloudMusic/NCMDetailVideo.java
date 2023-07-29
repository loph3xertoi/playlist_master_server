package com.daw.pms.Entity.NeteaseCloudMusic;

import com.daw.pms.Entity.Basic.BasicVideo;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class NCMDetailVideo extends BasicVideo {
  /** The id of the mv, may be mvid or vid. */
  private String id;

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

  /** The duration of the mv. */
  private Integer duration;

  /** The published time of the mv. */
  private String publishTime;

  /** The bite rate as key and the size of the mv as value. */
  private Map<String, Integer> rates;

  /** The links of the mv, the key is resolution and the value is the url. */
  private Map<String, String> links;
}
