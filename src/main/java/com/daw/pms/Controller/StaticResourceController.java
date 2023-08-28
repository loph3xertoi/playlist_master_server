package com.daw.pms.Controller;

import java.io.IOException;
import java.nio.file.Files;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/static")
public class StaticResourceController {
  @GetMapping("/images/default_library_cover.png")
  public ResponseEntity<byte[]> getDefaultLibraryCover() throws IOException {
    ClassPathResource imgFile = new ClassPathResource("static/images/default_library_cover.png");
    byte[] imageBytes = Files.readAllBytes(imgFile.getFile().toPath());
    return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(imageBytes);
  }
}
