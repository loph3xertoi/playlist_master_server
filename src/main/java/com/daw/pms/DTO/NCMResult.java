package com.daw.pms.DTO;

import java.util.List;
import java.util.Map;

/**
 * Result from netease cloud music proxy api server.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 7/22/23
 */
public class NCMResult {
  private Integer status;
  private Map<String, String> body;
  private List<String> cookie;
}
