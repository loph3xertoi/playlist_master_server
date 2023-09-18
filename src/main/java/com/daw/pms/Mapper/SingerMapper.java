package com.daw.pms.Mapper;

import com.daw.pms.Entity.PMS.PMSSinger;
import com.daw.pms.Provider.SingerSqlProvider;
import java.util.List;
import org.apache.ibatis.annotations.*;

/**
 * SingerMapper.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/18/23
 */
@Mapper
public interface SingerMapper {
  /**
   * Get singer by singer's id in pms.
   *
   * @param id Singer's id in pms.
   * @return Singer in pms.
   */
  @Select("select * from tb_pms_singer where id = #{id}")
  PMSSinger getSingerById(Long id);

  /**
   * Get all singers by singers id in pms.
   *
   * @param ids A list of ids of these singers.
   * @return A list of pms singers.
   */
  @SelectProvider(type = SingerSqlProvider.class, method = "getSingersByIds")
  List<PMSSinger> getSingersByIds(@Param("ids") List<Long> ids);

  /**
   * Get singer by name and type in pms.
   *
   * @param name Singer's name.
   * @param type Which platform this singer belongs to, 1 for qqmusic, 2 for ncm, 3 for bilibili.
   * @return Singer in pms.
   */
  @Select("select * from tb_pms_singer where name = #{name} and type = #{type}")
  PMSSinger getSingerByNameAndType(String name, Integer type);

  /**
   * Get all singers by song's id in pms.
   *
   * @param songId Song's id in pms.
   * @return A list of pms singers.
   */
  @Select(
      "select b.id, b.type, b.name, b.avatar from tb_song_singer a join tb_pms_singer b on a.fk_singer_id = b.id where a.fk_song_id = #{songId}")
  List<PMSSinger> getAllSingersBySongId(Long songId);

  /**
   * Add singer in pms.
   *
   * @param singer Singer in pms.
   * @return The id of new added singer in pms.
   */
  @InsertProvider(type = SingerSqlProvider.class, method = "addSinger")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int addSinger(PMSSinger singer);

  /**
   * Delete singer by singer's id in pms.
   *
   * @param id Singer's id in pms.
   * @return 1 for success, 0 for fail.
   */
  @Delete("delete from tb_pms_singer where id = #{id}")
  int deleteSinger(Long id);

  /**
   * Update singer information in pms.
   *
   * @param singer Updated singer in pms.
   * @return 1 for success, 0 for fail.
   */
  @UpdateProvider(type = SingerSqlProvider.class, method = "updateSinger")
  int updateSinger(PMSSinger singer);
}
