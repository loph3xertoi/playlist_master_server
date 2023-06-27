package com.daw.pms.Entity;

import java.util.List;
import lombok.Data;

/**
 * POJO for song returned by search api.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 6/2/23
 */
@Data
public class QQMusicBasicSong {
  /** The name of the song. */
  private String songName;

  /** The song id. */
  private String songId;

  /** The song mid. */
  private String songMid;

  /** The media mid of the song. */
  private String mediaMid;

  /** The singers of the song. */
  private List<QQMusicSinger> singers;

  /** Need vip to play(1: need vip, 0: no need vip). */
  private Integer payPlay;
}
