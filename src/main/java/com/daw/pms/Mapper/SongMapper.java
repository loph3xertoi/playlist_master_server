package com.daw.pms.Mapper;

import com.daw.pms.Entity.PMS.PMSSong;
import com.daw.pms.Entity.Provider.SongSqlProvider;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SongMapper {
  @InsertProvider(type = SongSqlProvider.class, method = "addSong")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  Long addSong(PMSSong song);

  @Delete("delete from tb_pms_song where id = #{id}")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  Long deleteSong(Long id);

  @UpdateProvider(type = SongSqlProvider.class, method = "updateSong")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  Long updateSong(PMSSong song);

  @Select("select * from tb_pms_song where id = #{id}")
  @Results({
    @Result(property = "payPlay", column = "pay_play"),
    @Result(property = "isTakenDown", column = "is_taken_down")
  })
  PMSSong getSong(Long id);

  @Select("select * from tb_pms_song where id in (#{ids})")
  @Results({
    @Result(property = "payPlay", column = "pay_play"),
    @Result(property = "isTakenDown", column = "is_taken_down")
  })
  List<PMSSong> getSongs(List<Long> ids);

  @Select("select * from tb_qqmusic_song where pms_song_id = #{id}")
  @Results({
    @Result(property = "pmsSongId", column = "pms_song_id"),
    @Result(property = "songId", column = "song_id"),
    @Result(property = "songMid", column = "song_mid"),
    @Result(property = "mediaMid", column = "media_mid")
  })
  Map<String, String> getQQMusicSong(Long id);

  @Select("select * from tb_ncm_song where pms_song_id = #{id}")
  @Results({
    @Result(property = "pmsSongId", column = "pms_song_id"),
    @Result(property = "ncmId", column = "ncm_id"),
    @Result(property = "mvId", column = "mv_id")
  })
  Map<String, String> getNCMSong(Long id);

  @Select("select * from tb_bilibili_resource where pms_song_id = #{id}")
  @Results({@Result(property = "pmsSongId", column = "pms_song_id")})
  Map<String, String> getBiliResource(Long id);

  @InsertProvider(type = SongSqlProvider.class, method = "addQQMusicSong")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  Long addQQMusicSong(List<Map<String, String>> params);

  @InsertProvider(type = SongSqlProvider.class, method = "addNCMSong")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  Long addNCMSong(List<Map<String, Long>> params);

  @InsertProvider(type = SongSqlProvider.class, method = "addBiliResource")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  Long addBiliResource(List<Map<String, String>> params);
}
