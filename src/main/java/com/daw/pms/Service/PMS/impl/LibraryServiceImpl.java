package com.daw.pms.Service.PMS.impl;

import com.daw.pms.DTO.PagedDataDTO;
import com.daw.pms.DTO.Result;
import com.daw.pms.DTO.UpdateLibraryDTO;
import com.daw.pms.Entity.Basic.BasicSinger;
import com.daw.pms.Entity.Basic.BasicSong;
import com.daw.pms.Entity.Bilibili.BiliResource;
import com.daw.pms.Entity.NeteaseCloudMusic.NCMSong;
import com.daw.pms.Entity.PMS.*;
import com.daw.pms.Entity.QQMusic.QQMusicSong;
import com.daw.pms.Mapper.PlaylistMapper;
import com.daw.pms.Mapper.RelationMapper;
import com.daw.pms.Mapper.SingerMapper;
import com.daw.pms.Mapper.SongMapper;
import com.daw.pms.Service.BiliBili.BiliFavListService;
import com.daw.pms.Service.NeteaseCloudMusic.NCMPlaylistService;
import com.daw.pms.Service.PMS.LibraryService;
import com.daw.pms.Service.QQMusic.QQMusicPlaylistService;
import com.daw.pms.Utils.PmsUserDetailsUtil;
import com.daw.pms.Utils.QiniuOSS;
import com.qiniu.common.QiniuException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import lombok.Synchronized;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service for handle playlists or favorites.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 7/11/23
 */
@Service
public class LibraryServiceImpl implements LibraryService, Serializable {
  private final QiniuOSS qiniuOSS;
  private final PmsUserDetailsUtil pmsUserDetailsUtil;
  private final QQMusicPlaylistService qqMusicPlaylistService;
  private final NCMPlaylistService ncmPlaylistService;
  private final BiliFavListService biliFavListService;
  private final PlaylistMapper playlistMapper;
  private final RelationMapper relationMapper;
  private final SongMapper songMapper;
  private final SingerMapper singerMapper;

  /**
   * Constructor for LibraryServiceImpl.
   *
   * @param qiniuOSS a {@link com.daw.pms.Utils.QiniuOSS} object.
   * @param pmsUserDetailsUtil a {@link com.daw.pms.Utils.PmsUserDetailsUtil} object.
   * @param qqMusicPlaylistService a {@link com.daw.pms.Service.QQMusic.QQMusicPlaylistService}
   *     object.
   * @param ncmPlaylistService a {@link com.daw.pms.Service.NeteaseCloudMusic.NCMPlaylistService}
   *     object.
   * @param biliFavListService a {@link com.daw.pms.Service.BiliBili.BiliFavListService} object.
   * @param playlistMapper a {@link com.daw.pms.Mapper.PlaylistMapper} object.
   * @param relationMapper a {@link com.daw.pms.Mapper.RelationMapper} object.
   * @param songMapper a {@link com.daw.pms.Mapper.SongMapper} object.
   * @param singerMapper a {@link com.daw.pms.Mapper.SingerMapper} object.
   */
  public LibraryServiceImpl(
      QiniuOSS qiniuOSS,
      PmsUserDetailsUtil pmsUserDetailsUtil,
      QQMusicPlaylistService qqMusicPlaylistService,
      NCMPlaylistService ncmPlaylistService,
      BiliFavListService biliFavListService,
      PlaylistMapper playlistMapper,
      RelationMapper relationMapper,
      SongMapper songMapper,
      SingerMapper singerMapper) {
    this.qiniuOSS = qiniuOSS;
    this.pmsUserDetailsUtil = pmsUserDetailsUtil;
    this.qqMusicPlaylistService = qqMusicPlaylistService;
    this.ncmPlaylistService = ncmPlaylistService;
    this.biliFavListService = biliFavListService;
    this.playlistMapper = playlistMapper;
    this.relationMapper = relationMapper;
    this.songMapper = songMapper;
    this.singerMapper = singerMapper;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Get all libraries for specific platform.
   */
  @Override
  public Result getLibraries(
      Long id, Integer pn, Integer ps, String biliPlatform, Integer type, Integer platform) {
    Result result;
    if (platform == 0) {
      Long pmsUserId = pmsUserDetailsUtil.getCurrentLoginUserId();
      PagedDataDTO<PMSLibrary> pagedDataDTO = new PagedDataDTO<>();
      int playlistCount = playlistMapper.getCountOfPlaylistsWithCreatorId(pmsUserId);
      pagedDataDTO.setCount(playlistCount);
      if (pn == null || ps == null) {
        throw new RuntimeException("Must specify pn and ps");
      }
      pagedDataDTO.setHasMore(pn * ps < playlistCount);
      List<PMSLibrary> playlists = playlistMapper.getPlaylistsByCreatorId(id, ps * (pn - 1), ps);
      for (PMSLibrary playlist : playlists) {
        String cover = playlist.getCover();
        try {
          String reflectedCoverUrl = qiniuOSS.reflectKeyToFinalOSSLink(cover);
          playlist.setCover(reflectedCoverUrl);
        } catch (UnsupportedEncodingException | QiniuException e) {
          throw new RuntimeException(e);
        }
      }
      pagedDataDTO.setList(playlists);
      //      result = Result.fail("Test");
      result = Result.ok(pagedDataDTO);
    } else if (platform == 1) {
      // TODO: need to implement pagination for qqmusic.
      result =
          qqMusicPlaylistService.getPlaylists(
              pmsUserDetailsUtil.getCurrentLoginUserQqMusicId().toString(),
              pmsUserDetailsUtil.getCurrentLoginUserQqMusicCookie());
    } else if (platform == 2) {
      // TODO: need to implement pagination for ncm.
      result =
          ncmPlaylistService.getPlaylists(
              pmsUserDetailsUtil.getCurrentLoginUserNcmId(),
              0,
              1000,
              pmsUserDetailsUtil.getCurrentLoginUserNcmCookie());
    } else if (platform == 3) {
      result =
          biliFavListService.getFavLists(
              pn,
              ps,
              pmsUserDetailsUtil.getCurrentLoginUserBilibiliId(),
              biliPlatform,
              type,
              pmsUserDetailsUtil.getCurrentLoginUserBiliCookie());
    } else {
      throw new RuntimeException("Invalid platform");
    }
    return result;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Get detail library with {@code libraryId} in {@code platform}.
   */
  @Override
  public Result getDetailLibrary(
      String libraryId,
      Integer pn,
      Integer ps,
      String keyword,
      String order,
      Integer range,
      Integer type,
      Integer platform) {
    Result result;
    if (platform == 0) {
      PMSDetailLibrary detailPlaylist = playlistMapper.getDetailPlaylist(Long.valueOf(libraryId));
      Long playlistId = detailPlaylist.getId();
      List<Long> songsId = relationMapper.getAllSongsIdByPlaylistId(playlistId);
      List<PMSSong> songs = new ArrayList<>(songsId.size());
      if (!songsId.isEmpty()) {
        songs = songMapper.getSongs(songsId);
        for (PMSSong song : songs) {
          List<Long> singersId = relationMapper.getAllSingersIdBySongId(song.getId());
          List<PMSSinger> singers = singerMapper.getSingersByIds(singersId);
          List<BasicSinger> basicSingers = new ArrayList<>(singers);
          song.setSingers(basicSingers);
        }
      }
      String cover = detailPlaylist.getCover();
      try {
        String reflectedCoverUrl = qiniuOSS.reflectKeyToFinalOSSLink(cover);
        detailPlaylist.setCover(reflectedCoverUrl);
      } catch (UnsupportedEncodingException | QiniuException e) {
        throw new RuntimeException(e);
      }
      detailPlaylist.setSongs(songs);
      result = Result.ok(detailPlaylist);
    } else if (platform == 1) {
      result =
          qqMusicPlaylistService.getDetailPlaylist(
              libraryId, pmsUserDetailsUtil.getCurrentLoginUserQqMusicCookie());
    } else if (platform == 2) {
      result =
          ncmPlaylistService.getDetailPlaylist(
              Long.valueOf(libraryId), pmsUserDetailsUtil.getCurrentLoginUserNcmCookie());
    } else if (platform == 3) {
      result =
          biliFavListService.getDetailFavList(
              Long.valueOf(libraryId),
              pn,
              ps,
              keyword,
              order,
              range,
              type,
              pmsUserDetailsUtil.getCurrentLoginUserBiliCookie());
    } else {
      throw new RuntimeException("Invalid platform");
    }
    return result;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Create new library.
   */
  @Override
  @Transactional
  public Result createLibrary(Map<String, String> library, Integer platform) {
    Result result;
    if (platform == 0) {
      PMSDetailLibrary pmsDetailLibrary = new PMSDetailLibrary();
      pmsDetailLibrary.setCreatorId(1L);
      String name = library.get("name");
      if (name == null) {
        throw new RuntimeException("Playlist name mustn't be null");
      }
      pmsDetailLibrary.setName(library.get("name"));
      pmsDetailLibrary.setCover("default_library_cover");
      pmsDetailLibrary.setItemCount(0);
      long currentTimeMillis = System.currentTimeMillis();
      pmsDetailLibrary.setCreateTime(currentTimeMillis);
      pmsDetailLibrary.setUpdateTime(currentTimeMillis);
      String intro = library.get("intro");
      if (intro == null || intro.isEmpty()) {
        intro = "";
      }
      pmsDetailLibrary.setIntro(intro);
      int createdPlaylistCount = playlistMapper.createPlaylist(pmsDetailLibrary);
      if (createdPlaylistCount != 0) {
        result = Result.ok(pmsDetailLibrary.getId());
      } else {
        result = Result.fail("Fail to create playlist");
      }
    } else if (platform == 1) {
      result =
          qqMusicPlaylistService.createPlaylist(
              library.get("name"), pmsUserDetailsUtil.getCurrentLoginUserQqMusicCookie());
    } else if (platform == 2) {
      result =
          ncmPlaylistService.createPlaylist(
              library.get("name"), pmsUserDetailsUtil.getCurrentLoginUserNcmCookie());
    } else if (platform == 3) {
      result =
          biliFavListService.createFavList(
              library, pmsUserDetailsUtil.getCurrentLoginUserBiliCookie());
    } else {
      throw new RuntimeException("Invalid platform");
    }
    return result;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Update library.
   */
  @Override
  @Transactional
  public Result updateLibrary(UpdateLibraryDTO library, Integer platform) {
    Result result;
    if (platform == 0) {
      PMSDetailLibrary pmsDetailLibrary = new PMSDetailLibrary();
      pmsDetailLibrary.setId(library.getId());
      pmsDetailLibrary.setCreatorId(1L);
      String name = library.getName();
      if (name == null) {
        throw new RuntimeException("Playlist name mustn't be null");
      }
      pmsDetailLibrary.setName(library.getName());
      MultipartFile cover = library.getCover();
      String coverString;
      if (cover != null && !cover.isEmpty()) {
        PMSLibrary playlist = playlistMapper.getPlaylist(library.getId());
        String playlistCover = playlist.getCover();
        boolean isDefaultCover = "default_library_cover".equals(playlistCover);
        if (isDefaultCover) {
          playlistCover = null;
        }
        try {
          InputStream fileInputStream = cover.getInputStream();
          coverString = qiniuOSS.uploadFileByInputStream(fileInputStream, playlistCover);
          if (isDefaultCover) {
            pmsDetailLibrary.setCover(coverString);
          }
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }

      long currentTimeMillis = System.currentTimeMillis();
      pmsDetailLibrary.setUpdateTime(currentTimeMillis);
      String intro = library.getIntro();
      if (intro == null || intro.isEmpty()) {
        intro = "";
      }
      pmsDetailLibrary.setIntro(intro);
      int updatedPlaylistCount = playlistMapper.updatePlaylist(pmsDetailLibrary);
      if (updatedPlaylistCount != 0) {
        result = Result.ok(updatedPlaylistCount);
      } else {
        result = Result.fail("Fail to update playlist");
      }
    } else if (platform == 1) {
      throw new RuntimeException("Not yet implement qqmusic platform");
    } else if (platform == 2) {
      throw new RuntimeException("Not yet implement ncm platform");
    } else if (platform == 3) {
      throw new RuntimeException("Not yet implement bilibili platform");
    } else {
      throw new RuntimeException("Invalid platform");
    }
    return result;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Delete the library.
   */
  @Override
  @Transactional
  public Result deleteLibrary(String libraryId, Integer platform) {
    Result result;
    if (platform == 0) {
      List<Long> ids =
          Arrays.stream(libraryId.split(",")).map(Long::parseLong).collect(Collectors.toList());
      int deletedPlaylistsCount = playlistMapper.deletePlaylists(ids);
      if (deletedPlaylistsCount != 0) {
        result = Result.ok(deletedPlaylistsCount);
      } else {
        result = Result.fail("Fail to delete library");
      }
    } else if (platform == 1) {
      result =
          qqMusicPlaylistService.deletePlaylist(
              libraryId, pmsUserDetailsUtil.getCurrentLoginUserQqMusicCookie());
    } else if (platform == 2) {
      result =
          ncmPlaylistService.deletePlaylist(
              libraryId, pmsUserDetailsUtil.getCurrentLoginUserNcmCookie());
    } else if (platform == 3) {
      result =
          biliFavListService.deleteFavList(
              libraryId, pmsUserDetailsUtil.getCurrentLoginUserBiliCookie());
    } else {
      throw new RuntimeException("Invalid platform");
    }
    return result;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Add songs {@code songsId} to library {@code libraryId} in platform {@code platform}.
   */
  @Override
  @Transactional
  public Result addSongsToLibrary(
      String libraryId,
      String biliSourceFavListId,
      String songsIds,
      List<BasicSong> songs,
      List<BiliResource> resources,
      Boolean isAddToPMSLibrary,
      Boolean isFavoriteSearchedResource,
      Integer platform) {
    Result result;
    if (platform == 0) {
      result = addPMSSongsToPMSLibrary(libraryId, songsIds);
    } else if (platform == 1) {
      if (isAddToPMSLibrary) {
        result = addQQMusicSongsToPMSLibrary(libraryId, songs);
      } else {
        result =
            qqMusicPlaylistService.addSongsToPlaylist(
                Integer.parseInt(libraryId),
                songsIds,
                pmsUserDetailsUtil.getCurrentLoginUserQqMusicCookie());
      }
    } else if (platform == 2) {
      if (isAddToPMSLibrary) {
        result = addNCMSongsToPMSLibrary(libraryId, songs);
      } else {
        result =
            ncmPlaylistService.addSongsToPlaylist(
                Long.valueOf(libraryId),
                songsIds,
                pmsUserDetailsUtil.getCurrentLoginUserNcmCookie());
      }
    } else if (platform == 3) {
      if (isAddToPMSLibrary) {
        result = addBiliResourcesToPMSLibrary(libraryId, resources);
      } else {
        if (isFavoriteSearchedResource) {
          result =
              biliFavListService.favoriteResourceToFavLists(
                  Long.valueOf(songsIds),
                  2,
                  libraryId,
                  pmsUserDetailsUtil.getCurrentLoginUserBiliCookie());
        } else {
          result =
              biliFavListService.multipleAddResources(
                  biliSourceFavListId,
                  libraryId,
                  pmsUserDetailsUtil.getCurrentLoginUserBilibiliId(),
                  songsIds,
                  "web",
                  pmsUserDetailsUtil.getCurrentLoginUserBiliCookie());
        }
      }
    } else {
      throw new RuntimeException("Invalid platform");
    }
    return result;
  }

  @NotNull
  private Result addPMSSongsToPMSLibrary(String libraryId, String songsIds) {
    Result result;
    List<String> songsIdsList = new ArrayList<>(Arrays.asList(songsIds.split(",")));
    PMSDetailLibrary detailPlaylist = playlistMapper.getDetailPlaylist(Long.valueOf(libraryId));
    List<Long> songsIdsInPlaylist =
        relationMapper.getAllSongsIdByPlaylistId(detailPlaylist.getId());
    songsIdsList.removeIf(element -> songsIdsInPlaylist.contains(Long.valueOf(element)));
    if (songsIdsList.isEmpty()) {
      return Result.fail("Songs already exists in this playlist");
    }
    detailPlaylist.setUpdateTime(System.currentTimeMillis());
    List<Map<String, Long>> params = new ArrayList<>();
    for (String songsId : songsIdsList) {
      params.add(
          new HashMap<String, Long>() {
            {
              put("fkPlaylistId", Long.valueOf(libraryId));
              put("fkSongId", Long.valueOf(songsId));
            }
          });
    }
    int oldRowCount = relationMapper.getCountOfRelationsOfPlaylistSong();
    relationMapper.addRelationPlaylistSong(params);
    int newRowCount = relationMapper.getCountOfRelationsOfPlaylistSong();
    int newAddedSongsCount = newRowCount - oldRowCount;
    int itemCount = detailPlaylist.getItemCount();
    detailPlaylist.setItemCount(itemCount + newAddedSongsCount);
    result = Result.ok(playlistMapper.updatePlaylist(detailPlaylist));
    return result;
  }

  // Remove existed songs in pms library.
  private void removeExistedSongs(
      List<?> rawSongs, List<Long> existedSongsIdsInPlaylist, int platform) {
    if (platform == 1) { // rawSongs is qq music songs.
      List<Long> ids =
          rawSongs.stream()
              .mapToLong((song) -> Long.parseLong(((QQMusicSong) song).getSongId()))
              .boxed()
              .collect(Collectors.toList());
      Map<Long, Long> existedSongsMap = songMapper.getExistedIdsAndSongIds(ids);
      ids.removeAll(existedSongsMap.values());
      List<Long> containedKeyList = new ArrayList<>();
      existedSongsMap.forEach(
          (id, songId) -> {
            if (existedSongsIdsInPlaylist.contains(id)) {
              containedKeyList.add(id);
            }
          });
      for (Long key : containedKeyList) {
        existedSongsMap.remove(key);
      }
      //      existedSongsMap.keySet().removeIf(existedSongsIdsInPlaylist::contains);
      ids.addAll(existedSongsMap.values());
      rawSongs.removeIf(
          song -> {
            QQMusicSong qqMusicSong = (QQMusicSong) song;
            return !ids.contains(Long.valueOf(qqMusicSong.getSongId()));
          });
    } else if (platform == 2) { // rawSongs is ncm songs.
      List<Long> ids =
          rawSongs.stream()
              .mapToLong((song) -> ((NCMSong) song).getId())
              .boxed()
              .collect(Collectors.toList());
      Map<Long, Long> existedSongsMap = songMapper.getExistedIdsAndNCMIds(ids);
      ids.removeAll(existedSongsMap.values());
      List<Long> containedKeyList = new ArrayList<>();
      existedSongsMap.forEach(
          (id, songId) -> {
            if (existedSongsIdsInPlaylist.contains(id)) {
              containedKeyList.add(id);
            }
          });
      for (Long key : containedKeyList) {
        existedSongsMap.remove(key);
      }
      //      existedSongsMap.keySet().removeIf(existedSongsIdsInPlaylist::contains);
      ids.addAll(existedSongsMap.values());
      rawSongs.removeIf(
          song -> {
            NCMSong ncmSong = (NCMSong) song;
            return !ids.contains(ncmSong.getId());
          });
    } else if (platform == 3) { // rawSongs is bili resources.
      List<Long> ids =
          rawSongs.stream()
              .mapToLong((song) -> ((BiliResource) song).getId())
              .boxed()
              .collect(Collectors.toList());
      Map<Long, Long> existedSongsMap = songMapper.getExistedIdsAndAids(ids);
      ids.removeAll(existedSongsMap.values());

      List<Long> containedKeyList = new ArrayList<>();
      existedSongsMap.forEach(
          (id, songId) -> {
            if (existedSongsIdsInPlaylist.contains(id)) {
              containedKeyList.add(id);
            }
          });
      for (Long key : containedKeyList) {
        existedSongsMap.remove(key);
      }
      //      existedSongsMap.keySet().removeIf(existedSongsIdsInPlaylist::contains);
      ids.addAll(existedSongsMap.values());
      rawSongs.removeIf(
          song -> {
            BiliResource biliResource = (BiliResource) song;
            return !ids.contains(biliResource.getId());
          });
    } else {
      throw new RuntimeException("Invalid platform");
    }
  }

  @NotNull
  @Synchronized
  private Result addQQMusicSongsToPMSLibrary(String libraryId, List<BasicSong> songs) {
    List<Map<String, Long>> songIds = new ArrayList<>();
    List<Map<String, String>> qqMusicSongs = new ArrayList<>();
    PMSDetailLibrary detailPlaylist = playlistMapper.getDetailPlaylist(Long.valueOf(libraryId));
    List<Long> songsIdsInPlaylist =
        relationMapper.getAllSongsIdByPlaylistId(detailPlaylist.getId());
    removeExistedSongs(songs, songsIdsInPlaylist, 1);
    if (songs.isEmpty()) {
      return Result.fail("Songs already exists in this playlist");
    }
    detailPlaylist.setUpdateTime(System.currentTimeMillis());
    // Add songs to tb_pms_song.
    for (BasicSong song : songs) {
      QQMusicSong qqMusicSong = (QQMusicSong) song;
      Map<String, String> selectedQQMusicSong =
          songMapper.getQQMusicSongBySongId(Long.valueOf(qqMusicSong.getSongId()));
      if (selectedQQMusicSong != null) {
        songIds.add(
            new HashMap<String, Long>() {
              {
                put("fkPlaylistId", Long.valueOf(libraryId));
                put("fkSongId", Long.valueOf(String.valueOf(selectedQQMusicSong.get("pmsSongId"))));
              }
            });
        continue;
      }
      PMSSong pmsSong = new PMSSong();
      pmsSong.setType(1);
      pmsSong.setName(qqMusicSong.getName());
      pmsSong.setCover(qqMusicSong.getCover());
      pmsSong.setPayPlay(qqMusicSong.getPayPlay());
      pmsSong.setIsTakenDown(qqMusicSong.getIsTakenDown());
      int addedSongsCount = songMapper.addSong(pmsSong);
      songIds.add(
          new HashMap<String, Long>() {
            {
              put("fkPlaylistId", Long.valueOf(libraryId));
              put("fkSongId", pmsSong.getId());
            }
          });
      qqMusicSongs.add(
          new HashMap<String, String>() {
            {
              put("pmsSongId", String.valueOf(pmsSong.getId()));
              put("songId", qqMusicSong.getSongId());
              put("songMid", qqMusicSong.getSongMid());
              put("mediaMid", qqMusicSong.getMediaMid());
            }
          });
      List<? extends BasicSinger> qqMusicSingers = qqMusicSong.getSingers();
      List<Map<String, Long>> singerIds = new ArrayList<>(qqMusicSingers.size());
      for (BasicSinger singer : qqMusicSingers) {
        String name = singer.getName();
        PMSSinger selectedSinger = singerMapper.getSingerByNameAndType(name, 1);
        if (selectedSinger != null) {
          singerIds.add(
              new HashMap<String, Long>() {
                {
                  put("fkSingerId", selectedSinger.getId());
                  put("fkSongId", pmsSong.getId());
                }
              });
          continue;
        }
        PMSSinger pmsSinger = new PMSSinger();
        pmsSinger.setType(1);
        pmsSinger.setName(singer.getName());
        pmsSinger.setHeadPic(singer.getHeadPic());
        int addedSingersCount = singerMapper.addSinger(pmsSinger);
        singerIds.add(
            new HashMap<String, Long>() {
              {
                put("fkSingerId", pmsSinger.getId());
                put("fkSongId", pmsSong.getId());
              }
            });
      }
      relationMapper.addRelationSongSinger(singerIds);
    }
    int oldRowCount = relationMapper.getCountOfRelationsOfPlaylistSong();
    relationMapper.addRelationPlaylistSong(songIds);
    int newRowCount = relationMapper.getCountOfRelationsOfPlaylistSong();
    int newAddedSongsCount = newRowCount - oldRowCount;
    int itemCount = detailPlaylist.getItemCount();
    detailPlaylist.setItemCount(itemCount + newAddedSongsCount);
    playlistMapper.updatePlaylist(detailPlaylist);
    if (!qqMusicSongs.isEmpty()) {
      songMapper.addQQMusicSong(qqMusicSongs);
    }
    return Result.ok();
  }

  @NotNull
  @Synchronized
  private Result addNCMSongsToPMSLibrary(String libraryId, List<BasicSong> songs) {
    List<Map<String, Long>> songIds = new ArrayList<>();
    List<Map<String, String>> ncmSongs = new ArrayList<>();
    PMSDetailLibrary detailPlaylist = playlistMapper.getDetailPlaylist(Long.valueOf(libraryId));
    List<Long> songsIdsInPlaylist =
        relationMapper.getAllSongsIdByPlaylistId(detailPlaylist.getId());
    removeExistedSongs(songs, songsIdsInPlaylist, 2);
    if (songs.isEmpty()) {
      return Result.fail("Songs already exists in this playlist");
    }
    detailPlaylist.setUpdateTime(System.currentTimeMillis());
    for (BasicSong song : songs) {
      NCMSong ncmSong = (NCMSong) song;
      Map<String, Object> selectedNCMSong = songMapper.getNCMSongByNCMId(ncmSong.getId());
      if (selectedNCMSong != null) {
        songIds.add(
            new HashMap<String, Long>() {
              {
                put("fkPlaylistId", Long.valueOf(libraryId));
                put("fkSongId", Long.valueOf(String.valueOf(selectedNCMSong.get("pmsSongId"))));
              }
            });
        continue;
      }

      PMSSong pmsSong = new PMSSong();
      pmsSong.setType(2);
      pmsSong.setName(ncmSong.getName());
      pmsSong.setCover(ncmSong.getCover());
      pmsSong.setPayPlay(ncmSong.getPayPlay());
      pmsSong.setIsTakenDown(ncmSong.getIsTakenDown());
      int addedSongCount = songMapper.addSong(pmsSong);
      songIds.add(
          new HashMap<String, Long>() {
            {
              put("fkPlaylistId", Long.valueOf(libraryId));
              put("fkSongId", pmsSong.getId());
            }
          });
      ncmSongs.add(
          new HashMap<String, String>() {
            {
              put("pmsSongId", String.valueOf(pmsSong.getId()));
              put("ncmId", String.valueOf(ncmSong.getId()));
              put("mvId", String.valueOf(ncmSong.getMvId()));
            }
          });
      List<? extends BasicSinger> ncmSingers = ncmSong.getSingers();
      List<Map<String, Long>> singerIds = new ArrayList<>(ncmSingers.size());
      for (BasicSinger singer : ncmSingers) {
        String name = singer.getName();
        PMSSinger selectedSinger = singerMapper.getSingerByNameAndType(name, 2);
        if (selectedSinger != null) {
          singerIds.add(
              new HashMap<String, Long>() {
                {
                  put("fkSingerId", selectedSinger.getId());
                  put("fkSongId", pmsSong.getId());
                }
              });
          continue;
        }
        PMSSinger pmsSinger = new PMSSinger();
        pmsSinger.setType(2);
        pmsSinger.setName(singer.getName());
        pmsSinger.setHeadPic(singer.getHeadPic());
        int addedSingersCount = singerMapper.addSinger(pmsSinger);
        singerIds.add(
            new HashMap<String, Long>() {
              {
                put("fkSingerId", pmsSinger.getId());
                put("fkSongId", pmsSong.getId());
              }
            });
      }
      relationMapper.addRelationSongSinger(singerIds);
    }
    int oldRowCount = relationMapper.getCountOfRelationsOfPlaylistSong();
    relationMapper.addRelationPlaylistSong(songIds);
    int newRowCount = relationMapper.getCountOfRelationsOfPlaylistSong();
    int newAddedSongsCount = newRowCount - oldRowCount;
    int itemCount = detailPlaylist.getItemCount();
    detailPlaylist.setItemCount(itemCount + newAddedSongsCount);
    playlistMapper.updatePlaylist(detailPlaylist);
    if (!ncmSongs.isEmpty()) {
      songMapper.addNCMSong(ncmSongs);
    }
    return Result.ok();
  }

  @NotNull
  @Synchronized
  private Result addBiliResourcesToPMSLibrary(String libraryId, List<BiliResource> resources) {
    List<Map<String, Long>> songIds = new ArrayList<>();
    List<Map<String, String>> biliResources = new ArrayList<>();
    PMSDetailLibrary detailPlaylist = playlistMapper.getDetailPlaylist(Long.valueOf(libraryId));
    List<Long> songsIdsInPlaylist =
        relationMapper.getAllSongsIdByPlaylistId(detailPlaylist.getId());
    removeExistedSongs(resources, songsIdsInPlaylist, 3);
    if (resources.isEmpty()) {
      return Result.fail("Songs already exists in this playlist");
    }
    detailPlaylist.setUpdateTime(System.currentTimeMillis());
    for (BiliResource resource : resources) {
      Map<String, Object> selectedBiliResource = songMapper.getBiliResourceByAid(resource.getId());
      if (selectedBiliResource != null) {
        songIds.add(
            new HashMap<String, Long>() {
              {
                put("fkPlaylistId", Long.valueOf(libraryId));
                put(
                    "fkSongId",
                    Long.valueOf(String.valueOf(selectedBiliResource.get("pmsSongId"))));
              }
            });
        continue;
      }
      PMSSong pmsSong = new PMSSong();
      pmsSong.setType(3);
      pmsSong.setName(resource.getTitle());
      pmsSong.setCover(resource.getCover());
      pmsSong.setPayPlay(0);
      pmsSong.setIsTakenDown(false);
      int addedSongsCount = songMapper.addSong(pmsSong);
      songIds.add(
          new HashMap<String, Long>() {
            {
              put("fkPlaylistId", Long.valueOf(libraryId));
              put("fkSongId", pmsSong.getId());
            }
          });
      biliResources.add(
          new HashMap<String, String>() {
            {
              put("pmsSongId", String.valueOf(pmsSong.getId()));
              put("aid", resource.getId().toString());
              put("bvid", resource.getBvid());
            }
          });
      String name = resource.getUpperName();
      PMSSinger selectedSinger = singerMapper.getSingerByNameAndType(name, 3);
      List<Map<String, Long>> singers = new ArrayList<>();
      if (selectedSinger != null) {
        singers.add(
            new HashMap<String, Long>() {
              {
                put("fkSingerId", selectedSinger.getId());
                put("fkSongId", pmsSong.getId());
              }
            });
        continue;
      }
      PMSSinger pmsSinger = new PMSSinger();
      pmsSinger.setType(3);
      pmsSinger.setName(resource.getUpperName());
      pmsSinger.setHeadPic("");
      int addedSingersCount = singerMapper.addSinger(pmsSinger);
      singers.add(
          new HashMap<String, Long>() {
            {
              put("fkSingerId", pmsSinger.getId());
              put("fkSongId", pmsSong.getId());
            }
          });
      relationMapper.addRelationSongSinger(singers);
    }
    int oldRowCount = relationMapper.getCountOfRelationsOfPlaylistSong();
    relationMapper.addRelationPlaylistSong(songIds);
    int newRowCount = relationMapper.getCountOfRelationsOfPlaylistSong();
    int newAddedSongsCount = newRowCount - oldRowCount;
    int itemCount = detailPlaylist.getItemCount();
    detailPlaylist.setItemCount(itemCount + newAddedSongsCount);
    playlistMapper.updatePlaylist(detailPlaylist);
    if (!biliResources.isEmpty()) {
      songMapper.addBiliResource(biliResources);
    }
    return Result.ok();
  }

  /**
   * {@inheritDoc}
   *
   * <p>Move songs {@code songsId} from source library with {@code fromLibrary} to target library
   * with {@code toLibrary}.
   */
  @Override
  @Transactional
  public Result moveSongsToOtherLibrary(
      String songsId, String fromLibrary, String toLibrary, Integer platform) {
    Result result;
    if (platform == 0) {
      String[] songsIds = songsId.split(",");
      PMSDetailLibrary detailFromPlaylist =
          playlistMapper.getDetailPlaylist(Long.valueOf(fromLibrary));
      detailFromPlaylist.setUpdateTime(System.currentTimeMillis());
      int fromItemCount = detailFromPlaylist.getItemCount();
      detailFromPlaylist.setItemCount(fromItemCount - songsIds.length);
      playlistMapper.updatePlaylist(detailFromPlaylist);
      PMSDetailLibrary detailToPlaylist = playlistMapper.getDetailPlaylist(Long.valueOf(toLibrary));
      detailToPlaylist.setUpdateTime(System.currentTimeMillis());
      Map<String, Object> params = new HashMap<>();
      List<Map<String, Long>> addedParams = new ArrayList<>();
      params.put("libraryId", fromLibrary);
      params.put("songsIds", songsIds);
      relationMapper.deleteRelationPlaylistSong(params);
      int oldRowCount = relationMapper.getCountOfRelationsOfPlaylistSong();
      List<Long> idsInToLibrary = relationMapper.getAllSongsIdByPlaylistId(Long.valueOf(toLibrary));
      for (String songId : songsIds) {
        if (!idsInToLibrary.contains(Long.valueOf(songId))) {
          addedParams.add(
              new HashMap<String, Long>() {
                {
                  put("fkPlaylistId", Long.valueOf(toLibrary));
                  put("fkSongId", Long.valueOf(songId));
                }
              });
        }
      }
      if (!addedParams.isEmpty()) {
        relationMapper.addRelationPlaylistSong(addedParams);
      }
      int newRowCount = relationMapper.getCountOfRelationsOfPlaylistSong();
      int addedSongsCount = newRowCount - oldRowCount;
      detailToPlaylist.setItemCount(detailToPlaylist.getItemCount() + addedSongsCount);
      playlistMapper.updatePlaylist(detailToPlaylist);
      result = Result.ok();
    } else if (platform == 1) {
      result =
          qqMusicPlaylistService.moveSongsToOtherPlaylist(
              songsId,
              Integer.parseInt(fromLibrary),
              Integer.parseInt(toLibrary),
              pmsUserDetailsUtil.getCurrentLoginUserQqMusicCookie());
    } else if (platform == 2) {
      result =
          ncmPlaylistService.moveSongsToOtherPlaylist(
              songsId,
              Long.valueOf(fromLibrary),
              Long.valueOf(toLibrary),
              pmsUserDetailsUtil.getCurrentLoginUserNcmCookie());
    } else if (platform == 3) {
      result =
          biliFavListService.multipleMoveResources(
              Long.valueOf(fromLibrary),
              Long.valueOf(toLibrary),
              pmsUserDetailsUtil.getCurrentLoginUserBilibiliId(),
              songsId,
              "web",
              pmsUserDetailsUtil.getCurrentLoginUserBiliCookie());
    } else {
      throw new RuntimeException("Invalid platform");
    }
    return result;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Remove songs {@code songsId} from library {@code libraryId}.
   */
  @Override
  @Transactional
  public Result removeSongsFromLibrary(String libraryId, String songsId, Integer platform) {
    Result result;
    if (platform == 0) {
      String[] songsIds = songsId.split(",");
      PMSDetailLibrary detailPlaylist = playlistMapper.getDetailPlaylist(Long.valueOf(libraryId));
      detailPlaylist.setUpdateTime(System.currentTimeMillis());
      int itemCount = detailPlaylist.getItemCount();
      detailPlaylist.setItemCount(itemCount - songsIds.length);
      playlistMapper.updatePlaylist(detailPlaylist);
      Map<String, Object> params = new HashMap<>();
      params.put("libraryId", libraryId);
      params.put("songsIds", songsIds);
      relationMapper.deleteRelationPlaylistSong(params);
      result = Result.ok();
    } else if (platform == 1) {
      result =
          qqMusicPlaylistService.removeSongsFromPlaylist(
              Integer.parseInt(libraryId),
              songsId,
              pmsUserDetailsUtil.getCurrentLoginUserQqMusicCookie());
    } else if (platform == 2) {
      result =
          ncmPlaylistService.removeSongsFromPlaylist(
              Long.valueOf(libraryId), songsId, pmsUserDetailsUtil.getCurrentLoginUserNcmCookie());
    } else if (platform == 3) {
      result =
          biliFavListService.multipleDeleteResources(
              songsId,
              Long.valueOf(libraryId),
              "web",
              pmsUserDetailsUtil.getCurrentLoginUserBiliCookie());
    } else {
      throw new RuntimeException("Invalid platform");
    }
    return result;
  }
}
