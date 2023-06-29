package com.daw.pms.Service.impl;

import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Wrap some base field and method for qq music service.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 6/3/23
 */
@Service
public class QQMusicBase {
  /** Host of proxy qq music api server. */
  @Value("${pm.remoteapi.qqmusic.host}")
  protected String host;

  /** Port of proxy qq music api server. */
  @Value("${pm.remoteapi.qqmusic.port}")
  protected String port;

  /** RestTemplate to send http request. */
  @Autowired protected RestTemplate restTemplate;

  /**
   * Send http request to {@code api} with parameters {@code params} and {@code cookie}.
   *
   * @param api Remote qq music api to be invoked.
   * @param params Request parameters.
   * @param cookie Your qq music cookie.
   * @return Result in string form.
   */
  protected <K, V> String requestGetAPI(String api, Map<K, V> params, Optional<String> cookie) {
    String url = host + ":" + port + api;
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
    params.forEach((k, v) -> builder.queryParam((String) k, v));
    HttpHeaders headers = new HttpHeaders();
    cookie.ifPresent(s -> headers.set("cookie", s));
    HttpEntity<?> entity = new HttpEntity<>(headers);
    ResponseEntity<String> response =
        restTemplate.exchange(
            builder.build(false).toUriString(), HttpMethod.GET, entity, String.class);
    return response.getBody();
  }
}
