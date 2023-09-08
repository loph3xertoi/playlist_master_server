package com.daw.pms.Utils;

import com.daw.pms.Entity.PMS.PMSUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class PMSUserDetailsUtil {
  public static Long getCurrentLoginUserId() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    Authentication authentication = securityContext.getAuthentication();
    PMSUserDetails pmsUserDetails = (PMSUserDetails) authentication.getPrincipal();
    return pmsUserDetails.getUser().getId();
  }

  public static String getCurrentLoginUserEmail() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    Authentication authentication = securityContext.getAuthentication();
    PMSUserDetails pmsUserDetails = (PMSUserDetails) authentication.getPrincipal();
    return pmsUserDetails.getUser().getEmail();
  }
}
