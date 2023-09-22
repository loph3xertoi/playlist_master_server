package com.daw.pms.Service.PMS.impl;

import com.daw.pms.DTO.Result;
import com.daw.pms.Service.PMS.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceImplTest {
  @Value("${pms.id}")
  private Long pmsId;

  @Autowired UserService userService;

  @Test
  void getUserInfo() {
    try {
      Result pmsUser = userService.getUserInfo(pmsId, 0);
      System.out.println("pmsUser: \n" + pmsUser);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Result qqmusicUser = userService.getUserInfo(pmsId, 1);
      System.out.println("qqmusicUser: \n" + qqmusicUser);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Result ncmUser = userService.getUserInfo(pmsId, 2);
      System.out.println("ncmUser: \n" + ncmUser);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Result bilibiliUser = userService.getUserInfo(pmsId, 3);
      System.out.println("bilibiliUser: \n" + bilibiliUser);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
