package edu.gmu.server.service;

import edu.gmu.server.entity.User;
import edu.gmu.server.model.UserStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentMap;

@Slf4j
@Service
public class PoolService {
  // username, game
  private final ConcurrentMap<String, GameManager> gamePool;
  // username, user
  private final ConcurrentMap<String, User> usersJoining;

  @Autowired
  public PoolService(ConcurrentMap<String, GameManager> gamePool, ConcurrentMap<String, User> usersJoining) {
    this.gamePool = gamePool;
    this.usersJoining = usersJoining;
  }

  private boolean removeUserFromJoiningPool(String username) {
    User removedUser = this.usersJoining.remove(username);
    if (removedUser != null) {
      log.info("User {} has been removed from the joining pool", username);
      return true;
    } else
      return false;
  }

  private boolean removeUserFromGamePool(String username) {
    GameManager removedUserGame = this.gamePool.remove(username);
    if (removedUserGame != null) {
      log.info("User {} has been removed from the joining pool", username);
      return true;
    } else
      return false;
  }

  public UserStatus getUserStatus(UserDetails currentUser) {
    String username = currentUser.getUsername();
    UserStatus userStatus = new UserStatus();
    if (this.usersJoining.containsKey(username))
      userStatus.setInJoiningPool(true);
    if (this.gamePool.containsKey(username))
      userStatus.setInGame(true);
    return userStatus;
  }

  public UserStatus getUserStatus(String username) {
    UserStatus userStatus = new UserStatus();
    if (this.usersJoining.containsKey(username))
      userStatus.setInJoiningPool(true);
    if (this.gamePool.containsKey(username))
      userStatus.setInGame(true);
    return userStatus;
  }

  /**
   * Remove the user from joining pool. If user is looking for a new game they will be removed
   * from the list of the player looking for a new game.
   * Remove the user from a game. If user is in a game they will be removed and disconnected from it.
   *
   * @param username
   */
  public UserStatus disconnect(String username) {
    boolean gameDisconnected = this.removeUserFromJoiningPool(username);
    boolean joinersDisconnected = this.removeUserFromGamePool(username);
    return this.getUserStatus(username);
  }

  public UserStatus disconnect(UserDetails currentUser) {
    String username = currentUser.getUsername();
    boolean gameDisconnected = this.removeUserFromJoiningPool(username);
    boolean joinersDisconnected = this.removeUserFromGamePool(username);
    return this.getUserStatus(username);
  }
}
