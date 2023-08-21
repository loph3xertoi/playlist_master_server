package com.daw.pms.Utils;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Class for send http requests.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 7/21/23
 */
@Component
public class HttpTools {
  /** Host of proxy qq music api server. */
  @Value("${qqmusic.proxy.host}")
  public String qqmusicHost;

  /** Port of proxy qq music api server. */
  @Value("${qqmusic.proxy.port}")
  public String qqmusicPort;

  /** Host of proxy netease cloud music api server. */
  @Value("${ncm.proxy.host}")
  public String ncmHost;

  /** Port of proxy netease cloud music api server. */
  @Value("${ncm.proxy.port}")
  public String ncmPort;

  private final RestTemplate restTemplate;

  public HttpTools(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  /**
   * Send http get request to {@code api} with parameters {@code params} and {@code cookie}.
   *
   * @param url Remote url you want to call.
   * @param params Request parameters.
   * @param cookie Your cookie for corresponding platform.
   * @return Result in string form.
   */
  public <K, V> String requestGetAPI(String url, Map<K, V> params, Optional<String> cookie) {
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
    if (params != null) {
      params.forEach(
          (k, v) -> {
            try {
              builder.queryParam((String) k, URLEncoder.encode(String.valueOf(v), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
              throw new RuntimeException(e);
            }
          });
    }
    URI uri = builder.build(true).toUri();
    HttpHeaders headers = new HttpHeaders();
    headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.set(
        "User-Agent",
        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");
    cookie.ifPresent(s -> headers.set("Cookie", s));
    HttpEntity<?> entity = new HttpEntity<>(headers);
    ResponseEntity<String> response =
        restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
    return response.getBody();
  }

  /**
   * Send http post request to {@code api} with parameters {@code params} and {@code cookie}.
   *
   * @param url Remote url you want to call.
   * @param params Request body.
   * @param referer The referer in header.
   * @param cookie Your cookie for corresponding platform.
   * @return Result in string form.
   */
  public <K, V> String requestPostAPI(
      String url, Map<K, V> params, Optional<String> referer, Optional<String> cookie) {
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
    URI uri = builder.build(true).toUri();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    headers.set(
        "User-Agent",
        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");
    referer.ifPresent(s -> headers.set("Referer", s));
    cookie.ifPresent(s -> headers.set("Cookie", s));
    HttpEntity<?> requestEntity = new HttpEntity<>(params, headers);
    ResponseEntity<String> response =
        restTemplate.exchange(uri, HttpMethod.POST, requestEntity, String.class);
    return response.getBody();
  }

  /**
   * Send http request to {@code api} with parameters {@code params} and {@code cookie}.
   *
   * @param url Remote url you want to call.
   * @param cookie Your cookie for corresponding platform.
   * @return Result in string form.
   */
  public String requestGetAPIByFinalUrl(String url, Optional<String> cookie) {
    HttpHeaders headers = new HttpHeaders();
    headers.set(
        "Accept",
        "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7;application/json");
    headers.set(
        "User-Agent",
        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");
    cookie.ifPresent(s -> headers.set("Cookie", s));
    HttpEntity<?> entity = new HttpEntity<>(headers);
    ResponseEntity<String> response =
        restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    return response.getBody();
  }

  /**
   * Send http request to {@code api} with parameters {@code params} and {@code cookie}.
   *
   * @param url Remote url you want to call.
   * @param cookie Your cookie for corresponding platform.
   * @return Image bytes.
   */
  public ResponseEntity<byte[]> requestGetImageByFinalUrl(String url, Optional<String> cookie) {
    HttpHeaders headers = new HttpHeaders();
    headers.set(
        "Accept",
        "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7;application/json");
    headers.set(
        "User-Agent",
        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");
    cookie.ifPresent(s -> headers.set("Cookie", s));
    HttpEntity<?> entity = new HttpEntity<>(headers);

    return restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);
  }
}
