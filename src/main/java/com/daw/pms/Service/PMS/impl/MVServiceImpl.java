package com.daw.pms.Service.PMS.impl;

import com.daw.pms.Entity.Basic.BasicVideo;
import com.daw.pms.Service.NeteaseCloudMusic.NCMMVService;
import com.daw.pms.Service.PMS.MVService;
import com.daw.pms.Service.QQMusic.QQMusicMVService;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MVServiceImpl implements MVService, Serializable {
  @Value("${qqmusic.cookie}")
  private String qqMusicCookie;

  @Value("${ncm.cookie}")
  private String ncmCookie;

  private final QQMusicMVService qqMusicMVService;
  private final NCMMVService ncmMVService;

  public MVServiceImpl(QQMusicMVService qqMusicMVService, NCMMVService ncmMVService) {
    this.qqMusicMVService = qqMusicMVService;
    this.ncmMVService = ncmMVService;
  }

  /**
   * @param vid The vid of the mv.
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
      throw new RuntimeException("Invalid platform.");
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
      throw new RuntimeException("Invalid platform.");
    }
    return mvLinks;
  }

  /**
   * Get all related videos with the song.
   *
   * @param songId The song's id.
   * @param mvId The mv's id, only in ncm platform.
   * @param limit The limit of related videos, only in ncm platform.
   * @param platform The platform id.
   * @return All the related video about the song with {@code songId}.
   */
  @Override
  public List<BasicVideo> getRelatedVideos(
      Long songId, String mvId, Integer limit, Integer platform) {
    List<BasicVideo> relatedVideos;
    if (platform == 0) {
      throw new RuntimeException("Not yet implement pms platform.");
    } else if (platform == 1) {
      relatedVideos = qqMusicMVService.getRelatedVideos(songId.intValue(), qqMusicCookie);
    } else if (platform == 2) {
      relatedVideos = ncmMVService.getRelatedVideos(songId, mvId, limit, ncmCookie);
    } else if (platform == 3) {
      throw new RuntimeException("Not yet implement bilibili platform.");
    } else {
      throw new RuntimeException("Invalid platform.");
    }
    return relatedVideos;
  }
}
