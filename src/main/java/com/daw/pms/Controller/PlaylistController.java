package com.daw.pms.Controller;

import com.daw.pms.DTO.Result;
import com.daw.pms.Entity.QQMusicDetailPlaylist;
import com.daw.pms.Entity.QQMusicPlaylist;
import com.daw.pms.Service.PlaylistService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlaylistController {
  private final PlaylistService playlistService;

  public PlaylistController(PlaylistService playlistService) {
    this.playlistService = playlistService;
  }

  @GetMapping("/playlists/{uid}/{platformId}")
  public Result getPlaylists(
      @PathVariable(name = "uid") String uid,
      @PathVariable(name = "platformId") Integer platformId) {
    List<QQMusicPlaylist> playlists = playlistService.getPlaylists(uid, platformId);
    return Result.ok(playlists, (long) playlists.size());
  }

  @GetMapping("/detailplaylist/{playlistId}/{platformId}")
  public Result getDetailPlaylist(
      @PathVariable(name = "playlistId") String playlistId,
      @PathVariable(name = "platformId") Integer platformId) {
    QQMusicDetailPlaylist detailPlaylist =
        playlistService.getDetailPlaylist(playlistId, platformId);
    if (detailPlaylist.getTid() == null) {
      return Result.fail("The tid of playlist not exist.");
    }
    return Result.ok(detailPlaylist);
  }
}
