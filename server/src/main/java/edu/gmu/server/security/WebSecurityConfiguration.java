package edu.gmu.server.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

  final HeartsUserDetailsService userDetailsService;
  final JwtProvider jwtProvider;
  private final CookieProvider cookieProvider;

  @Autowired
  public WebSecurityConfiguration(HeartsUserDetailsService userDetailsService, JwtProvider jwtProvider, CookieProvider cookieProvider) {
    this.userDetailsService = userDetailsService;
    this.jwtProvider = jwtProvider;
    this.cookieProvider = cookieProvider;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    //    http.csrf().disable(); // only disable for testing
    http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
    http.headers().contentSecurityPolicy("script-src 'self'");
    http.headers().httpStrictTransportSecurity().includeSubDomains(true).maxAgeInSeconds(31536000);
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    http.authorizeRequests().antMatchers("/api/auth/authenticate", "/api/auth/register").permitAll();
    http.authorizeRequests().anyRequest().authenticated();
    http.addFilterBefore(
      new JwtTokenFilter(userDetailsService, jwtProvider, cookieProvider),
      UsernamePasswordAuthenticationFilter.class);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(11);
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }
}
