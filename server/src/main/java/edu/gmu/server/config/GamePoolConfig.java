package edu.gmu.server.config;

import edu.gmu.server.entity.User;
import edu.gmu.server.service.GameManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Configuration
@EnableScheduling
public class GamePoolConfig {

  // Map of <username, GameId> of users in currently in Game
  private final ConcurrentMap<String, GameManager> gamePool = new ConcurrentHashMap<>();
  // Pool of users looking for a new game
  private final ConcurrentMap<String, User> usersJoining = new ConcurrentHashMap<>();

  @Bean
  ConcurrentMap<String, GameManager> getGamePool() {
    return this.gamePool;
  }

  @Bean
  ConcurrentMap<String, User> getUsersJoining() {
    return this.usersJoining;
  }
}
