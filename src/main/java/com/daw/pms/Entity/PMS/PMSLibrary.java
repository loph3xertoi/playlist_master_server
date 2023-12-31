package com.daw.pms.Entity.PMS;

import com.daw.pms.Entity.Basic.BasicLibrary;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Basic playlist in pms.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 7/11/23
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PMSLibrary extends BasicLibrary {
  /** The id of this library in playlist master server. */
  private Long id;

  /** The creator's id of this playlist. */
  private Long creatorId;
}
