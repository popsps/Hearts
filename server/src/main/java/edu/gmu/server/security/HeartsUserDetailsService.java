package edu.gmu.server.security;

import edu.gmu.server.entity.User;
import edu.gmu.server.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.springframework.security.core.userdetails.User.withUsername;

@Slf4j
@Service
public class HeartsUserDetailsService implements UserDetailsService {
  private final UserRepository userRepository;
  private final JwtProvider jwtProvider;

  public HeartsUserDetailsService(UserRepository userRepository, JwtProvider jwtProvider) {
    this.userRepository = userRepository;
    this.jwtProvider = jwtProvider;
  }

  @Override
  public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
    User user = this.userRepository.findByUsernameOrEmail(id).orElseThrow(() ->
      new UsernameNotFoundException(String.format("Cannot validate user %s", id)));
    return
      withUsername(user.getUsername())
        .password(user.getPassword())
        .authorities(user.getRole())
        .accountExpired(false)
        .accountLocked(false)
        .credentialsExpired(false)
        .disabled(false)
        .build();
  }

  public Optional<UserDetails> loadUserByJwtToken(String jwtToken) {
    String username = this.jwtProvider.getUsername(jwtToken);
    String role = this.jwtProvider.getRole(jwtToken);
    return Optional.of(
      withUsername(username)
        .password("")
        .authorities(role)
        .accountExpired(false)
        .accountLocked(false)
        .credentialsExpired(false)
        .disabled(false)
        .build());
  }
}
