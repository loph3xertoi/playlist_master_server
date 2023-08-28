package com.daw.pms.Mapper;

import com.daw.pms.Entity.PMS.PMSSinger;
import com.daw.pms.Entity.Provider.SingerSqlProvider;
import java.util.List;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SingerMapper {
  @Select("select * from tb_pms_singer where id = #{id}")
  PMSSinger getSinger(Long id);

  @Select("select * from tb_pms_singer where id in (#{ids})")
  List<PMSSinger> getSingers(List<Long> ids);

  @InsertProvider(type = SingerSqlProvider.class, method = "addSinger")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  Long addSinger(PMSSinger singer);

  @Delete("delete from tb_pms_singer where id = #{id}")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  Long deleteSinger(Long id);

  @UpdateProvider(type = SingerSqlProvider.class, method = "updateSinger")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  Long updateSinger(PMSSinger singer);
}
