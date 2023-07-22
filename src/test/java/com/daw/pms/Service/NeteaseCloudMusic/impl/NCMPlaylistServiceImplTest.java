package com.daw.pms.Service.NeteaseCloudMusic.impl;

import com.daw.pms.Entity.NeteaseCloudMusic.NCMDetailPlaylist;
import com.daw.pms.Entity.NeteaseCloudMusic.NCMPlaylist;
import com.daw.pms.Entity.NeteaseCloudMusic.NCMSong;
import com.daw.pms.GlobalConfig;
import com.daw.pms.Service.NeteaseCloudMusic.NCMPlaylistService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
class NCMPlaylistServiceImplTest {
  @Autowired NCMPlaylistService ncmPlaylistService;

  private static Long newCreatedPlaylistId1;
  private static Long newCreatedPlaylistId2;

  @Test
  void getPlaylists() {
    List<NCMPlaylist> playlists =
        ncmPlaylistService.getPlaylists(GlobalConfig.ncmId, 0, 10, GlobalConfig.ncmCookie);
    System.out.println(playlists);
  }

  @Test
  void getDetailPlaylist() {
    NCMDetailPlaylist detailPlaylist =
        ncmPlaylistService.getDetailPlaylist(8574846185L, GlobalConfig.ncmCookie);
    System.out.println(detailPlaylist);
  }

  @Test
  void getAllSongsFromPlaylist() {
    List<NCMSong> allSongsFromPlaylist =
        ncmPlaylistService.getAllSongsFromPlaylist(
            8574846185L, Optional.empty(), Optional.empty(), GlobalConfig.ncmCookie);
    System.out.println(allSongsFromPlaylist);
  }

  @Test
  @Order(1)
  void createPlaylist() throws InterruptedException {
    newCreatedPlaylistId1 = ncmPlaylistService.createPlaylist("test1", GlobalConfig.ncmCookie);
    Thread.sleep(2000);
    newCreatedPlaylistId2 = ncmPlaylistService.createPlaylist("test2", GlobalConfig.ncmCookie);
    System.out.println(newCreatedPlaylistId1 + "," + newCreatedPlaylistId2);
  }

  @Test
  @Order(2)
  void addSongsToPlaylist() {
    String result =
        ncmPlaylistService.addSongsToPlaylist(
            newCreatedPlaylistId1, "347231,1860591464", GlobalConfig.ncmCookie);
    System.out.println(result);
  }

  @Test
  @Order(3)
  void moveSongsToOtherPlaylist() {
    String result =
        ncmPlaylistService.moveSongsToOtherPlaylist(
            "347231,1860591464",
            newCreatedPlaylistId1,
            newCreatedPlaylistId2,
            GlobalConfig.ncmCookie);
    System.out.println(result);
  }

  @Test
  @Order(4)
  void removeSongsFromPlaylist() {
    String result =
        ncmPlaylistService.removeSongsFromPlaylist(
            newCreatedPlaylistId2, "347231,1860591464", GlobalConfig.ncmCookie);
    System.out.println(result);
  }

  @Test
  @Order(5)
  void deletePlaylist() {
    String result =
        ncmPlaylistService.deletePlaylist(
            newCreatedPlaylistId1 + "," + newCreatedPlaylistId2, GlobalConfig.ncmCookie);
    System.out.println(result);
  }
}
