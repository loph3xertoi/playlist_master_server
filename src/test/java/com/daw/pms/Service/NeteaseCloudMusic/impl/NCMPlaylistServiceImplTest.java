package com.daw.pms.Service.NeteaseCloudMusic.impl;

import com.daw.pms.DTO.Result;
import com.daw.pms.Entity.NeteaseCloudMusic.NCMSong;
import com.daw.pms.Service.NeteaseCloudMusic.NCMPlaylistService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class NCMPlaylistServiceImplTest {
  @Value("${ncm.id}")
  private Long ncmId;

  @Value("${ncm.cookie}")
  private String ncmCookie;

  @Autowired NCMPlaylistService ncmPlaylistService;

  private static Result library1;
  private static Result library2;

  @Test
  void getPlaylists() {
    Result playlists = ncmPlaylistService.getPlaylists(ncmId, 0, 10, ncmCookie);
    System.out.println(playlists);
  }

  @Test
  void getDetailPlaylist() {
    Result detailPlaylist = ncmPlaylistService.getDetailPlaylist(8574846185L, ncmCookie);
    System.out.println(detailPlaylist);
  }

  @Test
  void getAllSongsFromPlaylist() {
    List<NCMSong> allSongsFromPlaylist =
        ncmPlaylistService.getAllSongsFromPlaylist(
            8574846185L, Optional.empty(), Optional.empty(), ncmCookie);
    System.out.println(allSongsFromPlaylist);
  }

  @Test
  @Order(1)
  void createPlaylist() throws InterruptedException {
    library1 = ncmPlaylistService.createPlaylist("test1", ncmCookie);
    Thread.sleep(2000);
    library2 = ncmPlaylistService.createPlaylist("test2", ncmCookie);
    System.out.println("library1: " + library1);
    System.out.println("library2: " + library2);
  }

  @Test
  @Order(2)
  void addSongsToPlaylist() {
    Result result =
        ncmPlaylistService.addSongsToPlaylist(
            Long.valueOf(library1.getData().toString()), "347231,1860591464", ncmCookie);
    System.out.println(result);
  }

  @Test
  @Order(3)
  void moveSongsToOtherPlaylist() {
    Result result =
        ncmPlaylistService.moveSongsToOtherPlaylist(
            "347231,1860591464",
            Long.valueOf(library1.getData().toString()),
            Long.valueOf(library2.getData().toString()),
            ncmCookie);
    System.out.println(result);
  }

  @Test
  @Order(4)
  void removeSongsFromPlaylist() {
    Result result =
        ncmPlaylistService.removeSongsFromPlaylist(
            Long.valueOf(library2.getData().toString()), "347231,1860591464", ncmCookie);
    System.out.println(result);
  }

  @Test
  @Order(5)
  void deletePlaylist() {
    Result result =
        ncmPlaylistService.deletePlaylist(
            Long.valueOf(library1.getData().toString())
                + ","
                + Long.valueOf(library2.getData().toString()),
            ncmCookie);
    System.out.println(result);
  }
}
