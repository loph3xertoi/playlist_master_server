package com.daw.pms.Utils;

import com.daw.pms.Entity.OAuth2.GitHubOAuth2User;
import com.daw.pms.Entity.OAuth2.GoogleOAuth2User;
import com.daw.pms.Entity.PMS.PMSUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class PMSUserDetailsUtil {
  public static Long getCurrentLoginUserId() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    Authentication authentication = securityContext.getAuthentication();
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

  public static String getCurrentLoginUserEmail() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    Authentication authentication = securityContext.getAuthentication();
    PMSUserDetails pmsUserDetails = (PMSUserDetails) authentication.getPrincipal();
    return pmsUserDetails.getUser().getEmail();
  }
}
