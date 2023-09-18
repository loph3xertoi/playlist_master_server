package com.daw.pms.Mapper;

import com.daw.pms.Entity.PMS.PMSDetailLibrary;
import com.daw.pms.Entity.PMS.PMSLibrary;
import com.daw.pms.Provider.PlaylistSqlProvider;
import java.util.List;
import org.apache.ibatis.annotations.*;

/**
 * PlaylistMapper.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/18/23
 */
@Mapper
public interface PlaylistMapper {
  /**
   * Get playlist by playlist's id in pms.
   *
   * @param id Playlist's id in pms.
   * @return The detail playlist in pms.
   */
  @Select("select * from tb_pms_playlist where id = #{id}")
  @Results({
    @Result(property = "bgPic", column = "bg_pic"),
    @Result(property = "creatorId", column = "creator_id"),
    @Result(property = "itemCount", column = "songs_count")
  })
  PMSLibrary getPlaylist(Long id);

  /**
   * Get paged playlists by creator's id in pms.
   *
   * @param creatorId Creator's id in pms.
   * @param offset Offset.
   * @param pageSize Page size.
   * @return Paged playlists with creator's id.
   */
  @Select(
      "select * from tb_pms_playlist where creator_id = #{creatorId} limit #{pageSize} offset #{offset}")
  @Results({
    @Result(property = "bgPic", column = "bg_pic"),
    @Result(property = "creatorId", column = "creator_id"),
    @Result(property = "itemCount", column = "songs_count")
  })
  List<PMSLibrary> getPlaylistsByCreatorId(Long creatorId, int offset, int pageSize);

  /**
   * Get detail playlist by playlist's id in pms.
   *
   * @param id Playlist's id in pms.
   * @return Detail pms library.
   */
  @Select(
      "select * from tb_pms_playlist a join tb_pms_detail_playlist b on a.id = b.id where a.id = #{id}")
  @Results({
    @Result(property = "creatorId", column = "creator_id"),
    @Result(property = "createTime", column = "create_time"),
    @Result(property = "updateTime", column = "update_time"),
    @Result(property = "itemCount", column = "songs_count")
  })
  PMSDetailLibrary getDetailPlaylist(Long id);

  /**
   * Get the count of pms playlists with the creator id.
   *
   * @param creatorId Creator's id of playlist in pms.
   * @return Count of playlists with the creator id.
   */
  @Select("select count(*) from tb_pms_playlist where creator_id = #{creatorId}")
  int getCountOfPlaylistsWithCreatorId(Long creatorId);

  /**
   * Create playlist in pms.
   *
   * @param library The pms detail library to insert.
   * @return The id of created playlist in pms.
   */
  @InsertProvider(type = PlaylistSqlProvider.class, method = "createPlaylist")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int createPlaylist(PMSDetailLibrary library);

  /**
   * Delete playlist in pms by playlist's id.
   *
   * @param id Playlist's id in pms.
   * @return 1 for success, 0 for fail.
   */
  @Delete("delete from tb_pms_playlist where id = #{id}")
  int deletePlaylist(Long id);

  /**
   * Delete playlists in pms by ids.
   *
   * @param ids A list of playlist's ids to delete in pms.
   * @return Count of deleted playlists.
   */
  @DeleteProvider(type = PlaylistSqlProvider.class, method = "deletePlaylists")
  int deletePlaylists(@Param("ids") List<Long> ids);

  /**
   * Update playlist in pms.
   *
   * @param library New pms library to update.
   * @return 1 for success, 0 for fail.
   */
  @UpdateProvider(type = PlaylistSqlProvider.class, method = "updatePlaylist")
  int updatePlaylist(PMSDetailLibrary library);
}
