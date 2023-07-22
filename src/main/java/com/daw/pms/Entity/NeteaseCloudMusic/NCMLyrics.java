package com.daw.pms.Entity.NeteaseCloudMusic;

import java.io.Serializable;
import lombok.Data;

/**
 * Lyrics for song in netease cloud music
 *
 * @author Daw Loph
 * @version 1.0
 * @since 7/22/23
 */
@Data
public class NCMLyrics implements Serializable {
  /** LRC format. */
  private String lrc;

  /** Pin yin format. */
  private String kLyric;

  /** Translate to chinese. */
  private String tLyric;

  /** Roma format. */
  private String romaLrc;

  /** Per-character format. */
  private String yrc;

  /** Per-character format translated to chinese. */
  private String ytLrc;
}
