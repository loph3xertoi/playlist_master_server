package com.daw.pms.Service.impl;

import static org.junit.jupiter.api.Assertions.*;

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

  @Test
  void getBasicSongsFromPlaylist() {
    System.out.println(
        qqMusicSongService.getBasicSongsFromPlaylist(
            "4146457838", QQMusicCookieServiceImplTest.cookie));
  }

  // 002GNiJS1BwnQJ 003aW4ny3SpmBa
  @Test
  void getDetailSong() {
    System.out.println(
        qqMusicSongService.getDetailSong("002GNiJS1BwnQJ", QQMusicCookieServiceImplTest.cookie));
  }

  @Test
  void getSimilarSongs() {
    System.out.println(
        qqMusicSongService.getSimilarSongs("102060511", QQMusicCookieServiceImplTest.cookie));
  }

  @Test
  void getLyrics() {
    System.out.println(
        qqMusicSongService.getLyrics("001UiydT2NNPir", QQMusicCookieServiceImplTest.cookie));
  }

  @Test
  void getSongLink() {
    System.out.println(
        qqMusicSongService.getSongLink(
            "003nkjOy4dtZxc", "128", "003nkjOy4dtZxc", QQMusicCookieServiceImplTest.cookie));
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
