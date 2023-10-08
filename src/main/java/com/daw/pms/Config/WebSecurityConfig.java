package com.daw.pms.Config;

import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.apache.tomcat.util.http.SameSiteCookies;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

/**
 * WebSecurityConfig class.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/19/23
 */
@Configuration
@EnableWebSecurity()
// @EnableWebSecurity(debug = true)
public class WebSecurityConfig {
  @Value("${pms.remote-ip:127.0.0.1}")
  private String remoteServerIp;

  private final UserDetailsService userDetailsService;

  /**
   * Constructor for WebSecurityConfig.
   *
   * @param userDetailsService a {@link
   *     org.springframework.security.core.userdetails.UserDetailsService} object.
   */
  public WebSecurityConfig(UserDetailsService userDetailsService) {
    this.userDetailsService = userDetailsService;
  }

  /**
   * passwordEncoder.
   *
   * @return a {@link org.springframework.security.crypto.password.PasswordEncoder} object.
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new PepperedPasswordEncoder();
  }

  /**
   * authenticationProvider.
   *
   * @return a {@link org.springframework.security.authentication.dao.DaoAuthenticationProvider}
   *     object.
   */
  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  /**
   * securityFilterChain.
   *
   * @param http a {@link org.springframework.security.config.annotation.web.builders.HttpSecurity}
   *     object.
   * @return a {@link org.springframework.security.web.SecurityFilterChain} object.
   * @throws java.lang.Exception if any.
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf()
        .disable()
        .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
        .headers(
            headers ->
                headers.referrerPolicy(
                    referrer ->
                        referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER)))
        .authorizeRequests()
        .antMatchers(
            "/",
            //                      "/swagger-ui.html",
            //                      "/swagger-ui/**",
            //                      "/v3/api-docs/**",
            //                      "/v3/api-docs.yaml",
            //            "/users",
            "/index.html",
            "/robots.txt",
            "/favicon.ico",
            "/css/**",
            "/js/**",
            "/images/**",
            "/mpd/**",
            "/login",
            "/register",
            "/sendcode",
            "/verify/nologin/signUp",
            "/verify/nologin/resetPassword",
            "/login/oauth2/github",
            "/login/oauth2/google",
            "/logout/success",
            "/error",
            "/hello",
            "/cors/bili/splash")
        .permitAll()
        .antMatchers("/actuator/prometheus")
        .hasIpAddress(remoteServerIp)
        .antMatchers(HttpMethod.POST, "/verify/resetPassword", "/verify/bindEmail")
        .hasAnyRole(String.valueOf(UserRole.USER), String.valueOf(UserRole.ADMIN))
        .antMatchers(HttpMethod.GET, "/users", "/kick", "/kickAll", "/actuator")
        .hasRole(String.valueOf(UserRole.ADMIN))
        .antMatchers(HttpMethod.GET)
        .hasAnyRole(String.valueOf(UserRole.USER), String.valueOf(UserRole.ADMIN))
        .antMatchers(HttpMethod.POST)
        .hasRole(String.valueOf(UserRole.ADMIN))
        .antMatchers(HttpMethod.DELETE)
        .hasRole(String.valueOf(UserRole.ADMIN))
        .antMatchers(HttpMethod.PUT)
        .hasRole(String.valueOf(UserRole.ADMIN))
        .anyRequest()
        .authenticated()
        //        .and()
        //        .portMapper()
        //        .http(httpPort)
        //        .mapsTo(httpsPort)
        //        .and()
        //        .requiresChannel()
        //        .anyRequest()
        //        .requiresSecure()
        //        .and()
        //        .requestCache()
        //        .requestCache(new NullRequestCache())
        .and()
        .requiresChannel()
        .anyRequest()
        .requiresSecure()
        .and()
        .sessionManagement()
        .maximumSessions(1)
        .maxSessionsPreventsLogin(true)
        .and()
        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
        .and()
        .logout()
        .deleteCookies("JSESSIONID")
        .logoutSuccessUrl("/logout/success")
        .and()
        .exceptionHandling(
            customizer ->
                customizer.authenticationEntryPoint(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));
    return http.build();
  }

  /**
   * sessionRegistry.
   *
   * @return a {@link org.springframework.security.core.session.SessionRegistry} object.
   */
  @Bean
  public SessionRegistry sessionRegistry() {
    return new SessionRegistryImpl();
  }

  @Bean
  public TomcatContextCustomizer sameSiteCookiesConfig() {
    return context -> {
      final Rfc6265CookieProcessor cookieProcessor = new Rfc6265CookieProcessor();
      cookieProcessor.setSameSiteCookies(SameSiteCookies.NONE.getValue());
      context.setCookieProcessor(cookieProcessor);
      context.setUseHttpOnly(false);
    };
  }
}
