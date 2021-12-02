package edu.gmu.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card {
  @NotNull
  private Suit suit;
  @NotNull
  private Rank rank;

  @Override
  public String toString() {
    return this.getRank() + " of " + this.getSuit();
  }

}
