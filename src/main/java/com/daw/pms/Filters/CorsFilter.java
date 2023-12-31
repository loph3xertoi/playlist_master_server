package com.daw.pms.Filters;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

/**
 * CORS filter.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/21/23
 */
@Component
@WebFilter("/*")
public class CorsFilter implements Filter {
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletResponse httpServletResponse = (HttpServletResponse) response;
    httpServletResponse.setHeader("Access-Control-Allow-Origin", "http://localhost:42653");
    httpServletResponse.setHeader("Access-Control-Allow-Methods", "*");
    httpServletResponse.setHeader("Access-Control-Max-Age", "36000");
    httpServletResponse.setHeader(
        "Access-Control-Allow-Headers",
        "Access-Control-Allow-Headers, Access-Control-Allow-Origin, Set-Cookie, Access-Control-Max-Age, Access-Control-Expose-Headers, Access-Control-Allow-Credentials, Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
    httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
    httpServletResponse.setHeader("Access-Control-Expose-Headers", "*");
    chain.doFilter(request, response);
  }
}
