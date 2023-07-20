package com.daw.pms.Service.QQMusic.impl;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
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
  public String host;

  /** Port of proxy qq music api server. */
  @Value("${pm.remoteapi.qqmusic.port}")
  public String port;

  /** RestTemplate to send http request. */
  @Autowired public RestTemplate restTemplate;

  /**
   * Send http request to {@code api} with parameters {@code params} and {@code cookie}.
   *
   * @param api Remote qq music api to be invoked.
   * @param params Request parameters.
   * @param cookie Your qq music cookie.
   * @return Result in string form.
   */
  public <K, V> String requestGetAPI(String api, Map<K, V> params, Optional<String> cookie) {
    String url = host + ":" + port + api;
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
    params.forEach(
        (k, v) -> {
          try {
            builder.queryParam((String) k, URLEncoder.encode(String.valueOf(v), "UTF-8"));
          } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
          }
        });
    URI uri = builder.build(true).toUri();
    HttpHeaders headers = new HttpHeaders();
    headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
    cookie.ifPresent(s -> headers.set("cookie", s));
    HttpEntity<?> entity = new HttpEntity<>(headers);
    ResponseEntity<String> response =
        restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
    return response.getBody();
  }
}
