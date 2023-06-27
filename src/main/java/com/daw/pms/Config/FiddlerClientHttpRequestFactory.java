package com.daw.pms.Config;

import java.io.IOException;
import java.net.URI;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

public class FiddlerClientHttpRequestFactory implements ClientHttpRequestFactory {
  private final HttpComponentsClientHttpRequestFactory delegate;

  public FiddlerClientHttpRequestFactory() {
    HttpHost proxy = new HttpHost("localhost", 8866);
    DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
    HttpClient httpClient = HttpClientBuilder.create().setRoutePlanner(routePlanner).build();
    delegate = new HttpComponentsClientHttpRequestFactory(httpClient);
  }

  @Override
  public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
    return delegate.createRequest(uri, httpMethod);
  }
}
