package com.daw.pms.Provider;

import com.daw.pms.Entity.PMS.PMSDetailLibrary;
import java.util.List;
import org.apache.ibatis.jdbc.SQL;

/**
 * Playlist sql provider.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/18/23
 */
public class PlaylistSqlProvider {
  /**
   * updatePlaylist.
   *
   * @param library a {@link com.daw.pms.Entity.PMS.PMSDetailLibrary} object.
   * @return a {@link java.lang.String} object.
   */
  public String updatePlaylist(PMSDetailLibrary library) {
    String name = library.getName();
    String cover = library.getCover();
    Integer songsCount = library.getItemCount();
    String intro = library.getIntro();
    Long createTime = library.getCreateTime();
    Long updateTime = library.getUpdateTime();
    Long id = library.getId();
    if (id == null) {
      throw new RuntimeException("Playlist id mustn't be null.");
    }
    if (name == null
        && cover == null
        && songsCount == null
        && intro == null
        && createTime == null
        && updateTime == null) {
      throw new RuntimeException("Must specify at least one field to update");
    }
    SQL sql =
        new SQL().UPDATE("tb_pms_playlist t1").JOIN("tb_pms_detail_playlist t2 ON t1.id = t2.id");
    if (name != null) {
      sql.SET("t1.name = #{name}");
    }
    if (cover != null) {
      sql.SET("t1.cover = #{cover}");
    }
    if (songsCount != null) {
      sql.SET("t1.songs_count = #{itemCount}");
    }
    if (intro != null) {
      sql.SET("t2.intro = #{intro}");
    }
    if (createTime != null) {
      sql.SET("t2.create_time = #{createTime}");
    }
    if (updateTime != null) {
      sql.SET("t2.update_time = #{updateTime}");
    }
    sql.WHERE("t1.id = #{id}");
    return sql.toString();
  }

  /**
   * deletePlaylists.
   *
   * @param ids a {@link java.util.List} object.
   * @return a {@link java.lang.String} object.
   */
  public String deletePlaylists(List<Long> ids) {
    return new SQL() {
      {
        DELETE_FROM("tb_pms_playlist");
        if (ids != null) {
          WHERE(getSqlConditionCollection("id", ids));
        }
      }
    }.toString();
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

  /**
   * createPlaylist.
   *
   * @return a {@link java.lang.String} object.
   */
  public String createPlaylist() {
    String createPlaylist =
        new SQL()
            .INSERT_INTO("tb_pms_playlist")
            .VALUES("creator_id", "#{creatorId}")
            .VALUES("name", "#{name}")
            .VALUES("cover", "#{cover}")
            .VALUES("songs_count", "#{itemCount}")
            .toString();

    String createDetailPlaylist =
        new SQL()
            .INSERT_INTO("tb_pms_detail_playlist")
            .VALUES("id", "LAST_INSERT_ID()")
            .VALUES("intro", "#{intro}")
            .VALUES("create_time", "#{createTime}")
            .VALUES("update_time", "#{updateTime}")
            .toString();
    return createPlaylist + ";" + createDetailPlaylist;
  }
}
