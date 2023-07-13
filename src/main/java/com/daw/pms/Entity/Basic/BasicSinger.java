package com.daw.pms.Entity.Basic;

import java.io.Serializable;
import lombok.Data;

/**
 * Basic singer.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 7/11/23
 */
@Data
public class BasicSinger implements Serializable {
  /** Singer's name. */
  private String name;

  /** Singer's head picture. */
  private String headPic;
}
