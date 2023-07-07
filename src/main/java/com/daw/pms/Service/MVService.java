package com.daw.pms.Service;

import com.daw.pms.Entity.QQMusicMV;
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
  QQMusicMV getDetailMV(String vid, Integer platformId);

  Map<String, List<String>> getMVsLink(String vids, Integer platformId);
}
