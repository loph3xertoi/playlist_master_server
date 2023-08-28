package com.daw.pms.Entity.Provider;

import com.daw.pms.Entity.PMS.PMSSinger;
import org.apache.ibatis.jdbc.SQL;

public class SingerSqlProvider {
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

  public String addSinger(PMSSinger singer) {
    String name = singer.getName();
    String avatar = singer.getHeadPic();
    Integer type = singer.getType();

    if (name == null) {
      throw new RuntimeException("Singer name mustn't be null");
    }
    if (type == null) {
      throw new RuntimeException("Singer type mustn't be null");
    }
    if (avatar == null) {
      avatar = "";
    }

    return new SQL()
        .INSERT_INTO("tb_pms_singer")
        .VALUES("name", "#{name}")
        .VALUES("avatar", "#{headPic}")
        .VALUES("type", "#{type}")
        .toString();
  }
}
