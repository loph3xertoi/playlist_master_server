package com.daw.pms.Entity.QQMusic;

import java.io.Serializable;
import lombok.Data;

/**
 * POJO for lyrics of song in qq music.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 6/2/23
 */
@Data
public class QQMusicLyrics implements Serializable {
  /** Lyrics of song. */
  private String lyric;

  /** Translate of lyrics. */
  private String trans;
}
