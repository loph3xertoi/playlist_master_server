package com.daw.pms.Service.PMS.impl;

import com.daw.pms.GlobalConfig;
import com.daw.pms.Service.PMS.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PMSUserServiceImplTest {
  @Autowired UserService userService;

  @Test
  void getUserInfo() {
    System.out.println(userService.getUserInfo(GlobalConfig.pmsId, 1));
  }
}
