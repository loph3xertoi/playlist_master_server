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
   * @param vid The vid of the mv.
   * @param platform The platform id.
   * @return The detail information of the mv {@code vid}.
   */
  BasicVideo getDetailMV(String vid, Integer platform);

  /**
   * @param vids The vids of the mv(s), multi vids separated by comma.
   * @param platform The platform id.
   * @return The urls for the mv(s).
   */
  Map<String, List<String>> getMVsLink(String vids, Integer platform);

  /**
   * @param songId The song id.
   * @param platform The platform id.
   * @return All the related video about the song with {@code songId}.
   */
  List<BasicVideo> getRelatedVideos(Integer songId, Integer platform);
}
