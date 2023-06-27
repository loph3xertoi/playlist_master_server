package com.daw.pms.Entity;

import lombok.Data;

import java.util.List;

/**
 * POJO for paged result.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 6/2/23
 */
@Data
public class QQMusicSearchSongPagedResult {
  /** Basic songs returned by one page. */
  private List<QQMusicBasicSong> basicSongList;

  /** Page order. */
  private Integer pageNo;

  /** Count of songs returned one-page result. */
  private Integer pageSize;

  /** Total numbers of all matched songs. */
  private Integer total;
}
