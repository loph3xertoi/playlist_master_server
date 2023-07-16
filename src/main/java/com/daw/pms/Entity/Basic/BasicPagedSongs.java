package com.daw.pms.Entity.Basic;

import java.io.Serializable;
import lombok.Data;

/**
 * Basic songs returned by one page.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 7/15/23
 */
@Data
public class BasicPagedSongs implements Serializable {
  /** Page number. */
  private Integer pageNo;

  /** Page size. */
  private Integer pageSize;

  /** Total numbers of all matched songs. */
  private Integer total;
}
