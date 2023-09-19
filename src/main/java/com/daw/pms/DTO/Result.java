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

  /**
   * ok.
   *
   * @return a {@link com.daw.pms.DTO.Result} object.
   */
  public static Result ok() {
    return new Result(true, null, null, null);
  }

  /**
   * ok.
   *
   * @param data a {@link java.lang.Object} object.
   * @return a {@link com.daw.pms.DTO.Result} object.
   */
  public static Result ok(Object data) {
    return new Result(true, null, data, null);
  }

  /**
   * ok.
   *
   * @param successMsg a {@link java.lang.String} object.
   * @param data a {@link java.lang.Object} object.
   * @return a {@link com.daw.pms.DTO.Result} object.
   */
  public static Result ok(String successMsg, Object data) {
    return new Result(true, successMsg, data, null);
  }

  /**
   * ok.
   *
   * @param data a {@link java.util.List} object.
   * @param total a {@link java.lang.Long} object.
   * @return a {@link com.daw.pms.DTO.Result} object.
   */
  public static Result ok(List<?> data, Long total) {
    return new Result(true, null, data, total);
  }

  /**
   * fail.
   *
   * @param errorMsg a {@link java.lang.String} object.
   * @return a {@link com.daw.pms.DTO.Result} object.
   */
  public static Result fail(String errorMsg) {
    return new Result(false, errorMsg, null, null);
  }
}
