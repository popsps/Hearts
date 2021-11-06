package edu.gmu.server.controller;

import edu.gmu.server.dto.AuthenticateDto;
import edu.gmu.server.dto.RegisterDto;
import edu.gmu.server.entity.User;
import edu.gmu.server.exception.HeartsBadCredentialsException;
import edu.gmu.server.exception.HeartsResourceNotFoundException;
import edu.gmu.server.security.CookieProvider;
import edu.gmu.server.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

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
  public User register(@RequestBody RegisterDto registerDto) {
    return this.authService.register(registerDto)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "user is not available"));
  }

  @PostMapping("/authenticate")
  @ResponseStatus(HttpStatus.CREATED)
  public User authenticate(@RequestBody AuthenticateDto authenticateDto, HttpServletResponse response) {
    final User user = this.authService.authenticate(authenticateDto)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication failed"));
    final Cookie sessionCookie = this.cookieProvider.createSessionCookie(user)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to issue credentials"));
    response.addCookie(sessionCookie);
    return this.authService.getProfileInfo(user);
  }




}
