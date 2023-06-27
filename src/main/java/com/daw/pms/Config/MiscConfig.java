package com.daw.pms.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class MiscConfig {
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
    //    return new RestTemplate(new FiddlerClientHttpRequestFactory());
  }
}
