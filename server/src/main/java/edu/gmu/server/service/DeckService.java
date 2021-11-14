package edu.gmu.server.service;

import edu.gmu.server.model.Card;
import edu.gmu.server.model.Deck;
import edu.gmu.server.model.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DeckService {
  public void shuffleCards(Deck deck) {
    Collections.shuffle(deck.getCards());
  }

  public void dealCards(Deck deck, List<Player> players) {
    List<Player> playerList = players.stream()
      .map(player -> {
        player.setCards(new ArrayList<>());
        return player;
      })
      .collect(Collectors.toList());
    int i = 0;
    Player currentPlayer = playerList.get(i);
    for (Card card : deck.getCards()) {
      currentPlayer.addCard(card);
      i = i + 1;
      if (i >= playerList.size())
        i = 0;
      currentPlayer = playerList.get(i);
    }
    playerList.stream().forEach(player -> player.setNumberOfRemainingCards(player.getCards().size()));
  }
}