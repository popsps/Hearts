package edu.gmu.server.security;

import edu.gmu.server.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

@Slf4j
@Service
public class CookieProvider {
  private final JwtProvider jwtProvider;

  @Autowired
  public CookieProvider(JwtProvider jwtProvider) {
    this.jwtProvider = jwtProvider;
  }

  public String base64UrlDecode(final String token) {
    return new String(Base64.getUrlDecoder().decode(token));
  }

  public String base64UrlEncode(final String str) {
    return Base64.getUrlEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
  }

  public Optional<Cookie> createSessionCookie(User user) {
    try {
      String token = this.jwtProvider.createToken(user.getUsername(), user.getRole());
      final Cookie sessionCookie = new Cookie("session", this.base64UrlEncode(token));
      sessionCookie.setPath("/");
      sessionCookie.setHttpOnly(true);
      sessionCookie.setSecure(true);
      return Optional.of(sessionCookie);
    } catch (Exception e) {
      return Optional.empty();
    }
  }
}
