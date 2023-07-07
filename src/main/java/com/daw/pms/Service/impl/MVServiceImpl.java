package com.daw.pms.Service.impl;

import com.daw.pms.Entity.QQMusicMV;
import com.daw.pms.Service.MVService;
import com.daw.pms.Service.QQMusicCookieService;
import com.daw.pms.Service.QQMusicMVService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class MVServiceImpl implements MVService {
  private final QQMusicMVService qqMusicMVService;
  private final QQMusicCookieService qqMusicCookieService;

  public MVServiceImpl(
      QQMusicMVService qqMusicMVService, QQMusicCookieService qqMusicCookieService) {
    this.qqMusicMVService = qqMusicMVService;
    this.qqMusicCookieService = qqMusicCookieService;
  }

  /**
   * @param vid The vid of the mv.
   * @param platformId The platform id.
   * @return The basic information of the mv {@code vid} wrapped with QQMusicMV.
   */
  @Override
  public QQMusicMV getDetailMV(String vid, Integer platformId) {
    QQMusicMV mvInfo = null;
    if (platformId == 1) {
      mvInfo = qqMusicMVService.getMVInfo(vid, qqMusicCookieService.getCookie(1));
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
}
