package com.daw.pms.Entity.PMS;

import com.daw.pms.Entity.Basic.BasicSinger;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Singer in pms.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 8/24/23
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PMSSinger extends BasicSinger {
  /** Singer id in pms. */
  private Long id;

  /** Which platform this singer belongs to, 1: qqmusic, 2: ncm, 3: bilibili. */
  private Integer type;
}
