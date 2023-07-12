package com.daw.pms.Service.impl;

import com.daw.pms.Config.QQMusicAPI;
import com.daw.pms.Service.QQMusic.impl.QQMusicBase;
import java.util.HashMap;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootTest
class QQMusicBaseTest {

  @TestConfiguration
  static class QQMusicBaseTestConfiguration {
    @Bean
    public QQMusicBase qqMusicBase() {
      return new QQMusicBase();
    }
  }

  @Autowired private QQMusicBase qqMusicBase;

  @Test
  void requestGetAPI() {
    System.out.println(
        qqMusicBase.requestGetAPI(
            QQMusicAPI.GET_PLAYLIST,
            new HashMap<String, String>() {
              {
                put("id", QQMusicCookieServiceImplTest.id);
              }
            },
            Optional.of(QQMusicCookieServiceImplTest.cookie)));
  }
}
