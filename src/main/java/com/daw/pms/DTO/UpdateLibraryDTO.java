package com.daw.pms.DTO;

import java.io.Serializable;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * DTO used when updating library.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/18/23
 */
@Data
public class UpdateLibraryDTO implements Serializable {
  /** Library's id. */
  private Long id;

  /** New name for library. */
  private String name;

  /** New introduction for library. */
  private String intro;

  /** New cover for library. */
  private MultipartFile cover;
}
