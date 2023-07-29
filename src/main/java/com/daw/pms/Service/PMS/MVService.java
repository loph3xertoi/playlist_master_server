package com.daw.pms.Service.PMS;

import com.daw.pms.Entity.Basic.BasicVideo;
import java.util.List;
import java.util.Map;

/**
 * Service for handle MV.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 6/28/23
 */
public interface MVService {
  /**
   * @param vid The vid/mvid/mlogId of the mv.
   * @param platform The platform id.
   * @return The detail information of the mv {@code vid}.
   */
  BasicVideo getDetailMV(String vid, Integer platform);

  /**
   * @param vids The vids of the mv(s), multi vids separated by comma.
   * @param platform The platform id.
   * @return The urls for the mv(s).
   * @deprecated Make a uniform result between different platforms.
   */
  Map<String, List<String>> getMVsLink(String vids, Integer platform);

  /**
   * Get all related videos with the song.
   *
   * @param songId The song's id.
   * @param mvId The mv's id, only in ncm platform.
   * @param limit The limit of related videos, only in ncm platform.
   * @param platform The platform id.
   * @return All the related video about the song with {@code songId}.
   */
  List<BasicVideo> getRelatedVideos(Long songId, String mvId, Integer limit, Integer platform);
}
