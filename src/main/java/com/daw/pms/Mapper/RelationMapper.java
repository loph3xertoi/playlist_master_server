package com.daw.pms.Mapper;

import com.daw.pms.Provider.RelationSqlProvider;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.*;

@Mapper
public interface RelationMapper {
  @Select("select * from tb_song_singer where id = #{id}")
  Map<String, Long> getRelationSongSinger(Long id);

  @Select("select fk_singer_id from tb_song_singer where fk_song_id = #{id}")
  List<Long> getAllSingersIdBySongId(Long id);

  @Select("select fk_song_id from tb_song_singer where fk_singer_id = #{id}")
  List<Long> getAllSongsIdBySingerId(Long id);

  @Select("select * from tb_playlist_song where id = #{id}")
  Map<String, Long> getRelationPlaylistSong(Long id);

  @Select("select fk_song_id from tb_playlist_song where fk_playlist_id = #{id}")
  List<Long> getAllSongsIdByPlaylistId(Long id);

  @Select("select fk_playlist_id from tb_playlist_song where fk_song_id = #{id}")
  List<Long> getAllPlaylistsIdBySongId(Long id);

  @InsertProvider(type = RelationSqlProvider.class, method = "addRelationSongSinger")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int addRelationSongSinger(@Param("params") List<Map<String, Long>> params);

  @InsertProvider(type = RelationSqlProvider.class, method = "addRelationPlaylistSong")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int addRelationPlaylistSong(@Param("params") List<Map<String, Long>> params);

  @DeleteProvider(type = RelationSqlProvider.class, method = "deleteRelationPlaylistSong")
  int deleteRelationPlaylistSong(Map<String, Object> params);

  @Select("select count(*) from tb_song_singer")
  int getRowCountOfRelationSongSinger();

  @Select("select count(*) from tb_playlist_song")
  int getRowCountOfRelationPlaylistSong();
}
