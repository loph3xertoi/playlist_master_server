package com.daw.pms.DTO;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDTO implements Serializable {
  private Long id;
  @NotBlank private String name;
  @NotBlank private String pass;
  @NotBlank private String role;
  private String email;
  private String phone;
  @NotBlank private Boolean enabled;
  @NotBlank private Integer loginType;
  private String intro;
  private String avatar;
  private String bgPic;
  private Long qqmusicId;
  private String qqmusicCookie;
  private Long ncmId;
  private String ncmCookie;
  private Long bilibiliId;
  private String biliCookie;
}
