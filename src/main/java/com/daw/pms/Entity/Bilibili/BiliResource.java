package com.daw.pms.Entity.Bilibili;

import java.io.Serializable;
import lombok.Data;

@Data
public class BiliResource implements Serializable {
  /** The id of this resource. */
  private Long id;

  /** The bvid of this resource. */
  private String bvid;

  /**
   * The type of this resource, 2 for video, 12 for music, 21 for videos, 24 for official resources.
   */
  private Integer type;

  /** The title of this resource. */
  private String title;

  /** The cover of this resource. */
  private String cover;

  /** The page of this resource, has multiple resources if greater than 1. */
  private int page;

  /** The duration of this resource. */
  private int duration;

  /** The upper's name of this resource. */
  private String upperName;

  /** Play count of this resource. */
  private Long playCount;

  /** Danmaku count of this resource. */
  private Long danmakuCount;
}
