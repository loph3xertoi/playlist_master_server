package com.daw.pms.Service.PMS.impl;

import com.daw.pms.Entity.Basic.BasicVideo;
import com.daw.pms.Mapper.SongMapper;
import com.daw.pms.Service.NeteaseCloudMusic.NCMMVService;
import com.daw.pms.Service.PMS.MVService;
import com.daw.pms.Service.QQMusic.QQMusicMVService;
import com.daw.pms.Utils.PmsUserDetailsUtil;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
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
  private final PmsUserDetailsUtil pmsUserDetailsUtil;
  private final QQMusicMVService qqMusicMVService;
  private final NCMMVService ncmMVService;
  private final SongMapper songMapper;

  /**
   * Constructor for MVServiceImpl.
   *
   * @param pmsUserDetailsUtil a {@link com.daw.pms.Utils.PmsUserDetailsUtil} object.
   * @param qqMusicMVService a {@link com.daw.pms.Service.QQMusic.QQMusicMVService} object.
   * @param ncmMVService a {@link com.daw.pms.Service.NeteaseCloudMusic.NCMMVService} object.
   * @param songMapper a {@link com.daw.pms.Mapper.SongMapper} object.
   */
  public MVServiceImpl(
      PmsUserDetailsUtil pmsUserDetailsUtil,
      QQMusicMVService qqMusicMVService,
      NCMMVService ncmMVService,
      SongMapper songMapper) {
    this.pmsUserDetailsUtil = pmsUserDetailsUtil;
    this.qqMusicMVService = qqMusicMVService;
    this.ncmMVService = ncmMVService;
    this.songMapper = songMapper;
  }

  /** {@inheritDoc} */
  @Override
  public BasicVideo getDetailMV(String vid, Integer platform) {
    BasicVideo mvInfo;
    if (platform == 0) {
      throw new RuntimeException("Not yet implement pms platform.");
    } else if (platform == 1) {
      mvInfo =
          qqMusicMVService.getDetailMV(vid, pmsUserDetailsUtil.getCurrentLoginUserQqMusicCookie());
    } else if (platform == 2) {
      mvInfo = ncmMVService.getDetailMV(vid, pmsUserDetailsUtil.getCurrentLoginUserNcmCookie());
    } else if (platform == 3) {
      throw new RuntimeException("Not yet implement bilibili platform.");
    } else {
      throw new RuntimeException("Invalid platform");
    }
    return mvInfo;
  }

  /** {@inheritDoc} */
  @Override
  public Map<String, List<String>> getMVsLink(String vids, Integer platform) {
    Map<String, List<String>> mvLinks;
    if (platform == 0) {
      throw new RuntimeException("Not yet implement pms platform.");
    } else if (platform == 1) {
      mvLinks =
          qqMusicMVService.getMVsLink(vids, pmsUserDetailsUtil.getCurrentLoginUserQqMusicCookie());
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
   * {@inheritDoc}
   *
   * <p>Get all related videos with the song.
   */
  @Override
  public List<? extends BasicVideo> getRelatedVideos(
      Long songId, String mvId, Integer limit, Integer songType, Integer platform) {
    List<? extends BasicVideo> relatedVideos;
    if (platform == 0) {
      if (songType == 1) {
        Map<String, Object> qqMusicSong = songMapper.getQQMusicSong(songId);
        Integer qqMusicSongId = Integer.valueOf(qqMusicSong.get("songId").toString());
        relatedVideos =
            qqMusicMVService.getRelatedVideos(
                qqMusicSongId, pmsUserDetailsUtil.getCurrentLoginUserQqMusicCookie());
      } else if (songType == 2) {
        Map<String, Object> ncmSong = songMapper.getNCMSong(songId);
        Long ncmId = Long.valueOf(ncmSong.get("ncmId").toString());
        String ncmMvId = ncmSong.get("mvId").toString();
        relatedVideos =
            ncmMVService.getRelatedVideos(
                ncmId, ncmMvId, 9999, pmsUserDetailsUtil.getCurrentLoginUserNcmCookie());
      } else {
        throw new RuntimeException("Invalid song type");
      }
    } else if (platform == 1) {
      relatedVideos =
          qqMusicMVService.getRelatedVideos(
              songId.intValue(), pmsUserDetailsUtil.getCurrentLoginUserQqMusicCookie());
    } else if (platform == 2) {
      if (limit == null) {
        throw new RuntimeException("Parameter invalid.");
      }
      relatedVideos =
          ncmMVService.getRelatedVideos(
              songId, mvId, limit, pmsUserDetailsUtil.getCurrentLoginUserNcmCookie());
    } else if (platform == 3) {
      throw new RuntimeException("Not yet implement bilibili platform.");
    } else {
      throw new RuntimeException("Invalid platform");
    }
    return relatedVideos;
  }
}
