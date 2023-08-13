package com.daw.pms.Config;

import java.security.cert.X509Certificate;
import javax.net.ssl.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
  @Bean
  public RestTemplate restTemplate() {
    // Proxy for 127.0.0.1:8089 with mitmproxy.
    //    // Create a SimpleClientHttpRequestFactory instance
    //    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    //    try {
    //      disableCertificateVerification(requestFactory);
    //    } catch (Exception e) {
    //      throw new RuntimeException(e);
    //    }
    //    // Set the proxy host and port
    //    String proxyHost = "127.0.0.1";
    //    int proxyPort = 8089;
    //
    //    // Set the proxy on the request factory
    //    requestFactory.setProxy(
    //        new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort)));
    //
    //    // Create a RestTemplate instance
    //    return new RestTemplate(requestFactory);

    //    return new RestTemplate(new FiddlerClientHttpRequestFactory());

    return new RestTemplate();
  }

  private void disableCertificateVerification(SimpleClientHttpRequestFactory requestFactory)
      throws Exception {
    // Create a trust manager that does not validate certificate chains
    TrustManager[] trustAllCerts =
        new TrustManager[] {
          new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
              return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {}

            public void checkServerTrusted(X509Certificate[] certs, String authType) {}
          }
        };

    // Create an SSL context with the trust manager
    SSLContext sslContext = SSLContext.getInstance("TLS");
    sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

    // Set the SSL context on the request factory
    HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

    // Disable hostname verification
    HostnameVerifier hostnameVerifier = (hostname, session) -> true;
    HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
  }
}
