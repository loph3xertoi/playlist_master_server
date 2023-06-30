package com.daw.pms.Entity;

import java.util.List;
import lombok.Data;

/**
 * POJO for detail playlist of qq music.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 6/29/23
 */
@Data
public class QQMusicDetailPlaylist {
  /** The name of your playlist. */
  private String name;

  /** The description of your playlist. */
  private String desc;

  /** The cover image of your playlist. */
  private String coverImage;

  /** The count of songs in this playlist. */
  private Integer songCount;

  /** Listen times of this playlist. */
  private Integer listenNum;

  /** The dirId(local id) of this playlist. */
  private Integer dirId;

  /** The tid(global id) of this playlist. */
  private String tid;

  /** All basic songs in this playlist. */
  private List<QQMusicBasicSong> songs;
}
