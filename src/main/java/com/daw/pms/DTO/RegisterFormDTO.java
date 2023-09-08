package com.daw.pms.DTO;

import com.daw.pms.Annotation.ValidPassword;
import java.io.Serializable;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterFormDTO implements Serializable {
  @NotBlank(message = "No blank name.")
  private String name;

  @NotBlank(message = "No blank email.")
  @Email(message = "Email format error.")
  private String email;

  @ValidPassword private String password;
}
