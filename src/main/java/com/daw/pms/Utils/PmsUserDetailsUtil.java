package com.daw.pms.Utils;

import com.daw.pms.Entity.OAuth2.GitHubOAuth2User;
import com.daw.pms.Entity.OAuth2.GoogleOAuth2User;
import com.daw.pms.Entity.PMS.PMSUserDetails;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Util for get current user's info.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/18/23
 */
@Component
public class PmsUserDetailsUtil {
  /**
   * Get current logged user's id.
   *
   * @return Current logged user's id.
   */
  public Long getCurrentLoginUserId() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    Authentication authentication = securityContext.getAuthentication();
    if (authentication == null || "anonymousUser".equals(authentication.getName())) {
      return null;
    }
    Object principal = authentication.getPrincipal();
    if (principal.getClass().equals(PMSUserDetails.class)) {
      PMSUserDetails pmsUserDetails = (PMSUserDetails) authentication.getPrincipal();
      return pmsUserDetails.getUser().getId();
    } else if (principal.getClass().equals(GitHubOAuth2User.class)) {
      GitHubOAuth2User gitHubOAuth2User = (GitHubOAuth2User) authentication.getPrincipal();
      return gitHubOAuth2User.getId();
    } else if (principal.getClass().equals(GoogleOAuth2User.class)) {
      GoogleOAuth2User googleOAuth2User = (GoogleOAuth2User) authentication.getPrincipal();
      return googleOAuth2User.getId();
    } else {
      throw new RuntimeException("Invalid login type");
    }
  }

  /**
   * Get current logged user's role.
   *
   * @return Current logged user's role.
   */
  public String getCurrentLoginUserRole() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    Authentication authentication = securityContext.getAuthentication();
    if (authentication == null || "anonymousUser".equals(authentication.getName())) {
      return null;
    }
    Object principal = authentication.getPrincipal();
    if (principal.getClass().equals(PMSUserDetails.class)) {
      PMSUserDetails pmsUserDetails = (PMSUserDetails) authentication.getPrincipal();
      return pmsUserDetails.getUser().getRole();
    } else if (principal.getClass().equals(GitHubOAuth2User.class)) {
      GitHubOAuth2User gitHubOAuth2User = (GitHubOAuth2User) authentication.getPrincipal();
      List<String> roles =
          gitHubOAuth2User.getOauth2User().getAuthorities().stream()
              .map(GrantedAuthority::getAuthority)
              .collect(Collectors.toList());
      if (roles.contains("ROLE_ADMIN")) {
        return "ROLE_ADMIN";
      } else if (roles.contains("ROLE_USER")) {
        return "ROLE_USER";
      } else {
        throw new RuntimeException("Invalid user role");
      }
    } else if (principal.getClass().equals(GoogleOAuth2User.class)) {
      GoogleOAuth2User googleOAuth2User = (GoogleOAuth2User) authentication.getPrincipal();
      List<String> roles =
          googleOAuth2User.getOauth2User().getAuthorities().stream()
              .map(GrantedAuthority::getAuthority)
              .collect(Collectors.toList());
      if (roles.contains("ROLE_ADMIN")) {
        return "ROLE_ADMIN";
      } else if (roles.contains("ROLE_USER")) {
        return "ROLE_USER";
      } else {
        throw new RuntimeException("Invalid user role");
      }
    } else {
      throw new RuntimeException("Invalid login type");
    }
  }

  /**
   * Get current logged user's email.
   *
   * @return Current logged user's email.
   */
  public String getCurrentLoginUserEmail() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    Authentication authentication = securityContext.getAuthentication();
    PMSUserDetails pmsUserDetails = (PMSUserDetails) authentication.getPrincipal();
    return pmsUserDetails.getUser().getEmail();
  }

  /**
   * Get qqmusic id of current logged user.
   *
   * @return Current logged user's qqmusic id.
   */
  public Long getCurrentLoginUserQqMusicId() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    Authentication authentication = securityContext.getAuthentication();
    Object principal = authentication.getPrincipal();
    if (principal.getClass().equals(PMSUserDetails.class)) {
      PMSUserDetails pmsUserDetails = (PMSUserDetails) authentication.getPrincipal();
      return pmsUserDetails.getUser().getQqmusicId();
    } else if (principal.getClass().equals(GitHubOAuth2User.class)) {
      GitHubOAuth2User gitHubOAuth2User = (GitHubOAuth2User) authentication.getPrincipal();
      return gitHubOAuth2User.getQqmusicId();
    } else if (principal.getClass().equals(GoogleOAuth2User.class)) {
      GoogleOAuth2User googleOAuth2User = (GoogleOAuth2User) authentication.getPrincipal();
      return googleOAuth2User.getQqmusicId();
    } else {
      throw new RuntimeException("Invalid login type");
    }
  }

  /**
   * Get ncm id of current logged user.
   *
   * @return Current logged user's ncm id.
   */
  public Long getCurrentLoginUserNcmId() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    Authentication authentication = securityContext.getAuthentication();
    Object principal = authentication.getPrincipal();
    if (principal.getClass().equals(PMSUserDetails.class)) {
      PMSUserDetails pmsUserDetails = (PMSUserDetails) authentication.getPrincipal();
      return pmsUserDetails.getUser().getNcmId();
    } else if (principal.getClass().equals(GitHubOAuth2User.class)) {
      GitHubOAuth2User gitHubOAuth2User = (GitHubOAuth2User) authentication.getPrincipal();
      return gitHubOAuth2User.getNcmId();
    } else if (principal.getClass().equals(GoogleOAuth2User.class)) {
      GoogleOAuth2User googleOAuth2User = (GoogleOAuth2User) authentication.getPrincipal();
      return googleOAuth2User.getNcmId();
    } else {
      throw new RuntimeException("Invalid login type");
    }
  }

  /**
   * Get bilibili id of current logged user.
   *
   * @return Current logged user's bilibili id.
   */
  public Long getCurrentLoginUserBilibiliId() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    Authentication authentication = securityContext.getAuthentication();
    Object principal = authentication.getPrincipal();
    if (principal.getClass().equals(PMSUserDetails.class)) {
      PMSUserDetails pmsUserDetails = (PMSUserDetails) authentication.getPrincipal();
      return pmsUserDetails.getUser().getBilibiliId();
    } else if (principal.getClass().equals(GitHubOAuth2User.class)) {
      GitHubOAuth2User gitHubOAuth2User = (GitHubOAuth2User) authentication.getPrincipal();
      return gitHubOAuth2User.getBilibiliId();
    } else if (principal.getClass().equals(GoogleOAuth2User.class)) {
      GoogleOAuth2User googleOAuth2User = (GoogleOAuth2User) authentication.getPrincipal();
      return googleOAuth2User.getBilibiliId();
    } else {
      throw new RuntimeException("Invalid login type");
    }
  }

  /**
   * Get qqmusic cookie of current logged user.
   *
   * @return Current logged user's qqmusic cookie.
   */
  public String getCurrentLoginUserQqMusicCookie() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    Authentication authentication = securityContext.getAuthentication();
    Object principal = authentication.getPrincipal();
    if (principal.getClass().equals(PMSUserDetails.class)) {
      PMSUserDetails pmsUserDetails = (PMSUserDetails) authentication.getPrincipal();
      return pmsUserDetails.getUser().getQqmusicCookie();
    } else if (principal.getClass().equals(GitHubOAuth2User.class)) {
      GitHubOAuth2User gitHubOAuth2User = (GitHubOAuth2User) authentication.getPrincipal();
      return gitHubOAuth2User.getQqmusicCookie();
    } else if (principal.getClass().equals(GoogleOAuth2User.class)) {
      GoogleOAuth2User googleOAuth2User = (GoogleOAuth2User) authentication.getPrincipal();
      return googleOAuth2User.getQqmusicCookie();
    } else {
      throw new RuntimeException("Invalid login type");
    }
  }

  /**
   * Get ncm cookie of current logged user.
   *
   * @return Current logged user's ncm cookie.
   */
  public String getCurrentLoginUserNcmCookie() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    Authentication authentication = securityContext.getAuthentication();
    Object principal = authentication.getPrincipal();
    if (principal.getClass().equals(PMSUserDetails.class)) {
      PMSUserDetails pmsUserDetails = (PMSUserDetails) authentication.getPrincipal();
      return pmsUserDetails.getUser().getNcmCookie();
    } else if (principal.getClass().equals(GitHubOAuth2User.class)) {
      GitHubOAuth2User gitHubOAuth2User = (GitHubOAuth2User) authentication.getPrincipal();
      return gitHubOAuth2User.getNcmCookie();
    } else if (principal.getClass().equals(GoogleOAuth2User.class)) {
      GoogleOAuth2User googleOAuth2User = (GoogleOAuth2User) authentication.getPrincipal();
      return googleOAuth2User.getNcmCookie();
    } else {
      throw new RuntimeException("Invalid login type");
    }
  }

  /**
   * Get bilibili cookie of current logged user.
   *
   * @return Current logged user's bilibili cookie.
   */
  public String getCurrentLoginUserBiliCookie() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    Authentication authentication = securityContext.getAuthentication();
    Object principal = authentication.getPrincipal();
    if (principal.getClass().equals(PMSUserDetails.class)) {
      PMSUserDetails pmsUserDetails = (PMSUserDetails) authentication.getPrincipal();
      return pmsUserDetails.getUser().getBiliCookie();
    } else if (principal.getClass().equals(GitHubOAuth2User.class)) {
      GitHubOAuth2User gitHubOAuth2User = (GitHubOAuth2User) authentication.getPrincipal();
      return gitHubOAuth2User.getBiliCookie();
    } else if (principal.getClass().equals(GoogleOAuth2User.class)) {
      GoogleOAuth2User googleOAuth2User = (GoogleOAuth2User) authentication.getPrincipal();
      return googleOAuth2User.getBiliCookie();
    } else {
      throw new RuntimeException("Invalid login type");
    }
  }
}
