package com.daw.pms.Service.PMS.impl;

import com.daw.pms.DTO.Result;
import com.daw.pms.Entity.Basic.BasicSong;
import com.daw.pms.Service.PMS.SongService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SongServiceTest {
  @Autowired SongService songService;

  @Test
  void getDetailSong() {
    try {
      Result pmsDetailSong = songService.getDetailSong("0", 0);
      System.out.println("pmsDetailSong: \n" + pmsDetailSong);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Result qqmusicDetailSong = songService.getDetailSong("0043hnU117oa5s", 1);
      System.out.println("qqmusicDetailSong: \n" + qqmusicDetailSong);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Result ncmDetailSong = songService.getDetailSong("185920", 2);
      System.out.println("ncmDetailSong: \n" + ncmDetailSong);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Result bilibiliDetailSong = songService.getDetailSong("1", 3);
      System.out.println("bilibiliDetailSong: \n" + bilibiliDetailSong);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  void getSimilarSongs() {
    try {
      List<BasicSong> pmsSimilarSongs = songService.getSimilarSongs("1", 0);
      System.out.println("pmsSimilarSongs: \n" + pmsSimilarSongs);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      List<BasicSong> qqmusicSimilarSongs = songService.getSimilarSongs("240442614", 1);
      System.out.println("qqmusicSimilarSongs: \n" + qqmusicSimilarSongs);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      List<BasicSong> ncmSimilarSongs = songService.getSimilarSongs("347230", 2);
      System.out.println("ncmSimilarSongs: \n" + ncmSimilarSongs);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      List<BasicSong> bilibiliSimilarSongs = songService.getSimilarSongs("1", 3);
      System.out.println("bilibiliSimilarSongs: \n" + bilibiliSimilarSongs);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  void getSongsLink() {
    try {
      Result pmsSongsLink = songService.getSongsLink("0,1", "standard", 0);
      System.out.println("pmsSongsLink: \n" + pmsSongsLink);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Result qqmusicSongsLink =
          songService.getSongsLink(
              "0029TIOX3jpfrT,002GNiJS1BwnQJ,0043hnU117oa5s,002yL3HS0nCeSD", "standard", 1);
      System.out.println("qqmusicSongsLink: \n" + qqmusicSongsLink);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Result ncmSongsLink = songService.getSongsLink("186125,185912,16835303", "higher", 2);
      System.out.println("ncmSongsLink: \n" + ncmSongsLink);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Result bilibiliSongsLink = songService.getSongsLink("0,1", "standard", 3);
      System.out.println("bilibiliSongsLink: \n" + bilibiliSongsLink);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  void searchResourcesByKeywords() {
    try {
      Result result = songService.searchResourcesByKeyword("洛天依", 0, 10, 1, 0);
      System.out.println("pmsPagedSongs: \n" + result);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Result qqmusicPagedSongs = songService.searchResourcesByKeyword("洛天依", 0, 10, 1, 1);
      System.out.println("qqmusicPagedSongs: \n" + qqmusicPagedSongs);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Result ncmPagedSongs = songService.searchResourcesByKeyword("洛天依", 0, 10, 1, 2);
      System.out.println("ncmPagedSongs: \n" + ncmPagedSongs);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Result bilibiliPagedSongs = songService.searchResourcesByKeyword("洛天依", 1, 10, 1, 3);
      System.out.println("bilibiliPagedSongs: \n" + bilibiliPagedSongs);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
