package com.daw.pms.DTO;

import java.io.Serializable;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginFormDTO implements Serializable {
  @NotBlank(message = "No blank email.")
  @Email(message = "Email format error.")
  private String email;

  @NotBlank(message = "No blank password.")
  private String password;
}
