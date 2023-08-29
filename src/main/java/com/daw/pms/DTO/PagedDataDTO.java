package com.daw.pms.DTO;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * DTO for paged data.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 8/2/23
 */
@Data
public class PagedDataDTO<T> implements Serializable {
  /** The total count of target data. */
  private int count;

  /** The paged data. */
  private List<T> list;

  /** Whether there has more data. */
  private Boolean hasMore;
}
