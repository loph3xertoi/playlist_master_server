package com.daw.pms.Service.PMS.impl;

import com.daw.pms.Entity.Basic.BasicVideo;
import com.daw.pms.Mapper.SongMapper;
import com.daw.pms.Service.NeteaseCloudMusic.NCMMVService;
import com.daw.pms.Service.PMS.MVService;
import com.daw.pms.Service.QQMusic.QQMusicMVService;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for handle MV.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 6/28/23
 */
@Service
public class MVServiceImpl implements MVService, Serializable {
  @Value("${qqmusic.cookie}")
  private String qqMusicCookie;

  @Value("${ncm.cookie}")
  private String ncmCookie;

  private final QQMusicMVService qqMusicMVService;
  private final NCMMVService ncmMVService;
  private final SongMapper songMapper;

  public MVServiceImpl(
      QQMusicMVService qqMusicMVService, NCMMVService ncmMVService, SongMapper songMapper) {
    this.qqMusicMVService = qqMusicMVService;
    this.ncmMVService = ncmMVService;
    this.songMapper = songMapper;
  }

  /**
   * @param vid The vid/mvid/mlogId of the mv.
   * @param platform The platform id.
   * @return The detail information of the mv {@code vid}.
   */
  @Override
  public BasicVideo getDetailMV(String vid, Integer platform) {
    BasicVideo mvInfo;
    if (platform == 0) {
      throw new RuntimeException("Not yet implement pms platform.");
    } else if (platform == 1) {
      mvInfo = qqMusicMVService.getDetailMV(vid, qqMusicCookie);
    } else if (platform == 2) {
      mvInfo = ncmMVService.getDetailMV(vid, ncmCookie);
    } else if (platform == 3) {
      throw new RuntimeException("Not yet implement bilibili platform.");
    } else {
      throw new RuntimeException("Invalid platform");
    }
    return mvInfo;
  }

  /**
   * @param vids The vids of the mv(s), multi vids separated by comma.
   * @param platform The platform id.
   * @return The urls for the mv(s).
   * @deprecated Make a uniform result between different platforms.
   */
  @Override
  public Map<String, List<String>> getMVsLink(String vids, Integer platform) {
    Map<String, List<String>> mvLinks;
    if (platform == 0) {
      throw new RuntimeException("Not yet implement pms platform.");
    } else if (platform == 1) {
      mvLinks = qqMusicMVService.getMVsLink(vids, qqMusicCookie);
    } else if (platform == 2) {
      throw new RuntimeException("Not yet implement ncm platform.");
    } else if (platform == 3) {
      throw new RuntimeException("Not yet implement bilibili platform.");
    } else {
      throw new RuntimeException("Invalid platform");
    }
    return mvLinks;
  }

  /**
   * Get all related videos with the song.
   *
   * @param songId The song's id.
   * @param mvId The mv's id, only in ncm platform.
   * @param limit The limit of related videos, only used in ncm platform.
   * @param songType The pms song's type, only used in pms platform.
   * @param platform The platform id.
   * @return All the related video about the song with {@code songId}.
   */
  @Override
  public List<? extends BasicVideo> getRelatedVideos(
      Long songId, String mvId, Integer limit, Integer songType, Integer platform) {
    List<? extends BasicVideo> relatedVideos;
    if (platform == 0) {
      if (songType == 1) {
        Map<String, Object> qqMusicSong = songMapper.getQQMusicSong(songId);
        Integer qqMusicSongId = Integer.valueOf(qqMusicSong.get("songId").toString());
        relatedVideos = qqMusicMVService.getRelatedVideos(qqMusicSongId, qqMusicCookie);
      } else if (songType == 2) {
        Map<String, Object> ncmSong = songMapper.getNCMSong(songId);
        Long ncmId = Long.valueOf(ncmSong.get("ncmId").toString());
        String ncmMvId = ncmSong.get("mvId").toString();
        relatedVideos = ncmMVService.getRelatedVideos(ncmId, ncmMvId, 9999, ncmCookie);
      } else {
        throw new RuntimeException("Invalid song type");
      }
    } else if (platform == 1) {
      relatedVideos = qqMusicMVService.getRelatedVideos(songId.intValue(), qqMusicCookie);
    } else if (platform == 2) {
      if (limit == null) {
        throw new RuntimeException("Parameter invalid.");
      }
      relatedVideos = ncmMVService.getRelatedVideos(songId, mvId, limit, ncmCookie);
    } else if (platform == 3) {
      throw new RuntimeException("Not yet implement bilibili platform.");
    } else {
      throw new RuntimeException("Invalid platform");
    }
    return relatedVideos;
  }
}
