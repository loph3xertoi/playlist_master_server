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

@Configuration
@EnableWebSecurity(debug = true)
public class WebSecurityConfig {
  private final UserDetailsService userDetailsService;

  public WebSecurityConfig(UserDetailsService userDetailsService) {
    this.userDetailsService = userDetailsService;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new PepperedPasswordEncoder();
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

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
            HttpMethod.GET, "/hello", "/logout/success", "/error", "/sendcode", "/cors/bili/splash")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/verify")
        .hasAnyRole(String.valueOf(UserRole.USER), String.valueOf(UserRole.ADMIN))
        .antMatchers(HttpMethod.GET, "/users")
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

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return (web) -> web.ignoring().antMatchers(HttpMethod.GET, "/images/**", "/xml/**", "/mpd/**");
  }

  @Bean
  public SessionRegistry sessionRegistry() {
    return new SessionRegistryImpl();
  }
}
