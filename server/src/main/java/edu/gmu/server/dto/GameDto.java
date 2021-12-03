package edu.gmu.server.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import edu.gmu.server.model.*;
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
  private boolean passTheTrash;
  private Suit leadingSuit;
  private boolean heartBroken;
  private int cardsRemaining;
  private int timer;
  private List<PlayerDto> opponents;
  private Map<String, Card> board;
  private Player you;
  private List<PlayerInfo> resultTable;
  private Status status;
  private LocalDateTime sessionCreated;
  private LocalDateTime sessionEnded;
  private boolean APlayerLeftTheGame = false;

  public GameDto(Long id, Status status) {
    this.setId(id);
    this.setStatus(status);
  }
}
