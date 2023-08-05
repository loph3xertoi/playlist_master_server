package com.daw.pms.Service.NeteaseCloudMusic.impl;

import com.daw.pms.Config.NCMAPI;
import com.daw.pms.DTO.PagedDataDTO;
import com.daw.pms.DTO.Result;
import com.daw.pms.Entity.Basic.BasicSinger;
import com.daw.pms.Entity.NeteaseCloudMusic.*;
import com.daw.pms.Service.NeteaseCloudMusic.NCMPlaylistService;
import com.daw.pms.Service.NeteaseCloudMusic.NCMSongService;
import com.daw.pms.Utils.HttpTools;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class NCMPlaylistServiceImpl implements NCMPlaylistService {

  private final HttpTools httpTools;
  private final NCMSongService ncmSongService;

  public NCMPlaylistServiceImpl(HttpTools httpTools, NCMSongService ncmSongService) {
    this.httpTools = httpTools;
    this.ncmSongService = ncmSongService;
  }

  /**
   * Get all playlists of user {@code uid} in netease cloud music.
   *
   * @param uid Your user id in netease cloud music.
   * @param offset Offset from the first song.
   * @param limit The number of songs returned by this query.
   * @param cookie Your cookie for netease cloud music.
   * @return All playlists created by {@code uid}, wrapped with Result DTO, the data is
   *     PagedDataDTO<BiliFavList>.
   * @apiNote GET /user/playlist?uid={@code uid}&offset={@code offset}&limit={@code limit}
   */
  @Override
  public Result getPlaylists(Long uid, Integer offset, Integer limit, String cookie) {
    PagedDataDTO<NCMPlaylist> data = new PagedDataDTO<>();
    String baseUrl = httpTools.ncmHost + ":" + httpTools.ncmPort;
    List<NCMPlaylist> playlists =
        extractNCMPlaylists(
            httpTools.requestGetAPI(
                baseUrl + NCMAPI.USER_PLAYLIST,
                new HashMap<String, String>() {
                  {
                    put("uid", uid.toString());
                    put("offset", offset.toString());
                    put("limit", limit.toString());
                  }
                },
                Optional.of(cookie)));
    // TODO: the count and hasMore need to be repaired.
    data.setCount(playlists.size());
    data.setHasMore(false);
    data.setList(playlists);
    return Result.ok(data);
  }

  /**
   * Resolve playlists from raw json playlists string.
   *
   * @param rawPlaylists Raw playlists string.
   * @return A list of BiliFavList.
   */
  private List<NCMPlaylist> extractNCMPlaylists(String rawPlaylists) {
    List<NCMPlaylist> playlists = new ArrayList<>();
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(rawPlaylists);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    JsonNode playlistsNode = jsonNode.get("playlist");
    for (JsonNode playlistNode : playlistsNode) {
      NCMPlaylist playlist = new NCMPlaylist();
      playlist.setName(playlistNode.get("name").textValue());
      playlist.setCover(playlistNode.get("coverImgUrl").textValue());
      playlist.setItemCount(playlistNode.get("trackCount").intValue());
      playlist.setId(playlistNode.get("id").longValue());
      playlists.add(playlist);
    }
    return playlists;
  }

  /**
   * Get detail playlist with {@code id}.
   *
   * @param id The playlist's global id.
   * @param cookie Your cookie for netease cloud music.
   * @return Detail playlist wrapped with Result DTO, the data is NCMDetailPlaylist.
   * @apiNote GET /playlist/detail?id={@code id}
   */
  @Override
  public Result getDetailPlaylist(Long id, String cookie) {
    String baseUrl = httpTools.ncmHost + ":" + httpTools.ncmPort;
    NCMDetailPlaylist playlist =
        extractDetailNCMPlaylist(
            httpTools.requestGetAPI(
                baseUrl + NCMAPI.PLAYLIST_DETAIL,
                new HashMap<String, String>() {
                  {
                    put("id", id.toString());
                  }
                },
                Optional.of(cookie)));
    List<NCMSong> songs = getAllSongsFromPlaylist(id, Optional.empty(), Optional.empty(), cookie);
    playlist.setSongs(songs);
    return Result.ok(playlist);
  }

  /**
   * Get all songs from playlist with {@code id}.
   *
   * @param id The playlist's global id.
   * @param offset Offset from the first song.
   * @param limit The number of songs returned by this query.
   * @param cookie Your cookie for netease cloud music.
   * @return The first {@code limit} songs start from {@code offset} of playlist.
   */
  @Override
  public List<NCMSong> getAllSongsFromPlaylist(
      Long id, Optional<Integer> offset, Optional<Integer> limit, String cookie) {
    String baseUrl = httpTools.ncmHost + ":" + httpTools.ncmPort;
    List<NCMSong> songs =
        extractAllSongs(
            httpTools.requestGetAPI(
                baseUrl + NCMAPI.PLAYLIST_TRACK_ALL,
                new HashMap<String, String>() {
                  {
                    put("id", id.toString());
                    offset.ifPresent(integer -> put("offset", integer.toString()));
                    limit.ifPresent(integer -> put("limit", integer.toString()));
                  }
                },
                Optional.of(cookie)));
    String ids =
        songs.stream().map(song -> song.getId().toString()).collect(Collectors.joining(","));
    Map<String, String> links;
    Result linksResult = ncmSongService.getSongsLink(ids, "standard", cookie);
    if (linksResult.getSuccess()) {
      links = (Map<String, String>) linksResult.getData();
    } else {
      throw new RuntimeException(linksResult.getMessage());
    }
    for (NCMSong song : songs) {
      song.setSongLink(links.getOrDefault(song.getId().toString(), ""));
      song.setIsTakenDown(song.getSongLink().isEmpty());
    }
    return songs;
  }

  private List<NCMSong> extractAllSongs(String rawAllSongs) {
    List<NCMSong> songs = new ArrayList<>();
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(rawAllSongs);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    JsonNode songsNode = jsonNode.get("songs");
    for (JsonNode songNode : songsNode) {
      NCMSong song = new NCMSong();
      song.setId(songNode.get("id").longValue());
      song.setMvId(songNode.get("mv").longValue());
      song.setName(songNode.get("name").textValue());

      List<BasicSinger> singers = new ArrayList<>();
      JsonNode singersNode = songNode.get("ar");
      for (JsonNode singerNode : singersNode) {
        NCMSinger singer = new NCMSinger();
        singer.setId(singerNode.get("id").longValue());
        singer.setName(singerNode.get("name").textValue());
        singers.add(singer);
      }
      song.setSingers(singers);

      song.setCover(songNode.get("al").get("picUrl").textValue());
      song.setPayPlay(songNode.get("fee").intValue());
      songs.add(song);
    }
    return songs;
  }

  private NCMDetailPlaylist extractDetailNCMPlaylist(String rawDetailNCMPlaylist) {
    NCMDetailPlaylist playlist = new NCMDetailPlaylist();
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(rawDetailNCMPlaylist);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    JsonNode playlistNode = jsonNode.get("playlist");
    playlist.setName(playlistNode.get("name").textValue());
    playlist.setCover(playlistNode.get("coverImgUrl").textValue());
    playlist.setItemCount(playlistNode.get("trackCount").intValue());
    playlist.setId(playlistNode.get("id").longValue());
    playlist.setTrackUpdateTime(playlistNode.get("trackUpdateTime").longValue());
    playlist.setUpdateTime(playlistNode.get("updateTime").longValue());
    playlist.setCreateTime(playlistNode.get("createTime").longValue());
    playlist.setPlayCount(playlistNode.get("playCount").intValue());
    playlist.setDescription(
        playlistNode.get("description").isNull()
            ? ""
            : playlistNode.get("description").textValue());
    JsonNode tagsNode = playlistNode.get("tags");
    List<String> tags = new ArrayList<>();
    for (JsonNode tagNode : tagsNode) {
      tags.add(tagNode.textValue());
    }
    playlist.setTags(tags);
    return playlist;
  }

  /**
   * Create playlist.
   *
   * @param name The name of playlist.
   * @param cookie Your cookie for netease cloud music.
   * @return The response of request wrapped by Result DTO.
   * @apiNote GET /playlist/create?name={@code name}
   */
  @Override
  public Result createPlaylist(String name, String cookie) {
    String baseUrl = httpTools.ncmHost + ":" + httpTools.ncmPort;
    return extractCreatingPlaylistResult(
        httpTools.requestGetAPI(
            baseUrl + NCMAPI.CREATE_PLAYLIST,
            new HashMap<String, String>() {
              {
                put("name", name);
              }
            },
            Optional.of(cookie)));
  }

  private Result extractCreatingPlaylistResult(String rawCreatingPlaylistResult) {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(rawCreatingPlaylistResult);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    int resultCode = jsonNode.get("code").intValue();
    if (resultCode == 200) {
      Long id = jsonNode.get("id").longValue();
      return Result.ok(id);
    } else {
      return Result.fail(rawCreatingPlaylistResult);
    }
  }

  /**
   * Delete playlist with ids {@code ids}.
   *
   * @param ids The id of playlist you want to delete, multiple id separated with comma.
   * @param cookie Your cookie for netease cloud music.
   * @return The response of request wrapped by Result DTO.
   * @apiNote GET /playlist/delete?id={@code ids}
   */
  @Override
  public Result deletePlaylist(String ids, String cookie) {
    String baseUrl = httpTools.ncmHost + ":" + httpTools.ncmPort;
    return extractDeletingPlaylistResult(
        httpTools.requestGetAPI(
            baseUrl + NCMAPI.DELETE_PLAYLIST,
            new HashMap<String, String>() {
              {
                put("id", ids);
              }
            },
            Optional.of(cookie)));
  }

  private Result extractDeletingPlaylistResult(String rawDeletingPlaylistResult) {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(rawDeletingPlaylistResult);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    int resultCode = jsonNode.get("code").intValue();
    if (resultCode == 200) {
      return Result.ok();
    } else if (resultCode == 400) {
      return Result.fail("No such library");
    } else {
      return Result.fail(rawDeletingPlaylistResult);
    }
  }

  /**
   * Add songs with tracks {@code tracks} to playlist with pid {@code pid}
   *
   * @param pid The id of the playlist.
   * @param tracks The id of songs, multiple id separated with comma.
   * @param cookie Your cookie for netease cloud music.
   * @return The response of request wrapped by Result DTO.
   * @apiNote GET /playlist/tracks?op=add&pid={@code pid}&tracks={@code tracks}
   */
  @Override
  public Result addSongsToPlaylist(Long pid, String tracks, String cookie) {
    String baseUrl = httpTools.ncmHost + ":" + httpTools.ncmPort;
    return extractAddingSongsToPlaylistResult(
        httpTools.requestGetAPI(
            baseUrl + NCMAPI.OPERATE_SONGS_IN_PLAYLIST,
            new HashMap<String, String>() {
              {
                put("op", "add");
                put("pid", pid.toString());
                put("tracks", tracks);
              }
            },
            Optional.of(cookie)));
  }

  private Result extractAddingSongsToPlaylistResult(String rawAddingSongsToPlaylistResult) {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(rawAddingSongsToPlaylistResult);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

    if (jsonNode.has("code") && jsonNode.get("code").intValue() == 404) {
      return Result.fail("No such library");
    }
    if (jsonNode.has("status") && jsonNode.get("status").intValue() == 200) {
      int resultCode = jsonNode.get("body").get("code").intValue();
      if (resultCode == 200) {
        return Result.ok();
      } else if (resultCode == 502) {
        return Result.fail("The songs is already in the playlist");
      } else if (resultCode == 400) {
        return Result.fail("Invalid parameters");
      }
    }
    return Result.fail(rawAddingSongsToPlaylistResult);
  }

  /**
   * Move songs {@code tracks} from playlist with {@code fromPid} to playlist with {@code toPid}.
   *
   * @param tracks Songs id to be moved, multiple songs id separated with comma.
   * @param fromPid Source playlist's pid.
   * @param toPid Target playlist's pid.
   * @param cookie Your cookie for netease cloud music.
   * @return The response of request wrapped by Result DTO.
   * @apiNote GET /playlist/tracks?op=mov&fromPid={@code fromPid}&toPid={@code toPid}&tracks={@code
   *     tracks}
   */
  @Override
  public Result moveSongsToOtherPlaylist(String tracks, Long fromPid, Long toPid, String cookie) {
    Result addResult = addSongsToPlaylist(toPid, tracks, cookie);
    if (addResult.getSuccess()) {
      Result removeResult = removeSongsFromPlaylist(fromPid, tracks, cookie);
      if (removeResult.getSuccess()) {
        return Result.ok();
      } else {
        return Result.fail(removeResult.getMessage());
      }
    } else {
      return Result.fail(addResult.getMessage());
    }
  }

  /**
   * Remove songs with song id {@code tracks} from playlist with pid {@code pid}.
   *
   * @param pid The id of playlist that you want to remove songs from.
   * @param tracks The songs' id, multiple songs id separated with comma.
   * @param cookie Your cookie for netease cloud music.
   * @return The response of request wrapped by Result DTO.
   * @apiNote GET /playlist/tracks?op=del&pid={@code pid}&tracks={@code tracks}
   */
  @Override
  public Result removeSongsFromPlaylist(Long pid, String tracks, String cookie) {
    String baseUrl = httpTools.ncmHost + ":" + httpTools.ncmPort;
    return extractRemovingSongsFromPlaylistResult(
        httpTools.requestGetAPI(
            baseUrl + NCMAPI.OPERATE_SONGS_IN_PLAYLIST,
            new HashMap<String, String>() {
              {
                put("op", "del");
                put("pid", pid.toString());
                put("tracks", tracks);
              }
            },
            Optional.of(cookie)));
  }

  private Result extractRemovingSongsFromPlaylistResult(String rawRemovingSongsFromPlaylistResult) {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(rawRemovingSongsFromPlaylistResult);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

    if (jsonNode.has("code") && jsonNode.get("code").intValue() == 404) {
      return Result.fail("No such library");
    }
    if (jsonNode.has("status") && jsonNode.get("status").intValue() == 200) {
      int resultCode = jsonNode.get("body").get("code").intValue();
      if (resultCode == 200) {
        return Result.ok();
      } else if (resultCode == 400) {
        return Result.fail("Invalid parameters");
      }
    }
    return Result.fail(rawRemovingSongsFromPlaylistResult);
  }
}
