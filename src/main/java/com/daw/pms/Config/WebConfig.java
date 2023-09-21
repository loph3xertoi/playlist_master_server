package com.daw.pms.Config;

import com.daw.pms.Filters.CorsFilter;
import javax.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
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
public class WebConfig implements WebMvcConfigurer {
  @Bean
  public FilterRegistrationBean<Filter> afterAuthFilterRegistrationBean() {
    FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
    CorsFilter corsFilter = new CorsFilter();
    registrationBean.setFilter(corsFilter);
    registrationBean.setOrder(10);
    return registrationBean;
  }
}
