package edu.gmu.server.service;

import edu.gmu.server.dto.AuthenticateDto;
import edu.gmu.server.dto.RegisterDto;
import edu.gmu.server.entity.Stats;
import edu.gmu.server.entity.User;
import edu.gmu.server.exception.HeartsBadCredentialsException;
import edu.gmu.server.exception.HeartsResourceNotFoundException;
import edu.gmu.server.repository.StatsRepository;
import edu.gmu.server.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.sql.rowset.serial.SerialBlob;
import javax.transaction.Transactional;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class AuthService {
  private final UserRepository userRepository;
  private final StatsRepository statsRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;

  @Autowired
  public AuthService(UserRepository userRepository, StatsRepository statsRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
    this.userRepository = userRepository;
    this.statsRepository = statsRepository;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
  }

  @Transactional
  public Page<User> getAllUsers(int page, int limit, Map<String, String> filters) {
    Pageable pageable = PageRequest.of(page, limit);
    return this.userRepository.findAll(pageable);
  }

  @Transactional
  public Optional<User> register(RegisterDto registerDto) {
    try {
      User newUserInfo = generateNewUser(registerDto);
      if (this.userRepository.findByUsername(newUserInfo.getUsername()).isEmpty()) {
        User newUser = this.userRepository.save(newUserInfo);
        Stats newStats = new Stats();
        newStats.setUser(newUser);
        this.statsRepository.save(newStats);
        return Optional.of(newUser);
      } else {
        return Optional.empty();
      }
    } catch (DataIntegrityViolationException e) {
      return Optional.empty();
    }
  }

  private User generateNewUser(RegisterDto registerDto) {
    User newUser = new User();
    newUser.setUsername(registerDto.getUsername());
    newUser.setFirstName(registerDto.getFirstName());
    newUser.setLastName(registerDto.getLastName());
    newUser.setNickname(registerDto.getNickname());
    newUser.setEmail(registerDto.getEmail());
    newUser.setPassword(this.passwordEncoder.encode(registerDto.getPassword()));
    newUser.setRole("PLAYER");
    String now = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME);
    newUser.setCreateTime(now);
    newUser.setLastAccessed(now);
    return newUser;
  }

  @Transactional
  public void uploadProfilePicture(UserDetails principal, MultipartFile picture) throws IOException, SQLException {
    if (principal != null) {
      log.info("principal {} attempts to upload a profile picture", principal.getUsername());
      User currentUser = this.userRepository.findByUsername(principal.getUsername())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, ""));
      log.info("Picture info: {}, {}, {}, {}", picture.getName(), picture.getSize(),
        picture.getBytes().length, picture.getOriginalFilename());
      Blob picBlob = new SerialBlob(picture.getBytes());
      currentUser.setProfilePicture(picBlob);
    } else {
      log.info("Attempt access to profile picture with bad credentials");
      throw new HeartsBadCredentialsException("Bad credentials provided");
    }
  }

  public byte[] getProfilePicture(UserDetails principal)
    throws HeartsBadCredentialsException, HeartsResourceNotFoundException {
    try {
      if (principal != null) {
        log.info("principal {} attempts to get profile picture", principal.getUsername());
        User currentUser = this.userRepository.findByUsername(principal.getUsername())
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Credentials"));
        int len = (int) currentUser.getProfilePicture().length();
        return currentUser.getProfilePicture().getBytes(1, len);
      } else {
        throw new BadCredentialsException("Bad Credentials provided");
      }
    } catch (BadCredentialsException e) {
      log.info("Attempt access to profile picture with bad credentials");
      throw new HeartsBadCredentialsException("Bad Credentials provided");
    } catch (NullPointerException e) {
      throw new HeartsResourceNotFoundException("A profile picture not found");
    } catch (Exception e) {
      throw new HeartsResourceNotFoundException("Operation failed");
    }
  }

  public Optional<User> authenticate(AuthenticateDto authenticateDto) {
    return this.loadAuthenticatedUser(authenticateDto.getUsername(), authenticateDto.getPassword());
  }

  private Optional<User> loadAuthenticatedUser(final String id, final String password) {
    String username = null;
    try {
      Authentication authentication = this.authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(id, password));
      if (authentication.isAuthenticated()) {
        org.springframework.security.core.userdetails.User user =
          (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        log.info("{} has successfully logged in", user.getUsername());
        return this.userRepository.findByUsername(user.getUsername());
      } else {
        return Optional.empty();
      }
    } catch (AuthenticationException e) {
      log.error("Authentication failed for user {}", id);
      return Optional.empty();
    } catch (Exception e) {
      log.error("Something while Authenticating...");
      return Optional.empty();
    }
  }

  public User getProfileInfo(User user) {
    User userInfo = new User();
    userInfo.setUsername(user.getUsername());
    userInfo.setFirstName(user.getFirstName());
    userInfo.setLastName(user.getLastName());
    userInfo.setNickname(user.getNickname());
    userInfo.setEmail(user.getEmail());
    return userInfo;
  }
}
