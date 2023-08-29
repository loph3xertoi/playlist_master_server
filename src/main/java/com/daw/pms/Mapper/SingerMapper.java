package com.daw.pms.Mapper;

import com.daw.pms.Entity.PMS.PMSSinger;
import com.daw.pms.Provider.SingerSqlProvider;
import java.util.List;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SingerMapper {
  @Select("select * from tb_pms_singer where id = #{id}")
  PMSSinger getSingerById(Long id);

  @SelectProvider(type = SingerSqlProvider.class, method = "getSingersByIds")
  List<PMSSinger> getSingersByIds(@Param("ids") List<Long> ids);

  @Select("select * from tb_pms_singer where name = #{name} and type = #{type}")
  PMSSinger getSingerByNameAndType(String name, Integer type);

  @InsertProvider(type = SingerSqlProvider.class, method = "addSinger")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int addSinger(PMSSinger singer);

  @Delete("delete from tb_pms_singer where id = #{id}")
  int deleteSinger(Long id);

  @UpdateProvider(type = SingerSqlProvider.class, method = "updateSinger")
  int updateSinger(PMSSinger singer);
}
