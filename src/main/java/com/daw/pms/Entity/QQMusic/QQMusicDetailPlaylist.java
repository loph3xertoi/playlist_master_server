package com.daw.pms.Entity.QQMusic;

import com.daw.pms.Entity.Basic.BasicLibrary;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Detail playlist of qq music.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 6/29/23
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class QQMusicDetailPlaylist extends BasicLibrary {
  /** The description of your playlist. */
  private String desc;

  /** Listen times of this playlist. */
  private Integer listenNum;

  /** The dirId(local dawid) of this playlist. */
  private Integer dirId;

  /** The tid(global id) of this playlist. */
  private String tid;

  /** All basic songs in this playlist. */
  private List<QQMusicSong> songs;
}
