package com.daw.pms.Entity.NeteaseCloudMusic;

import com.daw.pms.Entity.Basic.BasicLyrics;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Lyrics for song in netease cloud music
 *
 * @author Daw Loph
 * @version 1.0
 * @since 7/22/23
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class NCMLyrics extends BasicLyrics {
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
