package com.daw.pms.Service.impl;

import com.daw.pms.Service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceImplTest {
  @Autowired UserService userService;

  @Test
  void getUserInfo() {
    System.out.println(userService.getUserInfo(QQMusicCookieServiceImplTest.id));
  }
}
