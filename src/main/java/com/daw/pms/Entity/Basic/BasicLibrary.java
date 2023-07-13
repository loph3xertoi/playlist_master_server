package com.daw.pms.Entity.Basic;

import java.io.Serializable;
import lombok.Data;

/**
 * Basic library.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 7/11/23
 */
@Data
public class BasicLibrary implements Serializable {
  /** The name of your library. */
  private String name;

  /** The cover image of your library. */
  private String cover;

  /** The count of songs/videos in this library. */
  private Integer itemCount;
}
