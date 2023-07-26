package com.daw.pms.DTO;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result implements Serializable {
  private Boolean success;
  private String message;
  private Object data;
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
