package com.daw.pms.Entity;

import lombok.Data;

/**
 * POJO for playlist of qq music.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 6/1/23
 */
@Data
public class QQMusicPlaylist {
  /** The name of your playlist. */
  private String name;

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
}
