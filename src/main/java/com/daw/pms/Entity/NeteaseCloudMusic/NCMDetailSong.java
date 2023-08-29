package com.daw.pms.Entity.NeteaseCloudMusic;

import com.daw.pms.Entity.Basic.BasicSong;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Detail song of netease cloud music.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 7/22/23
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class NCMDetailSong extends BasicSong {
  /** The id of this song. */
  private Long id;

  /** The mv id of this song. */
  private Long mvId;

  /** The album name of this song. */
  private String albumName;

  /** The duration of this song. */
  private int duration;

  /** The release time of this song */
  private String publishTime;

  /** PMSSong's lyrics. */
  private NCMLyrics lyrics;

  /** The list of playlist in pm server the song belongs to. */
  private List<Integer> pmPlaylists;

  /** High bitrate of this song. */
  private int hBr;

  /** The size of high bitrate of this song. */
  private long hSize;

  /** Middle bitrate of this song. */
  private int mBr;

  /** The size of middle bitrate of this song. */
  private long mSize;

  /** Low bitrate of this song. */
  private int lBr;

  /** The size of low bitrate of this song. */
  private long lSize;

  /** Super quality bitrate of this song. */
  private int sqBr;

  /** The size of Super quality bitrate of this song. */
  private long sqSize;
}
