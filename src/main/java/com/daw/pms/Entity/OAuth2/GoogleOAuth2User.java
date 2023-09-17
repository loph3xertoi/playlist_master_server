package com.daw.pms.Entity.OAuth2;

import java.util.Collection;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoogleOAuth2User implements OAuth2User {
  private Long id;
  private OAuth2User oauth2User;
  private OAuth2AccessToken oauth2AccessToken;
  private String email;

  public GoogleOAuth2User(OAuth2User oauth2User) {
    this.oauth2User = oauth2User;
  }

  @Override
  public Map<String, Object> getAttributes() {
    return oauth2User.getAttributes();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return oauth2User.getAuthorities();
  }

  @Override
  public String getName() {
    return oauth2User.getAttribute("name");
  }

  public String getAvatar() {
    return oauth2User.getAttribute("picture");
  }
}
