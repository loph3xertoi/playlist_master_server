package com.daw.pms.Config;

import java.util.Collections;
import javax.servlet.*;
import org.springframework.boot.web.servlet.ServletContextInitializer;
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
  public ServletContextInitializer servletContextInitializer() {
    return servletContext -> {
      servletContext.setSessionTrackingModes(Collections.singleton(SessionTrackingMode.COOKIE));
      SessionCookieConfig sessionCookieConfig = servletContext.getSessionCookieConfig();
      sessionCookieConfig.setHttpOnly(false);
      sessionCookieConfig.setSecure(false);
    };
  }
}
