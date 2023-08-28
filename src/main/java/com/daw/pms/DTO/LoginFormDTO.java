package com.daw.pms.DTO;

import java.io.Serializable;
import lombok.Data;

@Data
public class LoginFormDTO implements Serializable {
  private String phone;
  private String code;
  private String password;
}
