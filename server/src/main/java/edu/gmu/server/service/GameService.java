package edu.gmu.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.gmu.server.dto.GameDto;
import edu.gmu.server.exception.HeartsPlayerInGameException;
import edu.gmu.server.exception.HeartsResourceNotFoundException;
import edu.gmu.server.model.*;
import edu.gmu.server.entity.Game;
import edu.gmu.server.entity.User;
import edu.gmu.server.repository.GameRepository;
import edu.gmu.server.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

@Slf4j
@Service
public class GameService {

  private Long gameId = 0L;
  private final int GAME_MAX_SIZE = 2;
  private final int TIME_OUT = 5;

  private final GameRepository gameRepository;
  private final UserRepository userRepository;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final UtilService utilService;
  private final PlayService playService;
  private final ConcurrentMap<String, GameManager> gamePool;
  private final ConcurrentMap<String, User> usersJoining;


  @Autowired
  public GameService(GameRepository gameRepository, UserRepository userRepository, PlayService playService,
                     ApplicationEventPublisher applicationEventPublisher, UtilService utilService,
                     ConcurrentMap<String, GameManager> gamePool, ConcurrentMap<String, User> usersJoining) {
    this.gameRepository = gameRepository;
    this.userRepository = userRepository;
    this.applicationEventPublisher = applicationEventPublisher;
    this.utilService = utilService;
    this.playService = playService;
    this.gamePool = gamePool;
    this.usersJoining = usersJoining;
  }


  @Transactional
  public Page<Game> getAllGames(int page, int limit, Map<String, String> filters) {
    Pageable pageable = PageRequest.of(page, limit);
    return this.gameRepository.findAll(pageable);
  }

  public Optional<GameDto> getJoinStatus(UserDetails principal)
    throws HeartsPlayerInGameException {
    log.info("Player {} attempts to join a new game", principal.getUsername());
    if (this.usersJoining.containsKey(principal.getUsername())) {
      GameManager gameManager = this.gamePool.get(principal.getUsername());
      GameDto gameDto = this.playService.getGameDto(principal.getUsername(), gameManager);
      return Optional.of(gameDto);
    } else {
      return Optional.empty();
    }
  }

  /**
   * A player can join a game.
   *
   * @param principal
   * @return
   * @throws JsonProcessingException
   */
  @Transactional
  public boolean joinNewGame(UserDetails principal)
    throws JsonProcessingException, HeartsPlayerInGameException,
    InterruptedException, ExecutionException, TimeoutException {
    log.info("Player {} attempts to join a new game", principal.getUsername());
    Optional<User> user = this.userRepository.findByUsername(principal.getUsername());
    User _user = user.orElseThrow(() -> new HeartsResourceNotFoundException("User not found"));
    if (this.gamePool.containsKey(_user.getUsername()))
      throw new HeartsPlayerInGameException(String.format("Player %s is already in a game", _user.getUsername()));
    else {
      this.usersJoining.putIfAbsent(_user.getUsername(), _user);
      log.info("Player {} is added to the pool, looking for a new game to be ready", _user.getUsername());
      CompletableFuture.runAsync(() -> this.applicationEventPublisher.publishEvent(_user.getUsername()));
    }
    if (this.usersJoining.containsKey(_user.getUsername()))
      return true;
    else
      return false;

  }

  @EventListener
  public void handleRegistrationGameEvent(String username) {
    log.info("handle registration for {}. Number of player waiting for a game is {}", username, this.usersJoining.size());
    synchronized (this.usersJoining) {
      if (this.usersJoining.size() >= this.GAME_MAX_SIZE) {
        GameManager newGameManager = new GameManager(gameId++, Status.NOT_STARTED);
        LocalDateTime now = this.utilService.getCurrentDateTimeUTC();
        newGameManager.setSessionCreated(now);
        log.info("Game room {} is created. Handling players registrations...", newGameManager.getId());
        this.usersJoining.entrySet().stream()
          .limit(this.GAME_MAX_SIZE)
          .forEach(userEntry -> {
            String _username = userEntry.getKey();
            User user = userEntry.getValue();
            Player newPlayer = new Player(user.getId(), user.getUsername(), user.getNickname());
            this.gamePool.putIfAbsent(_username, newGameManager);
            this.playService.addPlayer(newPlayer, newGameManager);
            // remove user from joining pool
            this.usersJoining.remove(_username);
            log.info("User {} registration to Game {} was successful", _username, newGameManager.getId());
            newGameManager.log(
              String.format("User %s registration to the Game %s was successful",
                _username, newGameManager.getId()));
          });
      }
    }
  }

  public boolean removeUserFromJoiningPool(UserDetails currentUser) {
    String username = currentUser.getUsername();
    User removedUser = this.usersJoining.remove(username);
    if (removedUser != null) {
      log.info("User {} has been removed from the joining pool", username);
      return true;
    } else
      return false;
  }

  private boolean removeUserFromGamePool(UserDetails currentUser) {
    String username = currentUser.getUsername();
    GameManager removedUserGame = this.gamePool.remove(username);
    if (removedUserGame != null) {
      log.info("User {} has been removed from the joining pool", username);
      return true;
    } else
      return false;
  }

  /**
   * Remove the user from joining pool. If user is looking for a new game they will be removed
   * from the list of the player looking for a new game.
   * Remove the user from a game. If user is in a game they will be removed and disconnected from it.
   *
   * @param currentUser
   */
  public boolean disconnect(UserDetails currentUser) {
    boolean gameDisconnected = this.removeUserFromJoiningPool(currentUser);
    boolean joinersDisconnected = this.removeUserFromGamePool(currentUser);
    if (gameDisconnected || joinersDisconnected)
      return true;
    else
      return false;
  }
}
