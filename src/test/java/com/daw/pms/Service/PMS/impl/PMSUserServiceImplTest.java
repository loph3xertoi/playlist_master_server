package com.daw.pms.Service.PMS.impl;

import com.daw.pms.Service.PMS.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PMSUserServiceImplTest {
  @Value("${pms.id}")
  private Long pmsId;

  @Autowired UserService userService;

  @Test
  void getUserInfo() {
    System.out.println(userService.getUserInfo(pmsId, 0));
    System.out.println(userService.getUserInfo(pmsId, 1));
    System.out.println(userService.getUserInfo(pmsId, 2));
  }
}
