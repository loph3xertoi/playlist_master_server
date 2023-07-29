package com.daw.pms.Service.NeteaseCloudMusic.impl;

import com.daw.pms.Entity.NeteaseCloudMusic.NCMDetailSong;
import com.daw.pms.Entity.NeteaseCloudMusic.NCMLyrics;
import com.daw.pms.Entity.NeteaseCloudMusic.NCMSearchSongsPagedResult;
import com.daw.pms.Entity.NeteaseCloudMusic.NCMSong;
import com.daw.pms.Service.NeteaseCloudMusic.NCMSongService;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
class NCMSongServiceImplTest {
  @Value("${ncm.cookie}")
  private String ncmCookie;

  @Autowired NCMSongService ncmSongService;

  @Test
  void getDetailSong() {
    NCMDetailSong detailSong = ncmSongService.getDetailSong("16835303", ncmCookie);
    System.out.println(detailSong);
  }

  @Test
  void getSimilarSongs() {
    List<NCMSong> similarSongs = ncmSongService.getSimilarSongs(1478965386L, ncmCookie);
    System.out.println(similarSongs);
  }

  @Test
  void getLyrics() {
    NCMLyrics lyrics = ncmSongService.getLyrics(1478965386L, ncmCookie);
    System.out.println(lyrics);
  }

  @Test
  void getSongsLink() {
    Map<String, String> songsLink =
        ncmSongService.getSongsLink("1478965386", "standard", ncmCookie);
    System.out.println(songsLink);
  }

  @Test
  void searchSongs() {
    NCMSearchSongsPagedResult songs =
        ncmSongService.searchResourcesByKeywords("洛天依", 0, 10, 1, ncmCookie);
    System.out.println(songs);
  }
}
