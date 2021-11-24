package edu.gmu.server.service;

import edu.gmu.server.model.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


@Data
@NoArgsConstructor
public class GameManager {
  // TODO: 11/14/2021 revert numbers back to 4 player game
  private final int GAME_SIZE = 2;
  // private final int DECK_SIZE = 52;
  private final int DECK_SIZE = 16;
//  private final int MAX_SCORE = 100;
  private final int MAX_SCORE = 10;
  private Long id;
  private Status status;
  private List<String> logs = new ArrayList<>();
  private LocalDateTime sessionCreated;
  private LocalDateTime sessionEnded;
  private LocalDateTime lastAccessTime;
  private int timer;
  private AtomicInteger countDawn = new AtomicInteger(GAME_SIZE);
  //  ===================================================================
  private int cardsRemaining = DECK_SIZE;
  private int score = 0;
  private boolean heartBroken;
  private Deck deck;
  private Suit leadingSuit;
  private List<Player> players = new ArrayList<>(4);
  // username, card
  private Map<String, Card> board = new HashMap<>(4);

  public GameManager(Long id, Status status) {
    this.setId(id);
    this.setStatus(status);
  }

  public void log(String action) {
    this.getLogs().add(action);
  }
}
