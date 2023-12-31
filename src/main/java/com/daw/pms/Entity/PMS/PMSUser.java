package com.daw.pms.Entity.PMS;

import com.daw.pms.Entity.Basic.BasicUser;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * User stored in playlist master server.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/19/23
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PMSUser extends BasicUser {
  /** User id in playlist master server. */
  private Long id;

  /** Description of pms user. */
  private String intro;

  /** All managed sub users in playlist master server. */
  private Map<String, ? extends BasicUser> subUsers;
}
