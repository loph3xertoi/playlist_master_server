package com.daw.pms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class PlaylistMasterServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(PlaylistMasterServerApplication.class, args);
  }
}
