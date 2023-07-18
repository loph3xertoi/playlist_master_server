package com.daw.pms.Service.impl;

import com.daw.pms.Service.QQMusic.QQMusicCookieService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class QQMusicCookieServiceImplTest {
  @Autowired private QQMusicCookieService qqMusicCookieService;
  public static final String cookie =
      "RK=cPE1oUG96l; ptcz=3a006b5174b87a9f67922612cbee3d1e3a1aa9c084e6e9a20d727f919fd52e55; pgv_pvid=1387964528; fqm_pvqid=3f99dd5b-347d-4535-8878-91820de7347c; ts_uid=3724214884; euin=owcz7e6soK4FNv**; tmeLoginType=2; ts_refer=ADTAGshare; fqm_sessionid=89917581-94c6-4109-82f1-a9ced50ab9af; pgv_info=ssid=s1112972034; ts_last=y.qq.com/n/ryqq/profile/create; login_type=1; psrf_qqaccess_token=FE9C7B32C6780C062FB6F6422C86CFD7; wxopenid=; psrf_qqunionid=43BEA8C2CFEA50810FE7B4EC6531E2F8; psrf_musickey_createtime=1689645655; psrf_access_token_expiresAt=1697421655; qm_keyst=Q_H_L_5EmCD-qWQVdDIMjCVF6hZTeziQwTZnsdGKrw8iRQId1N3iXSmtUA2wg; wxrefresh_token=; wxunionid=; qqmusic_key=Q_H_L_5EmCD-qWQVdDIMjCVF6hZTeziQwTZnsdGKrw8iRQId1N3iXSmtUA2wg; psrf_qqrefresh_token=5897CFA919C34EF6A852BD1A96C89484; psrf_qqopenid=3C116662943E815435D05D2505ED32A1; qm_keyst=Q_H_L_5EmCD-qWQVdDIMjCVF6hZTeziQwTZnsdGKrw8iRQId1N3iXSmtUA2wg; uin=2804161589";
  public static final String id = "2804161589";
  //  public static final String id = "3590659227";

  @Order(1)
  @Test
  void setCookie() {
    System.out.println(qqMusicCookieService.setCookie(cookie));
  }

  @Order(2)
  @Test
  void applyCookie() {
    System.out.println(qqMusicCookieService.applyCookie(id));
  }

  @Order(3)
  @Test
  void getCookie() {
    System.out.println(qqMusicCookieService.getCookie(1));
  }

  @BeforeEach
  void setUp() {
    //    System.out.println("setup...");
  }

  @AfterEach
  void tearDown() {
    //    System.out.println("teardown...");
  }
}
