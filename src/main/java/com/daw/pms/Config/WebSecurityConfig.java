package com.daw.pms.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

/**
 * WebSecurityConfig class.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/19/23
 */
@Configuration
@EnableWebSecurity(debug = true)
public class WebSecurityConfig {
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
        .authorizeHttpRequests()
        .antMatchers(
            HttpMethod.POST,
            "/login",
            "/register",
            "/verify/nologin/signUp",
            "/verify/nologin/resetPassword")
        .permitAll()
        .antMatchers(
            HttpMethod.GET,
            "/login/oauth2/github",
            "/login/oauth2/google",
            "/hello",
            "/logout/success",
            "/error",
            "/sendcode",
            "/cors/bili/splash")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/verify/resetPassword", "/verify/bindEmail")
        .hasAnyRole(String.valueOf(UserRole.USER), String.valueOf(UserRole.ADMIN))
        //        .antMatchers(HttpMethod.GET, "/users")
        //        .hasRole(String.valueOf(UserRole.ADMIN))
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
        .and()
        .sessionManagement()
        .maximumSessions(1)
        .maxSessionsPreventsLogin(true)
        .and()
        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
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
   * webSecurityCustomizer.
   *
   * @return a {@link
   *     org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer}
   *     object.
   */
  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return (web) ->
        web.ignoring()
            .antMatchers(
                HttpMethod.GET,
                "/",
                "/users",
                "/index.html",
                "/robots.txt",
                "/favicon.ico",
                "/css/**",
                "/js/**",
                "/images/**",
                "/xml/**",
                "/mpd/**");
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
}
