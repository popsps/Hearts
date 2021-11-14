package edu.gmu.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card {
  private Suit suit;
  private Rank rank;

  @Override
  public String toString() {
    return this.getRank() + " of " + this.getSuit();
  }
}
