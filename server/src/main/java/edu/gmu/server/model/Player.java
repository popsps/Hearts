package edu.gmu.server.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import edu.gmu.server.exception.HeartsNoSuchCardException;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Player {
  // player information
  private Long id;
  private String username;
  private String nickname;
  // card information
  private LocalDateTime playTime;
  private LocalDateTime turnExpireAt;
  private boolean turn;
  private boolean lastTrickTaken;
  private int numberOfRemainingCards;
  private List<Card> allowedCards = new ArrayList<>();
  private List<Card> cards;
  private List<Card> playedCards;
  private Card inPlayCard;

  // points information
  private int pointsTaken = 0;
  private int pointsTakenOverall = 0;
  private int placement = 0;

  public Player(Long id, String username, String nickname) {
    this.id = id;
    this.username = username;
    this.nickname = nickname;
  }

  public void addCard(Card card) {
    this.cards.add(card);
  }

}
