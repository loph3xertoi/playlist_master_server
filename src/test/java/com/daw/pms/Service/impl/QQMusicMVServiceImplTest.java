package com.daw.pms.Service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class QQMusicMVServiceImplTest {

  @Autowired QQMusicMVServiceImpl qqMusicMVService;

  @Test
  void getMVInfo() {
    System.out.println(
        qqMusicMVService.getMVInfo("c0021h9cv9k", QQMusicCookieServiceImplTest.cookie));
  }

  @Test
  void getMVLink() {
    System.out.println(
        qqMusicMVService.getMVLink("c0021h9cv9k", QQMusicCookieServiceImplTest.cookie));
  }
}
