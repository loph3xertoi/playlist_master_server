package com.daw.pms.Entity.Bilibili;

import com.daw.pms.Entity.Basic.BasicLibrary;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Basic fav list of bilibili.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 8/1/23
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BiliFavList extends BasicLibrary {
  /** The id of this fav list. */
  private Long id;

  /** The fid of the fav list. */
  private Long fid;

  /** The mid of the user. */
  private Long mid;

  /** The upper's name of this fav list. */
  private String upperName;

  /** The view count of this fav list. */
  private Long viewCount;

  /** The type of this fav list, 0 for created fav list, 1 for collected fav list. */
  private Integer type;
}
