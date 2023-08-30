package com.daw.pms.Entity.Basic;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * Basic video.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 7/11/23
 */
@Data
public class BasicVideo implements Serializable {
  /** The video name. */
  private String name;

  /** The cover of this video. */
  private String cover;

  /** The singers of this video. */
  private List<? extends BasicSinger> singers;
}
