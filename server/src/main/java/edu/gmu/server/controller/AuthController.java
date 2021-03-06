package edu.gmu.server.controller;

import edu.gmu.server.dto.AuthenticateDto;
import edu.gmu.server.dto.RegisterDto;
import edu.gmu.server.entity.User;
import edu.gmu.server.security.CookieProvider;
import edu.gmu.server.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthService authService;
  private final CookieProvider cookieProvider;

  @Autowired
  public AuthController(AuthService authService, CookieProvider cookieProvider) {
    this.authService = authService;
    this.cookieProvider = cookieProvider;
  }

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public User register(@RequestBody @Valid RegisterDto registerDto) {
    return this.authService.register(registerDto)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "user is not available"));
  }

  @PostMapping("/authenticate")
  @ResponseStatus(HttpStatus.CREATED)
  public User authenticate(@RequestBody @Valid AuthenticateDto authenticateDto, HttpServletResponse response) {
    final User user = this.authService.authenticate(authenticateDto)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication failed"));
    final Cookie sessionCookie = this.cookieProvider.createSessionCookie(user)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to issue credentials"));
    response.addCookie(sessionCookie);
    return this.authService.getProfileInfo(user);
  }

  @GetMapping("/session")
  public User getSession(@AuthenticationPrincipal UserDetails currentUser) {
    User user = new User();
    String username = currentUser.getUsername();
    user.setUsername(username);
    return user;
  }

  @DeleteMapping("/logout")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void logout(HttpServletResponse response) {
    final Cookie sessionCookie = new Cookie("session", null);
    sessionCookie.setSecure(true);
    sessionCookie.setHttpOnly(true);
    sessionCookie.setPath("/");
    response.addCookie(sessionCookie);
  }
}
