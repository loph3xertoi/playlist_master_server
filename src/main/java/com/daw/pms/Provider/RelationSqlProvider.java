package com.daw.pms.Provider;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.jdbc.SQL;

/**
 * Relation sql provider.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/18/23
 */
public class RelationSqlProvider {
  /**
   * addRelationSongSinger.
   *
   * @param params a {@link java.util.List} object.
   * @return a {@link java.lang.String} object.
   */
  public String addRelationSongSinger(List<Map<String, Long>> params) {
    StringBuilder sql = new StringBuilder();
    for (Map<String, Long> param : params) {
      Long fkSongId = param.get("fkSongId");
      Long fkSingerId = param.get("fkSingerId");
      if (fkSongId == null) {
        throw new RuntimeException("The fkSongId mustn't be null");
      }
      if (fkSingerId == null) {
        throw new RuntimeException("The fkSingerId mustn't be null");
      }
      String subSql =
          new SQL()
              .INSERT_INTO("tb_song_singer")
              .VALUES("fk_song_id", fkSongId.toString())
              .VALUES("fk_singer_id", fkSingerId.toString())
              .toString();
      sql.append(subSql).append(";");
    }
    return sql.toString();
  }

  /**
   * addRelationPlaylistSong.
   *
   * @param params a {@link java.util.List} object.
   * @return a {@link java.lang.String} object.
   */
  public String addRelationPlaylistSong(List<Map<String, Long>> params) {
    StringBuilder sql = new StringBuilder();
    for (Map<String, Long> param : params) {
      Long fkPlaylistId = param.get("fkPlaylistId");
      Long fkSongId = param.get("fkSongId");
      if (fkPlaylistId == null) {
        throw new RuntimeException("The fkPlaylistId mustn't be null");
      }
      if (fkSongId == null) {
        throw new RuntimeException("The fkSongId mustn't be null");
      }
      String subSql =
          new SQL()
              .INSERT_INTO("tb_playlist_song")
              .VALUES("fk_playlist_id", fkPlaylistId.toString())
              .VALUES("fk_song_id", fkSongId.toString())
              .toString();
      sql.append(subSql).append(";");
    }
    return sql.toString();
  }

  /**
   * deleteRelationPlaylistSong.
   *
   * @param params a {@link java.util.Map} object.
   * @return a {@link java.lang.String} object.
   */
  public String deleteRelationPlaylistSong(Map<String, Object> params) {
    StringBuilder sql = new StringBuilder();
    String libraryId = String.valueOf(params.get("libraryId"));
    String[] songsIds = (String[]) params.get("songsIds");
    for (String songId : songsIds) {
      String subSql =
          new SQL()
              .DELETE_FROM("tb_playlist_song")
              .WHERE("fk_playlist_id = " + libraryId + " and fk_song_id = " + songId)
              .toString();
      sql.append(subSql).append(";");
    }
    return sql.toString();
  }
}
