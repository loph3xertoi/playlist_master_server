package com.daw.pms.Service.impl;

import com.daw.pms.Service.QQMusic.QQMusicUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class QQMusicUserServiceImplTest {
  @Autowired QQMusicUserService qqMusicUserService;

  @Test
  void getUserInfo() {
    System.out.println(
        qqMusicUserService.getUserInfo(
            QQMusicCookieServiceImplTest.id, QQMusicCookieServiceImplTest.cookie));
  }
}
