package com.daw.pms.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
  @Bean
  public RestTemplate restTemplate() {
    //    List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
    //    messageConverters.add(new FormHttpMessageConverter());
    return new RestTemplate();
    //    return new RestTemplate(new FiddlerClientHttpRequestFactory());
  }
}
