package com.daw.pms.Entity.QQMusic;

import com.daw.pms.Entity.Basic.BasicSong;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Detail song of qq music.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 6/2/23
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class QQMusicDetailSong extends BasicSong {
  /** The subTitle of the song. */
  private String subTitle;

  /** The name of the album. */
  private String albumName;

  /** The id of the song. */
  private String songId;

  /** The mid of the song. */
  private String songMid;

  /** The media mid of the song. */
  private String mediaMid;

  /** The duration of the song. */
  private int duration;

  /** The description of the song. */
  private String songDesc;

  /** The release time of this song */
  private String pubTime;

  /** Lyrics. */
  private QQMusicLyrics lyrics;

  /** The list of playlist in pm server the song belongs to. */
  private List<Integer> pmPlaylists;

  /** The song's size in 128k. */
  private long size128;

  /** The song's size in 320k. */
  private long size320;

  /** The song's size in Ape. */
  private long sizeApe;

  /** The song's size in Flac. */
  private long sizeFlac;
}
