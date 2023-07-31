package com.daw.pms.Service.Bilibili.impl;

import com.daw.pms.Config.BilibiliAPI;
import com.daw.pms.Service.Bilibili.BilibiliCookieService;
import com.daw.pms.Utils.HttpTools;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class BilibiliCookieServiceImpl implements BilibiliCookieService {
  private final HttpTools httpTools;

  public BilibiliCookieServiceImpl(HttpTools httpTools) {
    this.httpTools = httpTools;
  }

  /**
   * Get wbi key.
   *
   * @return Img key and sub key.
   */
  @Cacheable(value = "bilibili-wbi-key", key = "#root.methodName", unless = "#result==null")
  @Override
  public Map<String, String> getWbiKey() {
    String rawWbiKey = httpTools.requestGetAPI(BilibiliAPI.GET_WBI_KEY, null, Optional.empty());
    return extractWbiKey(rawWbiKey);
  }

  Map<String, String> extractWbiKey(String rawWbiKey) {
    Map<String, String> map = new HashMap<>();
    JsonNode jsonNode;
    try {
      jsonNode = new ObjectMapper().readTree(rawWbiKey);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    JsonNode wbiKeyNode = jsonNode.get("data").get("wbi_img");
    String imgKeyString = wbiKeyNode.get("img_url").textValue();
    String subKeyString = wbiKeyNode.get("sub_url").textValue();
    String regex = ".*/([a-fA-F0-9]{32})\\..*";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(imgKeyString);
    if (matcher.matches()) {
      String imgKey = matcher.group(1);
      map.put("img_key", imgKey);
    } else {
      throw new RuntimeException("Cannot resolve img key.");
    }
    matcher.reset(subKeyString);
    if (matcher.matches()) {
      String subKey = matcher.group(1);
      map.put("sub_key", subKey);
    } else {
      throw new RuntimeException("Cannot resolve sub key.");
    }
    return map;
  }
}
