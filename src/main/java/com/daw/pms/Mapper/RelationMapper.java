package com.daw.pms.Mapper;

import com.daw.pms.Provider.RelationSqlProvider;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.*;

/**
 * RelationMapper.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/18/23
 */
@Mapper
public interface RelationMapper {
  /**
   * Get relation between song and singer by relation's id in pms.
   *
   * @param id Relation's id.
   * @return Relation between song and singer in pms.
   */
  @Select("select * from tb_song_singer where id = #{id}")
  Map<String, Object> getRelationSongSinger(Long id);

  /**
   * Get all singers' ids by song's id in pms.
   *
   * @param id Song's id in pms.
   * @return A list of singer's ids.
   */
  @Select("select fk_singer_id from tb_song_singer where fk_song_id = #{id}")
  List<Long> getAllSingersIdBySongId(Long id);

  /**
   * Get all songs' ids by singer's id in pms.
   *
   * @param id Singer's id in pms.
   * @return A list of song's ids.
   */
  @Select("select fk_song_id from tb_song_singer where fk_singer_id = #{id}")
  List<Long> getAllSongsIdBySingerId(Long id);

  /**
   * Get relation between playlist and song by relation's id in pms.
   *
   * @param id Relation's id.
   * @return The relation between playlist and song in pms.
   */
  @Select("select * from tb_playlist_song where id = #{id}")
  Map<String, Object> getRelationPlaylistSong(Long id);

  /**
   * Get all songs' ids by playlist's id in pms.
   *
   * @param id Playlist's id in pms.
   * @return A list of playlist's ids in pms.
   */
  @Select("select fk_song_id from tb_playlist_song where fk_playlist_id = #{id}")
  List<Long> getAllSongsIdByPlaylistId(Long id);

  /**
   * Get all playlists' ids by song's id in pms.
   *
   * @param id Song's id in pms.
   * @return A list playlist's ids.
   */
  @Select("select fk_playlist_id from tb_playlist_song where fk_song_id = #{id}")
  List<Long> getAllPlaylistsIdBySongId(Long id);

  /**
   * Add relation between song and singer.
   *
   * @param params Map contains relation between song and singer in pms.
   * @return Relation's id of new inserted relation.
   */
  @InsertProvider(type = RelationSqlProvider.class, method = "addRelationSongSinger")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int addRelationSongSinger(@Param("params") List<Map<String, Long>> params);

  /**
   * Add relation between playlist and song.
   *
   * @param params Map contains relation between playlist and song in pms.
   * @return Relation's id of new inserted relation.
   */
  @InsertProvider(type = RelationSqlProvider.class, method = "addRelationPlaylistSong")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int addRelationPlaylistSong(@Param("params") List<Map<String, Long>> params);

  /**
   * Delete relation between playlist and song in pms.
   *
   * @param params Map contains relation between playlist and song in pms.
   * @return 1 for success, 0 for fail.
   */
  @DeleteProvider(type = RelationSqlProvider.class, method = "deleteRelationPlaylistSong")
  int deleteRelationPlaylistSong(Map<String, Object> params);

  /**
   * Get the count of relations between songs and singers in pms.
   *
   * @return Count of relations between songs and singers in pms.
   */
  @Select("select count(*) from tb_song_singer")
  int getCountOfRelationsOfSongSinger();

  /**
   * Get the count of relations between playlists and songs in pms.
   *
   * @return Count of relations between playlists and songs in pms.
   */
  @Select("select count(*) from tb_playlist_song")
  int getCountOfRelationsOfPlaylistSong();
}
