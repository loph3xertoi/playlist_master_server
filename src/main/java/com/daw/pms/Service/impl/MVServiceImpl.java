package com.daw.pms.Service.impl;

import com.daw.pms.Entity.QQMusicMV;
import com.daw.pms.Service.MVService;
import com.daw.pms.Service.QQMusicCookieService;
import com.daw.pms.Service.QQMusicMVService;
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
}
