package com.daw.pms.Service.Bilibili.impl;

import com.daw.pms.Entity.Bilibili.BilibiliUser;
import com.daw.pms.Service.Bilibili.BilibiliUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BilibiliUserServiceImplTest {
  @Value("${bilibili.cookie}")
  private String bilibiliCookie;

  @Autowired public BilibiliUserService bilibiliUserService;

  @Test
  void getUserInfo() {
    BilibiliUser user = bilibiliUserService.getUserInfo(bilibiliCookie);
    System.out.println(user);
  }
}
