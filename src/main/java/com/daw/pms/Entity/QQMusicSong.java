package com.daw.pms.Entity;

import lombok.Data;

import java.util.List;

/**
 * POJO for song of qq music.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 6/2/23
 */
@Data
public class QQMusicSong {
  /** The name of the song. */
  private String songName;

  /** The subTitle of the song. */
  private String subTitle;

  /** All singers of this song. */
  private List<QQMusicSinger> singers;

  /** Need vip to play(1: need vip, 0: no need vip). */
  private Integer payPlay;

  /** The id of the song. */
  private String songId;

  /** The mid of the song. */
  private String songMid;

  /** The media mid of the song. */
  private String mediaMid;

  /** The video id of the song. */
  private String vid;

  /** The duration of the song. */
  private Integer duration;

  /** The description of the song. */
  private String songDesc;

  /** The release time of this song */
  private String pubTime;

  /** Song's lyrics. */
  private QQMusicLyrics lyrics;

  /** The list of playlist in pm server the song belongs to. */
  private List<Integer> pmPlaylists;

  /** The song's size in 128k. */
  private Long size128;

  /** The song's size in 320k. */
  private Long size320;

  /** The song's size in Ape. */
  private Long sizeApe;

  /** The song's size in Flac. */
  private Long sizeFlac;
}
