package edu.gmu.server.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import edu.gmu.server.model.Card;
import edu.gmu.server.model.Player;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlayerDto {
  private String username;
  private String nickname;
  private Integer numberOfRemainingCards;
  private boolean lastTrickTaken;
  private boolean turn;
  private LocalDateTime turnExpireAt;
  // points information
  private int placement = 0;
  private Integer pointsTaken;
  private Integer pointsTakenOverall;


  public PlayerDto(Player player) {
    this.setUsername(player.getUsername());
    this.setNickname(player.getNickname());
    this.setPointsTaken(player.getPointsTaken());
    this.setPointsTakenOverall(player.getPointsTakenOverall());
    this.setNumberOfRemainingCards(player.getNumberOfRemainingCards());
    this.setLastTrickTaken(player.isLastTrickTaken());
    this.setTurn(player.isTurn());
    this.setPlacement(player.getPlacement());
    this.setTurnExpireAt(player.getTurnExpireAt());
  }
}
