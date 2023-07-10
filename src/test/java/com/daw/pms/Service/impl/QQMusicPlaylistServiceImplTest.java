package com.daw.pms.Service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class QQMusicPlaylistServiceImplTest {
  @Autowired QQMusicPlaylistServiceImpl qqMusicPlaylistService;

  @Test
  void getPlaylists() {
    System.out.println(
        qqMusicPlaylistService.getPlaylists(
            QQMusicCookieServiceImplTest.id, QQMusicCookieServiceImplTest.cookie));
  }

  @Test
  void getDetailPlaylist() {
    System.out.println(
        qqMusicPlaylistService.getDetailPlaylist(
            "8729577481", QQMusicCookieServiceImplTest.cookie));
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
