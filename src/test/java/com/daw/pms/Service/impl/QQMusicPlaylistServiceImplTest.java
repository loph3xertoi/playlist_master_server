package com.daw.pms.Service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class QQMusicPlaylistServiceImplTest {
  @Autowired QQMusicPlaylistServiceImpl qqMusicPlaylistService;

  @Test
  void getPlaylist() {
    System.out.println(
        qqMusicPlaylistService.getPlaylist(
            QQMusicCookieServiceImplTest.id, QQMusicCookieServiceImplTest.cookie));
  }

  @Test
  void getDetailPlaylist() {
    System.out.println(
        qqMusicPlaylistService.getDetailPlaylist(
            "4146457838", QQMusicCookieServiceImplTest.cookie));
  }

  @Test
  void createPlaylist() {
    System.out.println(
        qqMusicPlaylistService.createPlaylist("my playlist", QQMusicCookieServiceImplTest.cookie));
  }

  @Test
  void deletePlaylist() {
    System.out.println(
        qqMusicPlaylistService.deletePlaylist(22, QQMusicCookieServiceImplTest.cookie));
  }
}
