package com.daw.pms.Utils;

import com.daw.pms.Entity.OAuth2.CustomOAuth2User;
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
    } else if (principal.getClass().equals(CustomOAuth2User.class)) {
      CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
      return customOAuth2User.getId();
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
