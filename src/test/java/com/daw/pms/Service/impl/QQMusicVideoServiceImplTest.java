package com.daw.pms.Service.impl;

import com.daw.pms.Service.QQMusic.impl.QQMusicMVServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class QQMusicVideoServiceImplTest {

  @Autowired QQMusicMVServiceImpl qqMusicMVService;

  @Test
  void getMVInfo() {
    System.out.println(
        qqMusicMVService.getDetailMV("c0021h9cv9k", QQMusicCookieServiceImplTest.cookie));
  }

  @Test
  void getMVsLink() {
    System.out.println(
        qqMusicMVService.getMVsLink(
            "c0021h9cv9k,h0025qsvrpe", QQMusicCookieServiceImplTest.cookie));
  }

  @Test
  void getRelatedVideos() {
    System.out.println(
        qqMusicMVService.getRelatedVideos(102340965, QQMusicCookieServiceImplTest.cookie));
  }
}
