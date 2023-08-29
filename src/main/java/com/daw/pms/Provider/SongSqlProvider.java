package com.daw.pms.Provider;

import com.daw.pms.Entity.PMS.PMSSong;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.jdbc.SQL;

public class SongSqlProvider {
  public String getSongs(List<Long> ids) {
    String sql =
        new SQL() {
          {
            SELECT("*");
            FROM("tb_pms_song");
            if (ids != null) {
              WHERE(getSqlConditionCollection("id", ids));
            }
          }
        }.toString();
    return sql;
  }

  public String getExistedIdsAndSongIdsList(List<Long> songIds) {
    String sql =
        new SQL() {
          {
            SELECT("pms_song_id, song_id");
            FROM("tb_qqmusic_song");
            if (songIds != null) {
              WHERE(getSqlConditionCollection("song_id", songIds));
            }
          }
        }.toString();
    return sql;
  }

  public String getExistedIdsAndNCMIdsList(List<Long> ncmIds) {
    String sql =
        new SQL() {
          { // @Select("select pms_song_id, ncm_id from tb_ncm_song where ncm_id in #{ncmIds}")
            SELECT("pms_song_id, ncm_id");
            FROM("tb_ncm_song");
            if (ncmIds != null) {
              WHERE(getSqlConditionCollection("ncm_id", ncmIds));
            }
          }
        }.toString();
    return sql;
  }

  public String getExistedIdsAndAidsList(List<Long> aids) {
    String sql =
        new SQL() {
          { //   @Select("select pms_song_id, aid from tb_bilibili_resource where aid in #{aids}")
            SELECT("pms_song_id, aid");
            FROM("tb_bilibili_resource");
            if (aids != null) {
              WHERE(getSqlConditionCollection("aid", aids));
            }
          }
        }.toString();
    return sql;
  }

  private String getSqlConditionCollection(String columnName, List<Long> ids) {
    StringBuilder strConditions = new StringBuilder();
    if (ids != null && ids.size() > 0) {
      int count = ids.size();
      for (int i = 0; i < count; i++) {
        String condition = ids.get(i).toString();
        strConditions.append(condition);
        if (i < count - 1) {
          strConditions.append(",");
        }
      }
      return columnName + " in (" + strConditions + ")";
    } else {
      return "1=2";
    }
  }

  public String updateSong(PMSSong song) {
    String name = song.getName();
    String cover = song.getCover();
    Integer payPlay = song.getPayPlay();
    Boolean isTakenDown = song.getIsTakenDown();
    Integer type = song.getType();
    Long id = song.getId();
    if (id == null) {
      throw new RuntimeException("Song id mustn't be null.");
    }
    if (name == null && cover == null && payPlay == null && isTakenDown == null && type == null) {
      throw new RuntimeException("Must specify at least one field to update");
    }
    SQL sql = new SQL().UPDATE("tb_pms_song");
    if (name != null) {
      sql.SET("name = #{name}");
    }
    if (cover != null) {
      sql.SET("cover = #{cover}");
    }
    if (payPlay != null) {
      sql.SET("pay_play = #{payPlay}");
    }
    if (isTakenDown != null) {
      sql.SET("is_taken_down = #{isTakenDown}");
    }
    if (type != null) {
      sql.SET("type = #{type}");
    }
    sql.WHERE("id = #{id}");
    return sql.toString();
  }

  public String addSong(PMSSong song) {
    String name = song.getName();
    String cover = song.getCover();
    Integer payPlay = song.getPayPlay();
    Boolean isTakenDown = song.getIsTakenDown();
    Integer type = song.getType();

    if (name == null) {
      throw new RuntimeException("Song name mustn't be null");
    }
    if (cover == null) {
      song.setCover("");
    }
    if (payPlay == null) {
      throw new RuntimeException("The payPlay mustn't be null");
    }
    if (isTakenDown == null) {
      throw new RuntimeException("The isTakenDown mustn't be null");
    }
    if (type == null) {
      throw new RuntimeException("The song type mustn't be null");
    }

    return new SQL()
        .INSERT_INTO("tb_pms_song")
        .VALUES("name", "#{name}")
        .VALUES("cover", "#{cover}")
        .VALUES("pay_play", "#{payPlay}")
        .VALUES("is_taken_down", "#{isTakenDown}")
        .VALUES("type", "#{type}")
        .toString();
  }

  public String addQQMusicSong(List<Map<String, String>> params) {
    StringBuilder sql = new StringBuilder();
    for (Map<String, String> param : params) {
      String pmsSongId = param.get("pmsSongId");
      String songId = param.get("songId");
      String songMid = param.get("songMid");
      String mediaMid = param.get("mediaMid");
      if (pmsSongId == null) {
        throw new RuntimeException("The pmsSongId mustn't be null");
      }
      if (songId == null) {
        throw new RuntimeException("The songId mustn't be null");
      }
      if (songMid == null) {
        throw new RuntimeException("The songMid mustn't be null");
      }
      if (mediaMid == null) {
        throw new RuntimeException("The mediaMid mustn't be null");
      }
      String subSql =
          new SQL()
              .INSERT_INTO("tb_qqmusic_song")
              .VALUES("pms_song_id", pmsSongId)
              .VALUES("song_id", songId)
              .VALUES("song_mid", songMid)
              .VALUES("media_mid", mediaMid)
              .toString();
      sql.append(subSql).append(";");
    }
    return sql.toString();
  }

  public String addNCMSong(List<Map<String, String>> params) {
    StringBuilder sql = new StringBuilder();
    for (Map<String, String> param : params) {
      String pmsSongId = param.get("pmsSongId");
      String ncmId = param.get("ncmId");
      String mvId = param.get("mvId");
      if (pmsSongId == null) {
        throw new RuntimeException("The pmsSongId mustn't be null");
      }
      if (ncmId == null) {
        throw new RuntimeException("The ncmId mustn't be null");
      }
      if (mvId == null) {
        throw new RuntimeException("The mvId mustn't be null");
      }
      String subSql =
          new SQL()
              .INSERT_INTO("tb_ncm_song")
              .VALUES("pms_song_id", pmsSongId)
              .VALUES("ncm_id", ncmId)
              .VALUES("mv_id", mvId)
              .toString();
      sql.append(subSql).append(";");
    }
    return sql.toString();
  }

  public String addBiliResource(List<Map<String, String>> params) {
    StringBuilder sql = new StringBuilder();
    for (Map<String, String> param : params) {
      String pmsSongId = param.get("pmsSongId");
      String aid = param.get("aid");
      String bvid = param.get("bvid");
      if (pmsSongId == null) {
        throw new RuntimeException("The pmsSongId mustn't be null");
      }
      if (aid == null) {
        throw new RuntimeException("The aid mustn't be null");
      }
      if (bvid == null) {
        throw new RuntimeException("The bvid mustn't be null");
      }
      String subSql =
          new SQL()
              .INSERT_INTO("tb_bilibili_resource")
              .VALUES("pms_song_id", pmsSongId)
              .VALUES("aid", aid)
              .VALUES("bvid", bvid)
              .toString();
      sql.append(subSql).append(";");
    }
    return sql.toString();
  }
}
