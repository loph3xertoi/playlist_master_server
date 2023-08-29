package com.daw.pms.Entity.Bilibili;

import java.io.Serializable;
import lombok.Data;

/**
 * The subpage of resource.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 8/2/23
 */
@Data
public class BiliSubpageOfResource implements Serializable {
  /** The bvid of this resource, same as the parent resource's bvid. */
  private String bvid;

  /** The cid of this resource. */
  private Long cid;

  /** The page number of this resource. */
  private int page;

  /** The name of this part of resource. */
  private String partName;

  /** The duration of this part of resource. */
  private int duration;

  /** The width of this part of resource. */
  private int width;

  /** The height of this part of resource. */
  private int height;
}
