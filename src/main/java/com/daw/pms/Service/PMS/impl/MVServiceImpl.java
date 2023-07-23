package com.daw.pms.Service.PMS.impl;

import com.daw.pms.Entity.Basic.BasicVideo;
import com.daw.pms.Entity.QQMusic.QQMusicDetailVideo;
import com.daw.pms.Service.PMS.MVService;
import com.daw.pms.Service.QQMusic.QQMusicCookieService;
import com.daw.pms.Service.QQMusic.QQMusicMVService;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class MVServiceImpl implements MVService, Serializable {
  private final QQMusicMVService qqMusicMVService;
  private final QQMusicCookieService qqMusicCookieService;

  public MVServiceImpl(
      QQMusicMVService qqMusicMVService, QQMusicCookieService qqMusicCookieService) {
    this.qqMusicMVService = qqMusicMVService;
    this.qqMusicCookieService = qqMusicCookieService;
  }

  /**
   * @param vid The vid of the mv.
   * @param platform The platform id.
   * @return The detail information of the mv {@code vid}.
   */
  @Override
  public BasicVideo getDetailMV(String vid, Integer platform) {
    BasicVideo mvInfo;
    if (platform == 1) {
      String cookie = qqMusicCookieService.getCookie(1);
      mvInfo = qqMusicMVService.getDetailMV(vid, cookie);
      Map<String, List<String>> mVsLink = qqMusicMVService.getMVsLink(vid, cookie);
      ((QQMusicDetailVideo) mvInfo).setLinks(mVsLink.get(vid));
    } else {
      throw new RuntimeException("Not yet implement other platform.");
    }
    return mvInfo;
  }

  /**
   * @param vids The vids of the mv(s), multi vids separated by comma.
   * @param platformId The platform id.
   * @return The urls for the mv(s).
   */
  @Override
  public Map<String, List<String>> getMVsLink(String vids, Integer platformId) {
    Map<String, List<String>> mvLinks = new HashMap<>();
    if (platformId == 1) {
      mvLinks = qqMusicMVService.getMVsLink(vids, qqMusicCookieService.getCookie(1));
    }
    return mvLinks;
  }

  /**
   * @param songId The song id.
   * @param platform The platform id.
   * @return All the related video about the song with {@code songId}.
   */
  @Override
  public List<BasicVideo> getRelatedVideos(Integer songId, Integer platform) {
    List<BasicVideo> relatedVideos;
    if (platform == 1) {
      String cookie = qqMusicCookieService.getCookie(1);
      relatedVideos = qqMusicMVService.getRelatedVideos(songId, cookie);
    } else {
      throw new RuntimeException("Not yet implement other platform.");
    }
    return relatedVideos;
  }
}
