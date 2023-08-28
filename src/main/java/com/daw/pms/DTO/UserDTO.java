package com.daw.pms.DTO;

import java.io.Serializable;
import lombok.Data;

@Data
public class UserDTO implements Serializable {
  private Long id;
  private String name;
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
