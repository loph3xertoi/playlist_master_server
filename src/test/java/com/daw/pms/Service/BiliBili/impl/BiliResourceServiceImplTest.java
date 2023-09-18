package com.daw.pms.Service.BiliBili.impl;

import com.daw.pms.DTO.Result;
import com.daw.pms.Service.BiliBili.BiliResourceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BiliResourceServiceImplTest {
  @Value("${bilibili.cookie}")
  private String biliCookie;

  @Autowired private BiliResourceService biliResourceService;

  @Test
  void getDetailResource() {
    Result result = biliResourceService.getDetailResource("BV1MR4y1P7T3", biliCookie);
    System.out.println(result);
  }

  @Test
  void getResourceDashLink() {
    Result result = biliResourceService.getResourceDashLink("BV19841187Tx", 893463032L, biliCookie);
    System.out.println(result);
  }

  @Test
  void searchResources() {
    Result result =
        biliResourceService.searchResources("video", "洛天依", "totalrank", 0, 0, 1, biliCookie);
    System.out.println(result);
  }
}
