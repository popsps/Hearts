package edu.gmu.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStatus {
  private boolean inJoiningPool = false;
  private boolean inGame = false;
}
