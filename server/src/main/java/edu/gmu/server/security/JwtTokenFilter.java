package edu.gmu.server.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;


@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {
  private final HeartsUserDetailsService userDetailsService;
  private final JwtProvider jwtProvider;
  private final CookieProvider cookieProvider;

  public JwtTokenFilter(HeartsUserDetailsService userDetailsService, JwtProvider jwtProvider,
                        CookieProvider cookieProvider) {
    this.userDetailsService = userDetailsService;
    this.jwtProvider = jwtProvider;
    this.cookieProvider = cookieProvider;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {

    try {
      log.info("Filter request; looking for authorization token");
      final Cookie[] cookies = request.getCookies();
      final Optional<Cookie> sessionCookie = Arrays.stream(cookies)
        .filter(cookie -> cookie.getName().matches("^session$")).findFirst();
      if (sessionCookie.isPresent()) {
        String jwtTokenDecoded = this.cookieProvider.base64UrlDecode(sessionCookie.get().getValue());
        if (this.jwtProvider.isValidToken(jwtTokenDecoded)) {
          Optional<UserDetails> userDetails = this.userDetailsService.loadUserByJwtToken(jwtTokenDecoded);
          if (userDetails.isPresent()) {
            UserDetails principal = userDetails.get();
            SecurityContextHolder.getContext()
              .setAuthentication(new PreAuthenticatedAuthenticationToken(principal, "", principal.getAuthorities()));
            log.info("user {} attempts to access resources", principal.getUsername());
          } else {
            log.error("Invalid token");
          }
        }
      }
    } catch (Exception e) {
      log.error("Not able to identify the user");
    }
    filterChain.doFilter(request, response);
  }
}
