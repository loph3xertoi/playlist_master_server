package com.daw.pms.Service.QQMusic.impl;

import com.daw.pms.Service.QQMusic.QQMusicUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class QQMusicUserServiceImplTest {
  @Value("${qqmusic.id}")
  private Long qqMusicId;

  @Value("${qqmusic.cookie}")
  private String qqMusicCookie;

  @Autowired QQMusicUserService qqMusicUserService;

  @Test
  void getUserInfo() {
    System.out.println(qqMusicUserService.getUserInfo(qqMusicId, qqMusicCookie));
  }
}
