package com.daw.pms.Entity.NeteaseCloudMusic;

import com.daw.pms.Entity.Basic.BasicSinger;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Singer of netease cloud music.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 7/22/23
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class NCMSinger extends BasicSinger {
  /** Singer's id. */
  private Long id;
}
