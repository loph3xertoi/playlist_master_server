package com.daw.pms.Service.NeteaseCloudMusic.impl;

import com.daw.pms.Service.NeteaseCloudMusic.NCMUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NCMUserServiceImplTest {
  @Value("${ncm.id}")
  private Long ncmId;

  @Value("${ncm.cookie}")
  private String ncmCookie;

  @Autowired NCMUserService ncmUserService;

  @Test
  void getUserInfo() {
    System.out.println(ncmUserService.getUserInfo(ncmId, ncmCookie));
  }
}
