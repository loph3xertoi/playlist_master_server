package com.daw.pms.Mapper;

import com.daw.pms.Entity.PMS.PMSSong;
import com.daw.pms.Provider.SongSqlProvider;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.*;

/**
 * SongMapper.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/18/23
 */
@Mapper
public interface SongMapper {
  /**
   * Add song in pms.
   *
   * @param song The song to add in pms.
   * @return The id of new added song in pms.
   */
  @InsertProvider(type = SongSqlProvider.class, method = "addSong")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int addSong(PMSSong song);

  /**
   * Delete song by id in pms.
   *
   * @param id The id of song to delete.
   * @return 1 for success, 0 for fail.
   */
  @Delete("delete from tb_pms_song where id = #{id}")
  int deleteSong(Long id);

  /**
   * Update song in pms.
   *
   * @param song The new song to update in pms.
   * @return 1 for success, 0 for fail.
   */
  @UpdateProvider(type = SongSqlProvider.class, method = "updateSong")
  int updateSong(PMSSong song);

  /**
   * Get song by id in pms.
   *
   * @param id Song's id in pms.
   * @return Song in pms.
   */
  @Select("select * from tb_pms_song where id = #{id}")
  @Results({
    @Result(property = "payPlay", column = "pay_play"),
    @Result(property = "isTakenDown", column = "is_taken_down")
  })
  PMSSong getSong(Long id);

  /**
   * Get songs by id in pms.
   *
   * @param ids A list of ids of these songs.
   * @return A list of pms songs.
   */
  @SelectProvider(type = SongSqlProvider.class, method = "getSongs")
  @Results({
    @Result(property = "payPlay", column = "pay_play"),
    @Result(property = "isTakenDown", column = "is_taken_down")
  })
  List<PMSSong> getSongs(@Param("ids") List<Long> ids);

  /**
   * Get qqmusic song by corresponding pms song's id.
   *
   * @param id The id of corresponding pms song for qqmusic song.
   * @return Map presenting qqmusic song.
   */
  @Select("select * from tb_qqmusic_song where pms_song_id = #{id}")
  @Results({
    @Result(property = "pmsSongId", column = "pms_song_id"),
    @Result(property = "songId", column = "song_id"),
    @Result(property = "songMid", column = "song_mid"),
    @Result(property = "mediaMid", column = "media_mid")
  })
  Map<String, Object> getQQMusicSong(Long id);

  /**
   * Get qqmusic song by songId.
   *
   * @param songId The songId of qqmusic song.
   * @return Map presenting qqmusic song.
   */
  @Select("select * from tb_qqmusic_song where song_id = #{songId}")
  @Results({
    @Result(property = "pmsSongId", column = "pms_song_id"),
    @Result(property = "songId", column = "song_id"),
    @Result(property = "songMid", column = "song_mid"),
    @Result(property = "mediaMid", column = "media_mid")
  })
  Map<String, String> getQQMusicSongBySongId(Long songId);

  /**
   * Get song's id in pms and songId of qqmusic song if exists according to songIds.
   *
   * @param songIds A list of songIds of qqmusic song.
   * @return A list of song's id in pms and songId of qqmusic song against songIds.
   */
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

  /**
   * Get ncm song by corresponding pms song's id.
   *
   * @param id The id of corresponding pms song for ncm song.
   * @return Map presenting ncm song.
   */
  @Select("select * from tb_ncm_song where pms_song_id = #{id}")
  @Results({
    @Result(property = "pmsSongId", column = "pms_song_id"),
    @Result(property = "ncmId", column = "ncm_id"),
    @Result(property = "mvId", column = "mv_id")
  })
  Map<String, Object> getNCMSong(Long id);

  /**
   * Get ncm song by ncmId.
   *
   * @param ncmId The ncmId of ncm song.
   * @return Map presenting ncm song.
   */
  @Select("select * from tb_ncm_song where ncm_id = #{ncmId}")
  @Results({
    @Result(property = "pmsSongId", column = "pms_song_id"),
    @Result(property = "ncmId", column = "ncm_id"),
    @Result(property = "mvId", column = "mv_id")
  })
  Map<String, Object> getNCMSongByNCMId(Long ncmId);

  /**
   * Get song's id in pms and ncmId of ncm song if exists according to ncmIds.
   *
   * @param ncmIds A list of ncmIds of ncm song.
   * @return A list of song's id in pms and ncmId of ncm song against ncmIds.
   */
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

  /**
   * Get bili resource by corresponding pms song's id.
   *
   * @param id The id of corresponding pms song for bili resource.
   * @return Map presenting bili resource.
   */
  @Select("select * from tb_bilibili_resource where pms_song_id = #{id}")
  @Results({@Result(property = "pmsSongId", column = "pms_song_id")})
  Map<String, Object> getBiliResource(Long id);

  /**
   * Get bili resource by aid.
   *
   * @param aid The aid of bili resource.
   * @return Map presenting bili resource.
   */
  @Select("select * from tb_bilibili_resource where aid = #{aid}")
  @Results({
    @Result(property = "pmsSongId", column = "pms_song_id"),
    @Result(property = "aid", column = "aid"),
    @Result(property = "bvid", column = "bvid")
  })
  Map<String, Object> getBiliResourceByAid(Long aid);

  /**
   * Get song's id in pms and aid of bili resource if exists according to aids.
   *
   * @param aids A list of aids of bili resource.
   * @return A list of song's id in pms and aid of bili resource against aids.
   */
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

  /**
   * Add qqmusic songs to pms.
   *
   * @param params A list of maps that presents qqmusic songs.
   * @return The count of added qqmusic songs.
   */
  @InsertProvider(type = SongSqlProvider.class, method = "addQQMusicSong")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int addQQMusicSong(@Param("params") List<Map<String, String>> params);

  /**
   * Add ncm songs to pms.
   *
   * @param params A list of maps that presents ncm songs.
   * @return The count of added ncm songs.
   */
  @InsertProvider(type = SongSqlProvider.class, method = "addNCMSong")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int addNCMSong(@Param("params") List<Map<String, String>> params);

  /**
   * Add bili resources to pms.
   *
   * @param params A list of maps that presents bili resources.
   * @return The count of added bili resources.
   */
  @InsertProvider(type = SongSqlProvider.class, method = "addBiliResource")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int addBiliResource(@Param("params") List<Map<String, String>> params);
}
