package com.daw.pms.Mapper;

import com.daw.pms.Entity.PMS.PMSDetailLibrary;
import com.daw.pms.Entity.PMS.PMSLibrary;
import com.daw.pms.Entity.Provider.PlaylistSqlProvider;
import java.util.List;
import org.apache.ibatis.annotations.*;

@Mapper
public interface PlaylistMapper {
  @Select("select * from tb_pms_playlist where id = #{id}")
  @Results({
    @Result(property = "bgPic", column = "bg_pic"),
    @Result(property = "creatorId", column = "creator_id"),
    @Result(property = "itemCount", column = "songs_count")
  })
  PMSLibrary getPlaylist(Long id);

  @Select(
      "select * from tb_pms_playlist where creator_id = #{creatorId} limit #{pageSize} offset #{offset}")
  @Results({
    @Result(property = "bgPic", column = "bg_pic"),
    @Result(property = "creatorId", column = "creator_id"),
    @Result(property = "itemCount", column = "songs_count")
  })
  List<PMSLibrary> getPlaylistByCreatorId(Long creatorId, Integer offset, Integer pageSize);

  @Select(
      "select * from tb_pms_playlist a join tb_pms_detail_playlist b on a.id = b.id where a.id = #{id}")
  @Results({
    @Result(property = "creatorId", column = "creator_id"),
    @Result(property = "createTime", column = "create_time"),
    @Result(property = "updateTime", column = "update_time"),
    @Result(property = "itemCount", column = "songs_count")
  })
  PMSDetailLibrary getDetailPlaylist(Long id);

  @Select("select count(*) from tb_pms_playlist where creator_id = #{creatorId}")
  Integer getCountOfUserPlaylists(Long creatorId);

  @InsertProvider(type = PlaylistSqlProvider.class, method = "createPlaylist")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  Long createPlaylist(PMSDetailLibrary library);

  @Delete("delete from tb_pms_playlist where id = #{id}")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  Long deletePlaylist(Long id);

  @DeleteProvider(type = PlaylistSqlProvider.class, method = "deletePlaylists")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  Long deletePlaylists(@Param("ids") List<Long> ids);

  @UpdateProvider(type = PlaylistSqlProvider.class, method = "updatePlaylist")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  Long updatePlaylist(PMSDetailLibrary library);
}
