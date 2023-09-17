package com.daw.pms.Service.PMS.impl;

import com.daw.pms.Entity.OAuth2.GitHubOAuth2User;
import com.daw.pms.Entity.OAuth2.GoogleOAuth2User;
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
import org.springframework.web.client.RestTemplate;

@Service
public class OAuth2UserDetailsServiceImpl extends DefaultOAuth2UserService {
  @Value("${pms.github-email-endpoint}")
  private String githubEmailEndpoint;

  private final HttpTools httpTools;
  private final UserMapper userMapper;

  public OAuth2UserDetailsServiceImpl(
      HttpTools httpTools, UserMapper userMapper, RestTemplate restTemplateWithProxy) {
    this.httpTools = httpTools;
    this.userMapper = userMapper;
    setRestOperations(restTemplateWithProxy);
  }

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User user = super.loadUser(userRequest);
    OAuth2AccessToken accessToken = userRequest.getAccessToken();
    String clientName = userRequest.getClientRegistration().getClientName();
    int loginType;
    String userEmail;
    if ("GitHub".equals(clientName)) {
      loginType = 1;
      userEmail = getUserEmail(accessToken.getTokenValue(), clientName);
      GitHubOAuth2User gitHubOAuth2User = new GitHubOAuth2User(user);
      Long userId = userMapper.getUserIdByName(user.getName(), loginType);
      if (userId != null) {
        gitHubOAuth2User.setId(userId);
      }
      gitHubOAuth2User.setEmail(userEmail);
      gitHubOAuth2User.setOauth2AccessToken(accessToken);
      return gitHubOAuth2User;
    } else if ("Google".equals(clientName)) {
      loginType = 2;
      userEmail = user.getAttribute("email");
      GoogleOAuth2User googleOAuth2User = new GoogleOAuth2User(user);
      Long userId = userMapper.getUserIdByName(user.getName(), loginType);
      if (userId != null) {
        googleOAuth2User.setId(userId);
      }
      googleOAuth2User.setEmail(userEmail);
      googleOAuth2User.setOauth2AccessToken(accessToken);
      return googleOAuth2User;
    } else {
      throw new RuntimeException("Unsupported client name: " + clientName);
    }
  }

  private String getUserEmail(String accessToken, String clientName) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + accessToken);
    headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
    String emailAPI;
    if ("GitHub".equals(clientName)) {
      emailAPI = githubEmailEndpoint;
    } else {
      throw new RuntimeException("Unsupported client name: " + clientName);
    }
    String emailsJson =
        httpTools.requestGetAPIByFinalUrlWithProxy(emailAPI, headers, Optional.empty());
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode emailsJsonNode;
    try {
      emailsJsonNode = objectMapper.readTree(emailsJson);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    if ("GitHub".equals(clientName)) {
      for (JsonNode emailJsonNode : emailsJsonNode) {
        boolean primary = emailJsonNode.get("primary").booleanValue();
        if (primary) {
          return emailJsonNode.get("email").textValue();
        }
      }
      throw new RuntimeException("No primary email found in " + clientName);
    } else {
      throw new RuntimeException("Unsupported client name: " + clientName);
    }
  }
}
