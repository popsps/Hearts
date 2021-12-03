package edu.gmu.server.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class Deck {
  private final int size = 52;
  private List<Card> cards;

  public Deck() {
    cards = new ArrayList<>(52);
    for (Suit suit : Suit.values()) {
      for (Rank rank : Rank.values()) {
        cards.add(new Card(suit, rank));
      }
    }
  }

  public Deck(int size) {
    cards = new ArrayList<>(16);
    for (Suit suit : Suit.values()) {
      for (Rank rank : Arrays.stream(Rank.values()).skip(9).collect(Collectors.toList())) {
        cards.add(new Card(suit, rank));
      }
    }
    Card ct = cards.stream().filter(card -> card.equals(new Card(Suit.CLUBS, Rank.JACK))).findFirst().get();
    ct.setRank(Rank.TWO);
  }
}
