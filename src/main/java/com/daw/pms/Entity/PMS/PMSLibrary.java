package com.daw.pms.Entity.PMS;

import com.daw.pms.Entity.Basic.BasicLibrary;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Store single playlist or favorite.
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
  private String id;
}
