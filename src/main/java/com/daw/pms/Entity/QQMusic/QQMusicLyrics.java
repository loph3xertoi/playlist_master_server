package com.daw.pms.Entity.QQMusic;

import com.daw.pms.Entity.Basic.BasicLyrics;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * POJO for lyrics of song in qq music.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 6/2/23
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class QQMusicLyrics extends BasicLyrics {
  /** Translate of lyrics. */
  private String trans;
}
