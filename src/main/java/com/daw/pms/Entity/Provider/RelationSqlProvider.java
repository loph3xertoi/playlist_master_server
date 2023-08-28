package com.daw.pms.Entity.Provider;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.jdbc.SQL;

public class RelationSqlProvider {
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
              .VALUES("fk_song_id", "#{fkSongId}")
              .VALUES("fk_singer_id", "#{fkSingerId}")
              .toString();
      sql.append(subSql).append(";");
    }
    return sql.toString();
  }

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
              .VALUES("fk_playlist_id", "#{fkPlaylistId}")
              .VALUES("fk_song_id", "#{fkSongId}")
              .toString();
      sql.append(subSql).append(";");
    }
    return sql.toString();
  }

  public String deleteRelationPlaylistSong(Map<String, Object> params) {
    StringBuilder sql = new StringBuilder();
    Long libraryId = (Long) params.get("libraryId");
    Long[] songsIds = (Long[]) params.get("songsIds");
    if (libraryId == null) {
      throw new RuntimeException("The libraryId mustn't be null");
    }
    for (Long songId : songsIds) {
      String subSql =
          new SQL()
              .DELETE_FROM("tb_playlist_song")
              .WHERE("fk_playlist_id = " + libraryId + " and fk_song_id = " + songId.toString())
              .toString();
      sql.append(subSql).append(";");
    }
    return sql.toString();
  }
}
