package com.daw.pms.Service.QQMusic.impl;

import com.daw.pms.Config.QQMusicAPI;
import java.util.HashMap;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

  @Value("${qqmusic.id}")
  private Long qqMusicId;

  @Value("${qqmusic.cookie}")
  private String qqMusicCookie;

  @Autowired private QQMusicBase qqMusicBase;

  @Test
  void requestGetAPI() {
    System.out.println(
        qqMusicBase.requestGetAPI(
            QQMusicAPI.GET_PLAYLIST,
            new HashMap<String, String>() {
              {
                put("id", qqMusicId.toString());
              }
            },
            Optional.of(qqMusicCookie)));
  }
}
