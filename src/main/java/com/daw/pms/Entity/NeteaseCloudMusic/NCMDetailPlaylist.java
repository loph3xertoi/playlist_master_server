package com.daw.pms.Entity.NeteaseCloudMusic;

import com.daw.pms.Entity.Basic.BasicLibrary;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Detail playlist of netease cloud music.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 7/22/23
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class NCMDetailPlaylist extends BasicLibrary {
  /** The id of this playlist. */
  private Long id;

  /** Last update time of the playlist's tracks. */
  private Long trackUpdateTime;

  /** Last update time of the playlist. */
  private Long updateTime;

  /** The creating time of the playlist. */
  private Long createTime;

  /** Playing count of this playlist. */
  private int playCount;

  /** The description of your playlist. */
  private String description;

  /** The tags of the playlist. */
  private List<String> tags;

  /** The songs within this playlist. */
  private List<NCMSong> songs;
}
