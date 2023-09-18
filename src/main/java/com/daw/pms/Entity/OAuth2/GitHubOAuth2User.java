package com.daw.pms.Entity.OAuth2;

import java.util.Collection;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * Custom OAuth2User for GitHub.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/18/23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GitHubOAuth2User implements OAuth2User {
  /** User's id in pms. */
  private Long id;

  /** Original OAuth2User for GitHub. */
  private OAuth2User oauth2User;

  /** Access token. */
  private OAuth2AccessToken oauth2AccessToken;

  /** Bound email in GitHub. */
  private String email;

  public GitHubOAuth2User(OAuth2User oauth2User) {
    this.oauth2User = oauth2User;
  }

  /**
   * Get all attributes of original OAuth2User in GitHub.
   *
   * @return All attributes of original OAuth2User in GitHub.
   */
  @Override
  public Map<String, Object> getAttributes() {
    return oauth2User.getAttributes();
  }

  /**
   * Get all authorities of original OAuth2User in GitHub.
   *
   * @return All authorities of original OAuth2User in GitHub.
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return oauth2User.getAuthorities();
  }

  /**
   * Get your name in GitHub.
   *
   * @return Your name in GitHub.
   */
  @Override
  public String getName() {
    return oauth2User.getAttribute("login");
  }

  /**
   * Get your avatar in GitHub.
   *
   * @return Your avatar in GitHub.
   */
  public String getAvatar() {
    return oauth2User.getAttribute("avatar_url");
  }
}
