package com.daw.pms.Service.impl;

import com.daw.pms.Service.QQMusic.impl.QQMusicSongServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class QQMusicSongServiceImplTest {
  @Autowired QQMusicSongServiceImpl qqMusicSongService;

  @Test
  void getSongsIdFromPlaylist() {
    System.out.println(
        qqMusicSongService.getSongsIdFromPlaylist("14", QQMusicCookieServiceImplTest.cookie));
  }

  // 002GNiJS1BwnQJ 003aW4ny3SpmBa
  @Test
  void getDetailSong() {
    System.out.println(
        qqMusicSongService.getDetailSong("000Miat72wA0e6", QQMusicCookieServiceImplTest.cookie));
  }

  @Test
  void getSimilarSongs() {
    System.out.println(
        qqMusicSongService.getSimilarSongs("240442614", QQMusicCookieServiceImplTest.cookie));
  }

  @Test
  void getLyrics() {
    System.out.println(
        qqMusicSongService.getLyrics("001UiydT2NNPir", QQMusicCookieServiceImplTest.cookie));
  }

  // Only for album, the song cover can obtain by albummid directly.
  @Test
  void getSongCoverUri() {
    System.out.println(
        qqMusicSongService.getSongCoverUri("0012vTYD3iRdKJ", QQMusicCookieServiceImplTest.cookie));
  }

  @Test
  void getSongLink() {
    System.out.println(
        qqMusicSongService.getSongLink(
            "001mzBxY4UVcAH", "128", "000f6dQ63WCT60", QQMusicCookieServiceImplTest.cookie));
  }

  @Test
  void getSongsLink() {
    System.out.println(
        qqMusicSongService.getSongsLink("000Miat72wA0e6", QQMusicCookieServiceImplTest.cookie));
  }

  @Test
  void searchSongByName() {
    System.out.println(
        qqMusicSongService.searchSongByName("洛天依", 1, 20, QQMusicCookieServiceImplTest.cookie));
  }

  @Test
  void addSongsToPlaylist() {
    System.out.println(
        qqMusicSongService.addSongsToPlaylist(
            "20",
            "003nkjOy4dtZxc,000idahy2pT761,001OgIGc0B4OEL",
            QQMusicCookieServiceImplTest.cookie));
  }

  @Test
  void moveSongsToOtherPlaylist() {
    System.out.println(
        qqMusicSongService.moveSongsToOtherPlaylist(
            "105302677,414119681,414478884", "20", "21", QQMusicCookieServiceImplTest.cookie));
  }

  @Test
  void removeSongsFromPlaylist() {
    System.out.println(
        qqMusicSongService.removeSongsFromPlaylist(
            "21", "105302677,414119681,414478884", QQMusicCookieServiceImplTest.cookie));
  }
}
