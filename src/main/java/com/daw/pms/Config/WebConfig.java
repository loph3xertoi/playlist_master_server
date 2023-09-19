package com.daw.pms.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

/**
 * Web mvn config.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/19/23
 */
@Configuration
// @EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
  /** {@inheritDoc} */
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**").allowedOrigins("*").allowedMethods("GET", "POST", "PUT", "DELETE");
  }

  //  @Override
  //  public void addResourceHandlers(ResourceHandlerRegistry registry) {
  //    registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
  //  }
}
