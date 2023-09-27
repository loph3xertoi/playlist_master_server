package com.daw.pms.Filters;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

/**
 * CORS filter.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/21/23
 */
@WebFilter("/*")
public class CorsFilter implements Filter {
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletResponse httpServletResponse = (HttpServletResponse) response;
    httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
    httpServletResponse.setHeader("Access-Control-Allow-Methods", "*");
    httpServletResponse.setHeader("Access-Control-Max-Age", "36000");
    httpServletResponse.setHeader("Access-Control-Allow-Headers", "*");
    httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
    httpServletResponse.setHeader("Access-Control-Expose-Headers", "*");
    httpServletResponse.setHeader("X-Frame-Options", "SAMEORIGIN");
    httpServletResponse.setHeader(
        "Content-Security-Policy", "frame-src 'self' https://playlistmaster.fun;");
    chain.doFilter(request, response);
  }
}
