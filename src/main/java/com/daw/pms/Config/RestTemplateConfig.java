package com.daw.pms.Config;

import java.net.InetSocketAddress;
import java.net.Proxy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplateConfig.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/19/23
 */
@Configuration
public class RestTemplateConfig {
  @Value("${server.proxy.host}")
  private String proxyHost;

  @Value("${server.proxy.port}")
  private String proxyPort;

  //  @Value("${server.ssl.trust-store}")
  //  private String trustStore;
  //
  //  @Value("${server.ssl.trust-store-password}")
  //  private String trustStorePassword;

  /**
   * restTemplateWithProxy.
   *
   * @return a {@link org.springframework.web.client.RestTemplate} object.
   */
  @Bean
  public RestTemplate restTemplateWithProxy() {
    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    Proxy proxy =
        new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, Integer.parseInt(proxyPort)));
    requestFactory.setProxy(proxy);
    return new RestTemplate(requestFactory);
  }

  /**
   * restTemplate.
   *
   * @return a {@link org.springframework.web.client.RestTemplate} object.
   */
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  //  RestTemplate restTemplate() throws Exception {
  //    SSLContext sslContext = new SSLContextBuilder()
  //      .loadTrustMaterial(trustStore.getURL(), trustStorePassword.toCharArray())
  //      .build();
  //    SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);
  //    HttpClient httpClient = HttpClients.custom()
  //      .setSSLSocketFactory(socketFactory)
  //      .build();
  //    HttpComponentsClientHttpRequestFactory factory =
  //      new HttpComponentsClientHttpRequestFactory(httpClient);
  //    return new RestTemplate(factory);
  //  }

  //  private void disableCertificateVerification(SimpleClientHttpRequestFactory requestFactory)
  //      throws Exception {
  //    // Create a trust manager that does not validate certificate chains
  //    TrustManager[] trustAllCerts =
  //        new TrustManager[] {
  //          new X509TrustManager() {
  //            public X509Certificate[] getAcceptedIssuers() {
  //              return null;
  //            }
  //
  //            public void checkClientTrusted(X509Certificate[] certs, String authType) {}
  //
  //            public void checkServerTrusted(X509Certificate[] certs, String authType) {}
  //          }
  //        };
  //
  //    // Create an SSL context with the trust manager
  //    SSLContext sslContext = SSLContext.getInstance("TLS");
  //    sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
  //
  //    // Set the SSL context on the request factory
  //    HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
  //
  //    // Disable hostname verification
  //    HostnameVerifier hostnameVerifier = (hostname, session) -> true;
  //    HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
  //  }
}
