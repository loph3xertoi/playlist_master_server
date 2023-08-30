package com.daw.pms.Entity.PMS;

import com.daw.pms.Entity.Basic.BasicSong;
import com.daw.pms.Entity.Bilibili.BiliResource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Detail pms song.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 8/30/23
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PMSDetailSong extends BasicSong {
  public PMSDetailSong(PMSSong song) {
    this.id = song.getId();
    this.type = song.getType();
    super.name = song.getName();
    super.cover = song.getCover();
    super.payPlay = song.getPayPlay();
    super.isTakenDown = song.getIsTakenDown();
  }

  /** The id of pms song. */
  private Long id;

  /** The type of this song, 1 for qqmusic, 2 for ncm, 3 for bilibili. */
  private Integer type;

  /** Original QQMusic song or NCM song. */
  private BasicSong basicSong;

  /** Original BiliBili resources. */
  private BiliResource biliResource;
}
