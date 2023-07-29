package com.daw.pms.Service.NeteaseCloudMusic.impl;

import com.daw.pms.Entity.Basic.BasicVideo;
import com.daw.pms.Entity.NeteaseCloudMusic.NCMDetailVideo;
import com.daw.pms.Service.NeteaseCloudMusic.NCMMVService;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NCMMVServiceImplTest {
  @Value("${ncm.cookie}")
  private String ncmCookie;

  @Autowired NCMMVService ncmMVService;

  @Test
  void getDetailMV() {
    NCMDetailVideo detailMV = ncmMVService.getDetailMV("5457128", ncmCookie);
    System.out.println("detailMV: \n" + detailMV);
    NCMDetailVideo detailMV2 = ncmMVService.getDetailMV("a1UkQWlMlHWEP0V", ncmCookie);
    System.out.println("detailMV2: \n" + detailMV2);
  }

  @Test
  void getMVLink() {
    String mvLink = ncmMVService.getMVLink("5457128", 480, ncmCookie);
    System.out.println(mvLink);
  }

  @Test
  void getMLogLinks() {
    Map<String, String> links = ncmMVService.getMLogLinks("a1UkQWlMlHWEP0V", ncmCookie);
    System.out.println(links);
  }

  @Test
  void getRelatedVideos() {
    List<BasicVideo> relatedVideos =
        ncmMVService.getRelatedVideos(460514067L, "5457128", 10, ncmCookie);
    System.out.println(relatedVideos);
  }

  @Test
  void convertMLogIdToVid() {
    String vid = ncmMVService.convertMLogIdToVid("a1UkQWlMlHWEP0V", ncmCookie);
    System.out.println(vid);
  }
}
