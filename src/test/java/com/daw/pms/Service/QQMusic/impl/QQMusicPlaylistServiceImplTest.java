package com.daw.pms.Service.QQMusic.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class QQMusicPlaylistServiceImplTest {
  @Value("${qqmusic.id}")
  private Long qqMusicId;

  @Value("${qqmusic.cookie}")
  private String qqMusicCookie;

  @Autowired QQMusicPlaylistServiceImpl qqMusicPlaylistService;

  @Test
  void getPlaylists() {
    System.out.println(qqMusicPlaylistService.getPlaylists(qqMusicId.toString(), qqMusicCookie));
  }

  @Test
  void getDetailPlaylist() {
    System.out.println(qqMusicPlaylistService.getDetailPlaylist("8729577481", qqMusicCookie));
  }

  @Test
  void createPlaylist() {
    System.out.println(qqMusicPlaylistService.createPlaylist("my playlist1", qqMusicCookie));
  }

  @Test
  void deletePlaylist() {
    System.out.println(qqMusicPlaylistService.deletePlaylist("22,23", qqMusicCookie));
  }

  @Test
  void addSongsToPlaylist() {
    System.out.println(
        qqMusicPlaylistService.addSongsToPlaylist(
            22, "003nkjOy4dtZxc,000idahy2pT761,001OgIGc0B4OEL", qqMusicCookie));
  }

  @Test
  void moveSongsToOtherPlaylist() {
    System.out.println(
        qqMusicPlaylistService.moveSongsToOtherPlaylist(
            "105302677,414119681,414478884", 20, 21, qqMusicCookie));
  }

  @Test
  void removeSongsFromPlaylist() {
    System.out.println(
        qqMusicPlaylistService.removeSongsFromPlaylist(
            22, "105302677,414119681,414478884", qqMusicCookie));
  }
}
