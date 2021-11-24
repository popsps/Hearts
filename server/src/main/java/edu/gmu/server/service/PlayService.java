package edu.gmu.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.gmu.server.dto.GameDto;
import edu.gmu.server.dto.PlayerDto;
import edu.gmu.server.dto.UserDto;
import edu.gmu.server.entity.Game;
import edu.gmu.server.entity.Stats;
import edu.gmu.server.entity.User;
import edu.gmu.server.exception.*;
import edu.gmu.server.model.*;
import edu.gmu.server.repository.GameRepository;
import edu.gmu.server.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PlayService {

  private final Card twoOfClubs = new Card(Suit.CLUBS, Rank.TWO);
  private final Card queenOfSpades = new Card(Suit.SPADES, Rank.QUEEN);
  private final int TIME_OUT = 30;
  private final ConcurrentMap<String, GameManager> gamePool;
  private final ConcurrentMap<String, User> usersJoining;
  private final UtilService utilService;
  private final DeckService deckService;
  private final ObjectMapper objectMapper;
  private final GameRepository gameRepository;
  private final UserRepository userRepository;

  @Autowired
  public PlayService(ConcurrentMap<String, GameManager> gamePool, ConcurrentMap<String, User> usersJoining, UtilService utilService,
                     DeckService deckService, GameRepository gameRepository, UserRepository userRepository) {
    this.gamePool = gamePool;
    this.usersJoining = usersJoining;
    this.utilService = utilService;
    this.deckService = deckService;
    this.gameRepository = gameRepository;
    this.userRepository = userRepository;
    this.objectMapper = new ObjectMapper();
  }

  public GameDto play(UserDetails currentUser, Card card) throws JsonProcessingException {
    String username = currentUser.getUsername();
    GameManager gameManager = this.gamePool.get(username);
    this.assertUserInGame(username, gameManager);
    this.assertUserTurn(username, gameManager);
    this.assertUserOnTime(username, gameManager);
    this.assertCardPlayedAllowed(username, card, gameManager);
    this.updateLastAccessTime(gameManager);
    this.playCard(username, card, gameManager);
    this.resolveGame(username, gameManager);
    if (gameManager.getCardsRemaining() == 0) {
      this.startNewRoundOrFinalize(gameManager);
    }
    return this.getGameDto(username, gameManager);
  }


  private void resolveGame(String username, GameManager gameManager) {
    Player currentPlayer = this.getPlayer(username, gameManager);
    Map<String, Card> board = gameManager.getBoard();
    // if everyone in the game played their card
    if (board.size() == gameManager.getGAME_SIZE()) {
      // calculate points and resolve who get the points
      Suit leadingSuit = gameManager.getLeadingSuit();
      Map.Entry<String, Card> maxPlayerEntry = board.entrySet().stream()
        .filter(entry -> entry.getValue().getSuit().equals(leadingSuit))
        .max((c1, c2) -> c1.getValue().getRank().getValue() - c2.getValue().getRank().getValue())
        .get();
      long heartPoints = board.values().stream().filter(card -> card.getSuit().equals(Suit.HEARTS)).count();
      if (heartPoints > 0)
        gameManager.setHeartBroken(true);
      long queenPoints = board.values().stream().filter(card -> card.equals(this.queenOfSpades)).count() * 13;
      int points = (int) (heartPoints + queenPoints);
      Player maxPlayer = this.getPlayer(maxPlayerEntry.getKey(), gameManager);
      maxPlayer.setPointsTaken(maxPlayer.getPointsTaken() + points);
      maxPlayer.setLastTrickTaken(true);
      gameManager.setLeadingSuit(null);
      this.passTurn(currentPlayer, maxPlayer, gameManager);
      this.clearBoard(gameManager);
    } else {
      this.determineNextPlayer(currentPlayer, gameManager);
    }
  }

  private void determineNextPlayer(Player currentPlayer, GameManager gameManager) {

    int currentPlayerIndex = gameManager.getPlayers().indexOf(currentPlayer);
    int nextPlayerIndex = (currentPlayerIndex + 1 == gameManager.getPlayers().size())
      ? 0 : currentPlayerIndex + 1;
    Player nextPlayer = gameManager.getPlayers().get(nextPlayerIndex);
    this.passTurn(currentPlayer, nextPlayer, gameManager);
  }

  private void passTurn(Player currentPlayer, Player nextPlayer, GameManager gameManager) {
    currentPlayer.setTurn(false);
    currentPlayer.setTurnExpireAt(null);
    this.clearAllowedCards(currentPlayer);
    nextPlayer.setTurn(true);
    this.setAllowedCards(nextPlayer, gameManager);
    LocalDateTime now = this.utilService.getCurrentDateTimeUTC();
    nextPlayer.setTurnExpireAt(now.plusSeconds(this.TIME_OUT));
  }

  private void passTurn(Player player, GameManager gameManager) {
    player.setTurn(true);
    this.setAllowedCards(player, gameManager);
    LocalDateTime now = this.utilService.getCurrentDateTimeUTC();
    player.setTurnExpireAt(now.plusSeconds(this.TIME_OUT));
  }

  private void clearBoard(GameManager gameManager) {
    gameManager.setBoard(new HashMap<>(4));
  }

  private void playCard(String username, Card card, GameManager gameManager) {
    log.info("Player {} plays {}", username, card);
    gameManager.log(String.format("Player %s plays %s", username, card));
    // play the card add necessary bookkeeping
    Player player = getPlayer(username, gameManager);
    player.getCards().remove(card);
    this.clearAllowedCards(player);
    if (gameManager.getBoard().isEmpty())
      gameManager.setLeadingSuit(card.getSuit());
    gameManager.getBoard().put(username, card);
    gameManager.setCardsRemaining(gameManager.getCardsRemaining() - 1);
  }

  public GameDto heartbeat(UserDetails currentUser) throws JsonProcessingException, HeartsGameNotExistException {
    String username = currentUser.getUsername();
    // if user is still in the joining pool
    if (this.usersJoining.containsKey(username)) {
      throw new HeartsGameNotExistException("Your game is not ready.");
    } else {
      GameManager gameManager = this.gamePool.get(username);
      // if user is in game
      if (gameManager != null) {
        this.updateLastAccessTime(gameManager);
        // if game is not started. init game
        if (gameManager.getStatus().equals(Status.NOT_STARTED)) {
          return this.initGame(currentUser);
        } else {
          // if one of player has missed their turn
          this.handleMissedTurns(gameManager);
          // if the round is over
          if (gameManager.getCardsRemaining() == 0) {
            this.startNewRoundOrFinalize(gameManager);
          }
          return this.getGameDto(username, gameManager);
        }
      } else { // if user is not in game and not in a joining pool
        throw new HeartsGameNotExistException("The requested Game does not exist or the user's session is over");
      }
    }
  }

  private void handleMissedTurns(GameManager gameManager) throws JsonProcessingException {
    Optional<Player> activePlayerOptional = gameManager
      .getPlayers().stream().filter(player -> player.isTurn()).findAny();
    if (activePlayerOptional.isPresent()) {
      Player activePlayer = activePlayerOptional.get();
      LocalDateTime now = this.utilService.getCurrentDateTimeUTC();
      if (now.isAfter(activePlayer.getTurnExpireAt())) {
        synchronized (gameManager) {
          gameManager.log(String.format("Player %s got disconnected", activePlayer.getNickname()));
          log.error("Player {} is disconnected. finalizing the game...", activePlayer.getUsername());
          activePlayer.setPointsTakenOverall(126);
          this.resolvePlacement(gameManager);
          this.finalizeGame(gameManager);
        }
      }
    }
  }

  public GameDto initGame(UserDetails currentUser) {
    String username = currentUser.getUsername();
    GameManager gameManager = this.gamePool.get(username);
    log.info("Player {} initializing new game {}", username, gameManager.getId());
    gameManager.log(String.format("Player %s initializing new game %s", username, gameManager.getId()));
    gameManager.setStatus(Status.IN_PROGRESS);
    gameManager.setTimer(this.TIME_OUT);
    // TODO: 11/14/2021 revert deck back to 4 player size
    // Deck newDeck = new Deck();
    Deck newDeck = new Deck(16);
    this.deckService.shuffleCards(newDeck);
    this.deckService.dealCards(newDeck, gameManager.getPlayers());
    gameManager.setDeck(newDeck);
    gameManager.setCardsRemaining(gameManager.getDECK_SIZE());
    this.initDetermineTurn(gameManager);
    return this.getGameDto(username, gameManager);
  }

  private void initDetermineTurn(GameManager gameManager) {
    gameManager.getPlayers().stream().forEach(player -> {
      boolean turn = false;
      turn = player.getCards().contains(new Card(Suit.CLUBS, Rank.TWO));
      player.setTurn(turn);
      if (player.isTurn()) {
        // bookkeeping for time, allowed cards
        this.passTurn(player, gameManager);
      } else {
        this.clearTurn(player);
      }
    });
  }

  private void clearAllowedCards(Player player) {
    player.getAllowedCards().clear();
  }

  private void clearTurn(Player player) {
    this.clearAllowedCards(player);
    player.setTurnExpireAt(null);
  }

  private void setAllowedCards(Player player, GameManager gameManager) {
    List<Card> cards = player.getCards();
    List<Card> allowedCards = new ArrayList<>();
    Suit leadingSuit = gameManager.getLeadingSuit();
    // if you are not leading
    if (leadingSuit != null) {
      allowedCards = player.getCards().stream()
        .filter(card -> card.getSuit().equals(leadingSuit)).collect(Collectors.toList());
      // if you don't have a card in the same suit
      if (allowedCards.isEmpty()) {
        allowedCards = cards.stream().collect(Collectors.toList());
      }
    } else if (cards.contains(this.twoOfClubs)) {
      allowedCards.add(this.twoOfClubs);
    } else { // if you are leading
      if (gameManager.isHeartBroken()) {
        allowedCards = cards.stream().collect(Collectors.toList());
      } else {
        allowedCards = cards.stream()
          .filter(card -> !card.getSuit().equals(Suit.HEARTS))
          .collect(Collectors.toList());
        // case Hearts is not broken and there is no other card to play except Hearts
        if (allowedCards.isEmpty())
          allowedCards = cards.stream().collect(Collectors.toList());
      }
    }
    player.setAllowedCards(allowedCards);
  }

  public void addPlayer(Player player, GameManager gameManager) throws HeartsGameIsFullException {
    if (gameManager.getPlayers() == null)
      gameManager.setPlayers(new ArrayList<>(4));
    if (gameManager.getPlayers().size() < 4)
      gameManager.getPlayers().add(player);
    else
      throw new HeartsGameIsFullException("Game is already full with 4 people");
  }

  public GameDto getGameDto(@NotNull String username, GameManager gameManager) {
    GameDto gameDto = new GameDto();
    gameDto.setId(gameManager.getId());
    gameDto.setStatus(gameManager.getStatus());
    gameDto.setSessionCreated(gameManager.getSessionCreated());
    gameDto.setSessionEnded(gameManager.getSessionEnded());
    Player player = this.getPlayer(username, gameManager);
    gameDto.setYou(player);
    List<PlayerDto> opponents = gameManager.getPlayers().stream()
      .filter(pl -> !pl.getUsername().equals(username))
      .map(pl -> new PlayerDto(pl)).collect(Collectors.toList());
    gameDto.setOpponents(opponents);
    gameDto.setHeartBroken(gameManager.isHeartBroken());
    gameDto.setLeadingSuit(gameManager.getLeadingSuit());
    gameDto.setCardsRemaining(gameManager.getCardsRemaining());
    gameDto.setBoard(gameManager.getBoard());
    gameDto.setTimer(gameManager.getTimer());
    return gameDto;
  }

  private boolean isGameOver(GameManager gameManager) {
    Player looser = gameManager.getPlayers().stream()
      .max((p1, p2) -> p1.getPointsTakenOverall() - p2.getPointsTakenOverall())
      .orElseThrow(() -> new HeartsPlayerNotInGameException("Cannot find a player"));
    if (looser.getPointsTakenOverall() >= gameManager.getMAX_SCORE())
      return true;
    else
      return false;
  }

  private void startNewRoundOrFinalize(GameManager gameManager) throws JsonProcessingException {
    synchronized (gameManager) {
      if (gameManager.getCardsRemaining() == 0) {
        log.info("New round is loaded");
        gameManager.getPlayers().forEach(player -> {
          int points = player.getPointsTaken();
          int pointsOverall = player.getPointsTakenOverall();
          player.setPointsTakenOverall(pointsOverall + points);
          player.setPointsTaken(0);
          this.resolvePlacement(gameManager);
        });
        if (this.isGameOver(gameManager)) {
          this.finalizeGame(gameManager);
        } else {
          gameManager.log(String.format("Starting a new round"));
          gameManager.setStatus(Status.IN_PROGRESS);
          this.deckService.shuffleCards(gameManager.getDeck());
          this.deckService.dealCards(gameManager.getDeck(), gameManager.getPlayers());
          gameManager.setCardsRemaining(gameManager.getDECK_SIZE());
          this.initDetermineTurn(gameManager);
        }
      }
    }
  }

  private synchronized void finalizeGame(GameManager gameManager) throws JsonProcessingException {
    if (this.gamePool.containsValue(gameManager) && this.isGameOver(gameManager)
      && !gameManager.getStatus().equals(Status.ENDED)) {
      // save to database
      this.saveGameToDatabase(gameManager);
      LocalDateTime now = this.utilService.getCurrentDateTimeUTC();
      gameManager.setSessionEnded(now);
      gameManager.setStatus(Status.ENDED);
    }
  }

  private Player getPlayer(String username, @NotNull GameManager gameManager) {
    return gameManager.getPlayers().stream()
      .filter(p -> p.getUsername().equals(username))
      .findFirst()
      .orElseThrow(() -> {
        log.error("Player {} does not belong to the game {}", username, gameManager.getId());
        return new HeartsPlayerNotInGameException("Player not found");
      });
  }


  private void assertCardPlayedAllowed(String username, Card card, GameManager gameManager) {
    Player player = this.getPlayer(username, gameManager);
    List<Card> allowedCards = player.getAllowedCards();
    List<Card> cards = player.getCards();
    try {
      if (!allowedCards.contains(card) || !cards.contains(card)) {
        throw new HeartsCardNotAllowedException("Card is not allowed");
      }
    } catch (NullPointerException e) {
      log.error("Player {} attempted to play the invalid card {}", username, card);
      throw new HeartsCardNotAllowedException("Card is not allowed");
    }
  }

  private void assertUserOnTime(String username, GameManager gameManager) {
    Player player = this.getPlayer(username, gameManager);
    LocalDateTime now = this.utilService.getCurrentDateTimeUTC();
    player.setPlayTime(now);
    if (now.isAfter(player.getTurnExpireAt())) {
      log.error("Player {} attempted to play a card while timed out", username);
      throw new HeartsTimeoutException("");
    }
  }

  private void assertUserTurn(String username, GameManager gameManager) {
    Player player = this.getPlayer(username, gameManager);
    if (!player.isTurn()) {
      log.error("Player {} attempted to play a card while it wasn't their turn", username);
      throw new HeartsInvalidTurnException("");
    }
  }

  private void assertUserInGame(String username, GameManager gameManager) {
    // throw exception in getPlayer
    try {
      this.getPlayer(username, gameManager);
    } catch (NullPointerException e) {
      log.error("No players in game detected");
      throw new HeartsPlayerNotInGameException("Player not found");
    }
  }

  private void resolvePlacement(GameManager gameManager) {
    List<Player> playerSorted = gameManager.getPlayers().stream()
      .sorted((p1, p2) -> p1.getPointsTakenOverall() - p2.getPointsTakenOverall())
      .collect(Collectors.toList());
    for (int i = 0; i < playerSorted.size(); i++) {
      final int index = i;
      Player player = gameManager.getPlayers().stream()
        .filter(p -> p.getId().equals(playerSorted.get(index).getId()))
        .findFirst()
        .orElseThrow(() -> new HeartsPlayerNotInGameException("Player not found"));
      player.setPlacement(i + 1);
    }
  }

  @Transactional
  protected void saveGameToDatabase(GameManager gameManager) throws JsonProcessingException {
    log.info("Saving game {}...", gameManager.getId());
    // update stats
    gameManager.getPlayers().stream().forEach(player -> {
      User user = this.userRepository.findByUsername(player.getUsername())
        .orElseThrow(() -> new HeartsUserNotFoundException("Cannot find user in the database"));
      Stats stats = user.getStats();
      int win = (player.getPlacement() == 1) ? 1 : 0;
      int lost = (player.getPlacement() > 1) ? 1 : 0;
      if (win == 1)
        gameManager.log(String.format("Player %s won the game", player.getNickname()));
      if (lost == 1)
        gameManager.log(String.format("Player %s lost the game", player.getNickname()));
      stats.setWin(stats.getWin() + win);
      stats.setLost(stats.getLost() + lost);
      int numOfGames = stats.getWin() + stats.getLost();
      double averagePlacement =
        (player.getPlacement() + stats.getAveragePlacement()) / numOfGames;
      double pointsTakenPerGame =
        (player.getPointsTakenOverall() + stats.getPointsTakenPerGame() * (numOfGames - 1)) / numOfGames;
      stats.setAveragePlacement(averagePlacement);
      stats.setPointsTakenPerGame(pointsTakenPerGame);
      user.setStats(stats);
      this.userRepository.save(user);
    });
    // save game info
    Game game = new Game();
    String jsonLogs = objectMapper.writeValueAsString(gameManager.getLogs());
    game.setSessionCreated(gameManager.getSessionCreated());
    game.setLogs(jsonLogs);
    Set<UserDto> users = gameManager.getPlayers().stream()
      .map(player -> new UserDto(player.getId(), player.getUsername(), player.getNickname()))
      .collect(Collectors.toSet());
    game.setUsers(users);
    game.setStatus(Status.ENDED);
    LocalDateTime now = this.utilService.getCurrentDateTimeUTC();
    game.setSessionEnded(now);
    this.gameRepository.save(game);

  }

  private void updateLastAccessTime(GameManager gameManager) {
    LocalDateTime now = this.utilService.getCurrentDateTimeUTC();
    gameManager.setLastAccessTime(now);
  }

  @Scheduled(initialDelay = 2, fixedRate = 2, timeUnit = TimeUnit.MINUTES)
  public void inactiveGamesRemover() {
    log.warn("Running inactive game removal task...");
    log.warn("Removing Games after 5 minutes of inactivity...");
    LocalDateTime now = this.utilService.getCurrentDateTimeUTC();
    synchronized (this.gamePool) {
      for (Map.Entry<String, GameManager> entry : this.gamePool.entrySet()) {
        GameManager game = entry.getValue();
        // remove finished games after 5 minutes of inactivity
        if (game.getStatus().equals(Status.ENDED) &&
          now.isAfter(game.getSessionEnded().plusMinutes(5))) {
          this.gamePool.remove(entry.getKey());
          log.info("The finished Game {} is removed after 5 minutes of inactivity", game.getId());
        }
        // remove hanging games after 5 minutes of inactivity
        if (game.getStatus().equals(Status.IN_PROGRESS) &&
          now.isAfter(game.getLastAccessTime().plusMinutes(5))) {
          this.gamePool.remove(entry.getKey());
          log.info("The hanging Game {} is removed after 5 minutes of inactivity", game.getId());
        }
      }
    }
  }
}
