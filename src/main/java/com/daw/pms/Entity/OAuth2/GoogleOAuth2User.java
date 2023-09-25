package com.daw.pms.Entity.OAuth2;

import com.daw.pms.DTO.UserDTO;
import java.util.Collection;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * User entity, used for login with Google in spring security.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/18/23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoogleOAuth2User implements OAuth2User {
  /** User's id in pms. */
  private Long id;

  /** Original OAuth2User for Google. */
  private OAuth2User oauth2User;

  /** Access token. */
  private OAuth2AccessToken oauth2AccessToken;

  /** Bound email in Google. */
  private String email;

  /** User's id in QQ Music platform. */
  private Long qqmusicId;

  /** User's cookie in QQ Music platform. */
  private String qqmusicCookie;

  /** User's id in Netease Cloud Music platform. */
  private Long ncmId;

  /** User's cookie in Netease Cloud Music platform. */
  private String ncmCookie;

  /** User's id in BiliBili platform. */
  private Long bilibiliId;

  /** User's cookie in BiliBili platform. */
  private String biliCookie;

  /**
   * Constructor for GoogleOAuth2User.
   *
   * @param oauth2User a {@link org.springframework.security.oauth2.core.user.OAuth2User} object.
   * @param userDTO a {@link com.daw.pms.DTO.UserDTO} object.
   */
  public GoogleOAuth2User(OAuth2User oauth2User, UserDTO userDTO) {
    this.oauth2User = oauth2User;
    if (userDTO != null) {
      this.id = userDTO.getId();
      this.qqmusicId = userDTO.getQqmusicId();
      this.qqmusicCookie = userDTO.getQqmusicCookie();
      this.ncmId = userDTO.getNcmId();
      this.ncmCookie = userDTO.getNcmCookie();
      this.bilibiliId = userDTO.getBilibiliId();
      this.biliCookie = userDTO.getBiliCookie();
    }
  }

  /**
   * {@inheritDoc}
   *
   * <p>Get all attributes of original OAuth2User in Google.
   */
  @Override
  public Map<String, Object> getAttributes() {
    return oauth2User.getAttributes();
  }

  /**
   * {@inheritDoc}
   *
   * <p>Get all authorities of original OAuth2User in Google.
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return oauth2User.getAuthorities();
  }

  /**
   * {@inheritDoc}
   *
   * <p>Get your name in Google.
   */
  @Override
  public String getName() {
    return oauth2User.getAttribute("name");
  }

  /**
   * Get your avatar in Google.
   *
   * @return Your avatar in Google.
   */
  public String getAvatar() {
    return oauth2User.getAttribute("picture");
  }
}
