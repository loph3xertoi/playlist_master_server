package com.daw.pms.Entity.PMS;

import com.daw.pms.Entity.Basic.BasicUser;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/** User stored in playlist master server. */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PMSUser extends BasicUser {
  /** User id in playlist master server. */
  private Long id;

  /** Description of pms user. */
  private String intro;

  /** All managed sub users in playlist master server. */
  private Map<String, BasicUser> subUsers;
}
