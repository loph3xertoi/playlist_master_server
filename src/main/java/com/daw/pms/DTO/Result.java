package com.daw.pms.DTO;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Common result DTO returned by controller.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/18/23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result implements Serializable {
  /** Indicating the success or not of this request, true of false. */
  private Boolean success;

  /** Error message if success is failed. */
  private String message;

  /** Real data if success. */
  private Object data;

  /** The data's length if it is list. */
  private Long total;

  public static Result ok() {
    return new Result(true, null, null, null);
  }

  public static Result ok(Object data) {
    return new Result(true, null, data, null);
  }

  public static Result ok(String successMsg, Object data) {
    return new Result(true, successMsg, data, null);
  }

  public static Result ok(List<?> data, Long total) {
    return new Result(true, null, data, total);
  }

  public static Result fail(String errorMsg) {
    return new Result(false, errorMsg, null, null);
  }
}
