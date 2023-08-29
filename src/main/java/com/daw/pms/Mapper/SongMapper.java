package com.daw.pms.Mapper;

import com.daw.pms.Entity.PMS.PMSSong;
import com.daw.pms.Provider.SongSqlProvider;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SongMapper {
  @InsertProvider(type = SongSqlProvider.class, method = "addSong")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int addSong(PMSSong song);

  @Delete("delete from tb_pms_song where id = #{id}")
  int deleteSong(Long id);

  @UpdateProvider(type = SongSqlProvider.class, method = "updateSong")
  int updateSong(PMSSong song);

  @Select("select * from tb_pms_song where id = #{id}")
  @Results({
    @Result(property = "payPlay", column = "pay_play"),
    @Result(property = "isTakenDown", column = "is_taken_down")
  })
  PMSSong getSong(Long id);

  @SelectProvider(type = SongSqlProvider.class, method = "getSongs")
  @Results({
    @Result(property = "payPlay", column = "pay_play"),
    @Result(property = "isTakenDown", column = "is_taken_down")
  })
  List<PMSSong> getSongs(@Param("ids") List<Long> ids);

  @Select("select * from tb_qqmusic_song where pms_song_id = #{id}")
  @Results({
    @Result(property = "pmsSongId", column = "pms_song_id"),
    @Result(property = "songId", column = "song_id"),
    @Result(property = "songMid", column = "song_mid"),
    @Result(property = "mediaMid", column = "media_mid")
  })
  Map<String, Object> getQQMusicSong(Long id);

  @Select("select * from tb_qqmusic_song where song_id = #{songId}")
  @Results({
    @Result(property = "pmsSongId", column = "pms_song_id"),
    @Result(property = "songId", column = "song_id"),
    @Result(property = "songMid", column = "song_mid"),
    @Result(property = "mediaMid", column = "media_mid")
  })
  Map<String, String> getQQMusicSongBySongId(Long songId);

  @SelectProvider(type = SongSqlProvider.class, method = "getExistedIdsAndSongIdsList")
  List<Map<String, Object>> getExistedIdsAndSongIdsList(@Param("songIds") List<Long> songIds);

  default Map<Long, Long> getExistedIdsAndSongIds(List<Long> songIds) {
    Map<Long, Long> result = new HashMap<>();
    List<Map<String, Object>> existedIdsAndSongIdsList = getExistedIdsAndSongIdsList(songIds);
    for (Map<String, Object> entry : existedIdsAndSongIdsList) {
      result.put(
          Long.valueOf(String.valueOf(entry.get("pms_song_id"))),
          Long.valueOf(String.valueOf(entry.get("song_id"))));
    }
    return result;
  }

  @Select("select * from tb_ncm_song where pms_song_id = #{id}")
  @Results({
    @Result(property = "pmsSongId", column = "pms_song_id"),
    @Result(property = "ncmId", column = "ncm_id"),
    @Result(property = "mvId", column = "mv_id")
  })
  Map<String, Object> getNCMSong(Long id);

  @Select("select * from tb_ncm_song where ncm_id = #{ncmId}")
  @Results({
    @Result(property = "pmsSongId", column = "pms_song_id"),
    @Result(property = "ncmId", column = "ncm_id"),
    @Result(property = "mvId", column = "mv_id")
  })
  Map<String, Object> getNCMSongByNCMId(Long ncmId);

  @SelectProvider(type = SongSqlProvider.class, method = "getExistedIdsAndNCMIdsList")
  List<Map<String, Object>> getExistedIdsAndNCMIdsList(@Param("ncmIds") List<Long> ncmIds);

  default Map<Long, Long> getExistedIdsAndNCMIds(List<Long> ncmIds) {
    Map<Long, Long> result = new HashMap<>();
    List<Map<String, Object>> existedIdsAndNCMIdsList = getExistedIdsAndNCMIdsList(ncmIds);
    for (Map<String, Object> entry : existedIdsAndNCMIdsList) {
      result.put(
          Long.valueOf(String.valueOf(entry.get("pms_song_id"))),
          Long.valueOf(String.valueOf(entry.get("ncm_id"))));
    }
    return result;
  }

  @Select("select * from tb_bilibili_resource where pms_song_id = #{id}")
  @Results({@Result(property = "pmsSongId", column = "pms_song_id")})
  Map<String, Object> getBiliResource(Long id);

  @Select("select * from tb_bilibili_resource where aid = #{aid}")
  @Results({
    @Result(property = "pmsSongId", column = "pms_song_id"),
    @Result(property = "aid", column = "aid"),
    @Result(property = "bvid", column = "bvid")
  })
  Map<String, Object> getBiliResourceByAid(Long aid);

  @SelectProvider(type = SongSqlProvider.class, method = "getExistedIdsAndAidsList")
  List<Map<String, Object>> getExistedIdsAndAidsList(@Param("aids") List<Long> aids);

  default Map<Long, Long> getExistedIdsAndAids(List<Long> aids) {
    Map<Long, Long> result = new HashMap<>();
    List<Map<String, Object>> existedIdsAndAidsList = getExistedIdsAndAidsList(aids);
    for (Map<String, Object> entry : existedIdsAndAidsList) {
      result.put(
          Long.valueOf(String.valueOf(entry.get("pms_song_id"))),
          Long.valueOf(String.valueOf(entry.get("aid"))));
    }
    return result;
  }

  @InsertProvider(type = SongSqlProvider.class, method = "addQQMusicSong")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int addQQMusicSong(@Param("params") List<Map<String, String>> params);

  @InsertProvider(type = SongSqlProvider.class, method = "addNCMSong")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int addNCMSong(@Param("params") List<Map<String, String>> params);

  @InsertProvider(type = SongSqlProvider.class, method = "addBiliResource")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int addBiliResource(@Param("params") List<Map<String, String>> params);
}
