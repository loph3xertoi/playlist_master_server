package com.daw.pms.Entity.Basic;

import lombok.Data;

/**
 * Basic library.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 7/11/23
 */
@Data
public class BasicLibrary {
  /** The name of your library. */
  private String name;

  /** The cover image of your library. */
  private String cover;

  /** The count of songs/videos in this library. */
  private Integer itemCount;
}
