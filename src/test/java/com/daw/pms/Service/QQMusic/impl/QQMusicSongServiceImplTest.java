package com.daw.pms.Service.QQMusic.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class QQMusicSongServiceImplTest {
  @Value("${qqmusic.cookie}")
  private String qqMusicCookie;

  @Autowired QQMusicSongServiceImpl qqMusicSongService;

  @Test
  void getSongsIdFromPlaylist() {
    System.out.println(qqMusicSongService.getSongsIdFromPlaylist("14", qqMusicCookie));
  }

  // 002GNiJS1BwnQJ 003aW4ny3SpmBa
  @Test
  void getDetailSong() {
    System.out.println(qqMusicSongService.getDetailSong("000Miat72wA0e6", qqMusicCookie));
  }

  @Test
  void getSimilarSongs() {
    System.out.println(qqMusicSongService.getSimilarSongs("240442614", qqMusicCookie));
  }

  @Test
  void getLyrics() {
    System.out.println(qqMusicSongService.getLyrics("001UiydT2NNPir", qqMusicCookie));
  }

  // Only for album, the song cover can obtain by albummid directly.
  @Test
  void getSongCoverUri() {
    System.out.println(qqMusicSongService.getSongCoverUri("0012vTYD3iRdKJ", qqMusicCookie));
  }

  @Test
  void getSongLink() {
    System.out.println(
        qqMusicSongService.getSongLink("001mzBxY4UVcAH", "128", "000f6dQ63WCT60", qqMusicCookie));
  }

  @Test
  void getSongsLink() {
    System.out.println(qqMusicSongService.getSongsLink("000Miat72wA0e6", qqMusicCookie));
  }

  @Test
  void searchSongByName() {
    System.out.println(qqMusicSongService.searchSongByName("洛天依", 1, 20, qqMusicCookie));
  }
}
