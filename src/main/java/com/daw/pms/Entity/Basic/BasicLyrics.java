package com.daw.pms.Entity.Basic;

import java.io.Serializable;
import lombok.Data;

@Data
public class BasicLyrics implements Serializable {
  /** Lyrics with lrc format. */
  private String lyric;
}
