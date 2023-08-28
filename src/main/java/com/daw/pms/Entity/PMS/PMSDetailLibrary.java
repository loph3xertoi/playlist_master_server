package com.daw.pms.Entity.PMS;

import com.daw.pms.Entity.Basic.BasicLibrary;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Detail playlist in pms.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 8/24/23
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PMSDetailLibrary extends BasicLibrary {
  /** The id of this library in playlist master server. */
  private Long id;

  /** The creator's id of this playlist. */
  private Long creatorId;

  /** The introduction of this playlist. */
  private String intro;

  /** Create time of this playlist. */
  private Long createTime;

  /** Update time of this playlist. */
  private Long updateTime;

  /** All songs in this playlist. */
  private List<PMSSong> songs;
}
