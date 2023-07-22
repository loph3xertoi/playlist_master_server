package com.daw.pms.Entity.NeteaseCloudMusic;

import com.daw.pms.Entity.Basic.BasicLibrary;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Basic playlist of netease cloud music.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 7/22/23
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class NCMPlaylist extends BasicLibrary {
  /** The id of this playlist. */
  private Long id;
}
