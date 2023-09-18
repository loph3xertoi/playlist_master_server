package com.daw.pms.Entity.Basic;

import java.io.Serializable;
import lombok.Data;

/**
 * Basic lyrics.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/18/23
 */
@Data
public class BasicLyrics implements Serializable {
  /** Lyrics with lrc format. */
  private String lyric;
}
