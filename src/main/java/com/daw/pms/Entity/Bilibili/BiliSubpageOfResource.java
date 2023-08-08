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
  /** The cid of this resource. */
  private Long cid;

  /** The page number of this resource. */
  private Integer page;

  /** The name of this part of resource. */
  private String partName;

  /** The duration of this part of resource. */
  private Integer duration;

  /** The width of this part of resource. */
  private Integer width;

  /** The height of this part of resource. */
  private Integer height;

  //  /** The first frame of this part of resource. */
  //  private String firstFrame;
}
