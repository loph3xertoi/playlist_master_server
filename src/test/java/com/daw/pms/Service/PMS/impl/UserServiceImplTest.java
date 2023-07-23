package com.daw.pms.Service.PMS.impl;

import com.daw.pms.Entity.Basic.BasicUser;
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
      BasicUser pmsUser = userService.getUserInfo(pmsId, 0);
      System.out.println("pmsUser: \n" + pmsUser);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      BasicUser qqmusicUser = userService.getUserInfo(pmsId, 1);
      System.out.println("qqmusicUser: \n" + qqmusicUser);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      BasicUser ncmUser = userService.getUserInfo(pmsId, 2);
      System.out.println("ncmUser: \n" + ncmUser);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      BasicUser bilibiliUser = userService.getUserInfo(pmsId, 3);
      System.out.println("bilibiliUser: \n" + bilibiliUser);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
