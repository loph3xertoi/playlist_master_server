package com.daw.pms.Provider;

import com.daw.pms.Entity.PMS.PMSSinger;
import java.util.List;
import org.apache.ibatis.jdbc.SQL;

/**
 * Singer sql provider.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/18/23
 */
public class SingerSqlProvider {
  /**
   * updateSinger.
   *
   * @param singer a {@link com.daw.pms.Entity.PMS.PMSSinger} object.
   * @return a {@link java.lang.String} object.
   */
  public String updateSinger(PMSSinger singer) {
    String name = singer.getName();
    String avatar = singer.getHeadPic();
    Integer type = singer.getType();
    Long id = singer.getId();
    if (id == null) {
      throw new RuntimeException("Singer id mustn't be null.");
    }
    if (name == null && avatar == null && type == null) {
      throw new RuntimeException("Must specify at least one field to update");
    }
    SQL sql = new SQL().UPDATE("tb_pms_singer");
    if (name != null) {
      sql.SET("name = #{name}");
    }
    if (avatar != null) {
      sql.SET("avatar = #{headPic}");
    }
    if (type != null) {
      sql.SET("type = #{type}");
    }
    sql.WHERE("id = #{id}");
    return sql.toString();
  }

  /**
   * addSinger.
   *
   * @param singer a {@link com.daw.pms.Entity.PMS.PMSSinger} object.
   * @return a {@link java.lang.String} object.
   */
  public String addSinger(PMSSinger singer) {
    String name = singer.getName();
    Integer type = singer.getType();

    if (name == null) {
      throw new RuntimeException("Singer name mustn't be null");
    }
    if (type == null) {
      throw new RuntimeException("Singer type mustn't be null");
    }

    return new SQL()
        .INSERT_INTO("tb_pms_singer")
        .VALUES("name", "#{name}")
        .VALUES("avatar", "#{headPic}")
        .VALUES("type", "#{type}")
        .toString();
  }

  /**
   * getSingersByIds.
   *
   * @param ids a {@link java.util.List} object.
   * @return a {@link java.lang.String} object.
   */
  public String getSingersByIds(List<Long> ids) {
    return new SQL() {
      { // @Select("select * from tb_pms_singer where id in (#{ids})")
        SELECT("*");
        FROM("tb_pms_singer");
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
}
