package com.daw.pms.Service.impl;

import com.daw.pms.Service.QQMusic.impl.QQMusicPlaylistServiceImpl;
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
        qqMusicPlaylistService.deletePlaylist("22,23", QQMusicCookieServiceImplTest.cookie));
  }

  @Test
  void addSongsToPlaylist() {
    System.out.println(
        qqMusicPlaylistService.addSongsToPlaylist(
            22,
            "003nkjOy4dtZxc,000idahy2pT761,001OgIGc0B4OEL",
            QQMusicCookieServiceImplTest.cookie));
  }

  @Test
  void moveSongsToOtherPlaylist() {
    System.out.println(
        qqMusicPlaylistService.moveSongsToOtherPlaylist(
            "105302677,414119681,414478884", 20, 21, QQMusicCookieServiceImplTest.cookie));
  }

  @Test
  void removeSongsFromPlaylist() {
    System.out.println(
        qqMusicPlaylistService.removeSongsFromPlaylist(
            22, "105302677,414119681,414478884", QQMusicCookieServiceImplTest.cookie));
  }
}
