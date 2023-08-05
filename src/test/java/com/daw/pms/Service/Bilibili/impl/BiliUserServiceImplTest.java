package com.daw.pms.Service.Bilibili.impl;

import com.daw.pms.Entity.Bilibili.BiliUser;
import com.daw.pms.Service.Bilibili.BiliUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BiliUserServiceImplTest {
  @Value("${bilibili.cookie}")
  private String biliCookie;

  @Autowired public BiliUserService biliUserService;

  @Test
  void getUserInfo() {
    BiliUser user = biliUserService.getUserInfo(biliCookie);
    System.out.println(user);
  }
}
