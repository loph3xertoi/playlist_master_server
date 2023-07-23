package com.daw.pms.Service.QQMusic.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class QQMusicVideoServiceImplTest {
  @Value("${qqmusic.cookie}")
  private String qqMusicCookie;

  @Autowired QQMusicMVServiceImpl qqMusicMVService;

  @Test
  void getMVInfo() {
    System.out.println(qqMusicMVService.getDetailMV("c0021h9cv9k", qqMusicCookie));
  }

  @Test
  void getMVsLink() {
    System.out.println(qqMusicMVService.getMVsLink("c0021h9cv9k,h0025qsvrpe", qqMusicCookie));
  }

  @Test
  void getRelatedVideos() {
    System.out.println(qqMusicMVService.getRelatedVideos(102340965, qqMusicCookie));
  }
}
