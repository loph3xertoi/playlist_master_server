package com.daw.pms.Service.NeteaseCloudMusic.impl;

import com.daw.pms.GlobalConfig;
import com.daw.pms.Service.NeteaseCloudMusic.NCMUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NCMUserServiceImplTest {
  @Autowired NCMUserService ncmUserService;

  @Test
  void getUserInfo() {
    System.out.println(ncmUserService.getUserInfo("1297597572", GlobalConfig.ncmCookie));
  }
}
