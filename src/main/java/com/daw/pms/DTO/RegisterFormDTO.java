package com.daw.pms.DTO;

import com.daw.pms.Annotation.ValidPassword;
import com.daw.pms.Annotation.ValidPhoneNumber;
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

  @ValidPhoneNumber private String phoneNumber;

  @ValidPassword private String password;
}
