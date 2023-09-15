package com.daw.pms.Service.PMS.impl;

import com.daw.pms.Entity.OAuth2.CustomOAuth2User;
import com.daw.pms.Mapper.UserMapper;
import com.daw.pms.Utils.HttpTools;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class OAuth2UserDetailsServiceImpl extends DefaultOAuth2UserService {
  @Value("${pms.github-email-uri}")
  private String githubEmailUrl;

  private final HttpTools httpTools;
  private final UserMapper userMapper;

  public OAuth2UserDetailsServiceImpl(HttpTools httpTools, UserMapper userMapper) {
    this.httpTools = httpTools;
    this.userMapper = userMapper;
  }

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User user = super.loadUser(userRequest);
    OAuth2AccessToken accessToken = userRequest.getAccessToken();
    String userEmail =
        getUserEmail(
            accessToken.getTokenValue(), userRequest.getClientRegistration().getClientName());
    CustomOAuth2User customOAuth2User = new CustomOAuth2User(user);
    Long userId = userMapper.getUserIdByName(user.getName(), 1);
    if (userId != null) {
      customOAuth2User.setId(userId);
    }
    customOAuth2User.setEmail(userEmail);
    customOAuth2User.setOauth2AccessToken(accessToken);
    return customOAuth2User;
  }

  private String getUserEmail(String accessToken, String clientName) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + accessToken);
    headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
    String emailsJson =
        httpTools.requestGetAPIByFinalUrlWithProxy(githubEmailUrl, headers, Optional.empty());
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode emailsJsonNode;
    try {
      emailsJsonNode = objectMapper.readTree(emailsJson);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    for (JsonNode emailJsonNode : emailsJsonNode) {
      boolean primary = emailJsonNode.get("primary").booleanValue();
      if (primary) {
        return emailJsonNode.get("email").textValue();
      }
    }
    throw new RuntimeException("No primary email found in " + clientName);
  }
}
