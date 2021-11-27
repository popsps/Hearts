package edu.gmu.server.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlayerInfo {
  private String username;
  private String nickname;
  private int placement = 0;
  private Integer pointsTaken;
  private Integer pointsTakenOverall;

  public PlayerInfo(Player player) {
    this.setUsername(player.getUsername());
    this.setNickname(player.getNickname());
    this.setPointsTaken(player.getPointsTaken());
    this.setPointsTakenOverall(player.getPointsTakenOverall());
    this.setPlacement(player.getPlacement());
  }
}
