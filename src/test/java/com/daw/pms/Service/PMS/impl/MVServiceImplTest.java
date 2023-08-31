package com.daw.pms.Service.PMS.impl;

import com.daw.pms.Entity.Basic.BasicVideo;
import com.daw.pms.Service.PMS.MVService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MVServiceImplTest {
  @Autowired private MVService mvService;

  @Test
  void getDetailMV() {
    try {
      BasicVideo pmsDetailMV = mvService.getDetailMV("0", 0);
      System.out.println("pmsDetailMV: \n" + pmsDetailMV);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      BasicVideo qqmusicDetailMV = mvService.getDetailMV("c0021h9cv9k", 1);
      System.out.println("qqmusicDetailMV: \n" + qqmusicDetailMV);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      BasicVideo ncmDetailMV1 = mvService.getDetailMV("5457128", 2);
      System.out.println("ncmDetailMV1: \n" + ncmDetailMV1);
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      BasicVideo ncmDetailMV2 = mvService.getDetailMV("a1UkQWlMlHWEP0V", 2);
      System.out.println("ncmDetailMV2: \n" + ncmDetailMV2);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      BasicVideo bilibiliDetailMV = mvService.getDetailMV("0", 3);
      System.out.println("bilibiliDetailMV: \n" + bilibiliDetailMV);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  void getRelatedVideos() {
    try {
      List<BasicVideo> pmsRelatedVideos = mvService.getRelatedVideos(0L, "0", 0, 0, 0);
      System.out.println("pmsRelatedVideos: \n" + pmsRelatedVideos);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      List<BasicVideo> qqmusicRelatedVideos = mvService.getRelatedVideos(102340965L, "0", 0, 1, 1);
      System.out.println("qqmusicRelatedVideos: \n" + qqmusicRelatedVideos);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      List<BasicVideo> ncmRelatedVideos =
          mvService.getRelatedVideos(460514067L, "5457128", 10, 2, 2);
      System.out.println("ncmRelatedVideos: \n" + ncmRelatedVideos);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      List<BasicVideo> bilibiliRelatedVideos = mvService.getRelatedVideos(0L, "0", 0, 3, 3);
      System.out.println("bilibiliRelatedVideos: \n" + bilibiliRelatedVideos);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
