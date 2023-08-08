package com.daw.pms.Entity.Bilibili;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * The basic resource of bilibili, such as video, music, videos and official resources.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 8/1/23
 */
@Data
public class BiliDetailResource implements Serializable {
  /** The id of this resource. */
  private Long id;

  /** The bvid of this resource. */
  private String bvid;

  /** The cid of this resource. */
  private Long cid;

  /**
   * The type of this resource, 2 for video, 12 for music, 21 for videos, 24 for official resources.
   */
  private Integer type;

  /** The title of this resource. */
  private String title;

  /** The cover of this resource. */
  private String cover;

  /** The page of this resource, has multiple resources if greater than 1. */
  private Integer page;

  /** Whether this resource has episodes. */
  private Boolean isSeasonResource;

  /** The duration of this resource. */
  private Integer duration;

  /** The upper's name of this resource. */
  private String upperName;

  /** The upper's mid of this resource. */
  private Long upperMid;

  /** The upper's head picture of this resource. */
  private String upperHeadPic;

  /** Play count of this resource. */
  private Long playCount;

  /** Danmaku count of this resource. */
  private Long danmakuCount;

  /** The collected count of this resource. */
  private Long collectedCount;

  /** The comment count. */
  private Long commentCount;

  /** The coins of this resource. */
  private Long coinsCount;

  /** Shared count of this resource. */
  private Long sharedCount;

  /** The liked count of this resource. */
  private Long likedCount;

  /** The introduction of this resource. */
  private String intro;

  /** The published data of this resource. */
  private Long publishedTime;

  /** The created time of this resource. */
  private Long createdTime;

  /** The dynamic lables of this resource. */
  private String dynamicLabels;

  /** The subpages of this resource. */
  private List<BiliSubpageOfResource> subpages;

  /**
   * The links of this video, the key is "video" for video without sound and "audio" for audio only,
   * The value is a map that the key is resource code and the value is the real link of
   * corresponding audio or video, specific code see {@link <a
   * href="https://socialsisteryi.github.io/bilibili-API-collect/docs/bangumi/videostream_url.html#qn%E8%A7%86%E9%A2%91%E6%B8%85%E6%99%B0%E5%BA%A6%E6%A0%87%E8%AF%86>Video
   * code</a>}
   */
  private Map<String, Map<String, String>> links;
}
