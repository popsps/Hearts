package edu.gmu.server.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import edu.gmu.server.model.Card;
import edu.gmu.server.model.Player;
import edu.gmu.server.model.Status;
import edu.gmu.server.model.Suit;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GameDto {
  private Long id;
  private Suit leadingSuit;
  private boolean heartBroken;
  private int cardsRemaining;
  private LocalDateTime sessionEnded;
  private List<PlayerDto> opponents;
  private Map<String, Card> board;
  private Player you;
  private Status status;
  private LocalDateTime sessionCreated;

  public GameDto(Long id, Status status) {
    this.setId(id);
    this.setStatus(status);
  }
}