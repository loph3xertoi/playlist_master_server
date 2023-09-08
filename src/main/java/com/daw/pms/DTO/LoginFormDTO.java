package com.daw.pms.DTO;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginFormDTO implements Serializable {
  @NotBlank(message = "No blank name.")
  private String name;

  @NotBlank(message = "No blank password.")
  private String password;
}
