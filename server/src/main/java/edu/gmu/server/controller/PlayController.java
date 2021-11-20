package edu.gmu.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.gmu.server.dto.GameDto;
import edu.gmu.server.exception.*;
import edu.gmu.server.model.Card;
import edu.gmu.server.service.PlayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequestMapping("/api/play")
public class PlayController {
  private final PlayService playService;

  @Autowired
  public PlayController(PlayService playService) {
    this.playService = playService;
  }

  @GetMapping("/heartbeat")
  public GameDto heartbeat(@AuthenticationPrincipal UserDetails currentUser) {
    try {
      return this.playService.heartbeat(currentUser);
    } catch (HeartsGameNotExistException e) {
      log.error(e.toString());
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "You are not in a game");
    } catch (JsonProcessingException e) {
      log.error(e.toString());
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong");
    }
  }

  @PostMapping("/card")
  public GameDto play(@AuthenticationPrincipal UserDetails userDetails,
                      @RequestBody Card card) {
    try {
      return this.playService.play(userDetails, card);
    } catch (HeartsGameIsFullException | HeartsPlayerNotInGameException |
      HeartsCardNotAllowedException | HeartsTimeoutException |
      HeartsInvalidTurnException | JsonProcessingException e) {
      log.error(e.toString());
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Request");
    }
  }
}
