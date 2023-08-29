package com.daw.pms.DTO;

import java.io.Serializable;
import java.util.Map;
import lombok.Data;

/**
 * DTO for bilibili resource's links object.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 8/11/23
 */
@Data
public class BiliLinksDTO implements Serializable {
  /**
   * The video links of the resource, key is "resource code", see <a
   * href="https://socialsisteryi.github.io/bilibili-API-collect/docs/bangumi/videostream_url.html#qn%E8%A7%86%E9%A2%91%E6%B8%85%E6%99%B0%E5%BA%A6%E6%A0%87%E8%AF%86">Video
   * code</a> for more details, the value is the link of this video corresponding to the resource
   * code in the key.
   */
  private Map<String, String> video;

  /**
   * The audio links of the resource, key is "resource code", see <a
   * href="https://socialsisteryi.github.io/bilibili-API-collect/docs/bangumi/videostream_url.html#qn%E8%A7%86%E9%A2%91%E6%B8%85%E6%99%B0%E5%BA%A6%E6%A0%87%E8%AF%86">Audio
   * code</a> for more details, the value is the link of this audio corresponding to the resource
   * code in the key.
   */
  private Map<String, String> audio;

  /** The mpd url for this resource. */
  private String mpd;
}
