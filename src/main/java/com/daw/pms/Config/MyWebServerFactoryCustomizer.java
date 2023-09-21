package com.daw.pms.Config;

import javax.annotation.PostConstruct;
import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

@Component
public class MyWebServerFactoryCustomizer
    implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

  private static final Logger LOGGER = LoggerFactory.getLogger(MyWebServerFactoryCustomizer.class);
  private final int httpPort;
  private final int redirectToHttpsPort;

  @Autowired
  MyWebServerFactoryCustomizer(
      @Value("${server.http-port}") int httpPort,
      @Value("${server.port}") int redirectToHttpsPort) {
    this.httpPort = httpPort;
    this.redirectToHttpsPort = redirectToHttpsPort;
  }

  @PostConstruct
  void postConstruct() {
    LOGGER.debug("postConstruct() | INVOKED");
  }

  @Override
  public void customize(TomcatServletWebServerFactory factory) {
    factory.addAdditionalTomcatConnectors(redirectConnector());
  }

  private Connector redirectConnector() {
    Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
    connector.setScheme("http");
    connector.setPort(httpPort);
    connector.setSecure(false);
    connector.setRedirectPort(redirectToHttpsPort);
    return connector;
  }
}
