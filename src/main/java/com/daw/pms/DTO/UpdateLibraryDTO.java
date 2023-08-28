package com.daw.pms.DTO;

import java.io.Serializable;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateLibraryDTO implements Serializable {
  private Long id;
  private String name;
  private String intro;
  private MultipartFile cover;
}
