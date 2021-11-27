package edu.gmu.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.gmu.server.dto.GameDto;
import edu.gmu.server.exception.*;
import edu.gmu.server.model.Card;
import edu.gmu.server.model.UserStatus;
import edu.gmu.server.service.PlayService;
import edu.gmu.server.service.PoolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/play")
public class PlayController {
  private final PlayService playService;
  private final PoolService poolService;

  @Autowired
  public PlayController(PlayService playService, PoolService poolService) {
    this.playService = playService;
    this.poolService = poolService;
  }

  @GetMapping("/status")
  public UserStatus getUserStatus(@AuthenticationPrincipal UserDetails currentUser) {
    return this.poolService.getUserStatus(currentUser);
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
                      @RequestBody @Valid Card card) {
    try {
      return this.playService.play(userDetails, card);
    } catch (HeartsGameIsFullException | HeartsPlayerNotInGameException e) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to perform such action");
    } catch (HeartsCardNotAllowedException | HeartsTimeoutException |
      HeartsInvalidTurnException | JsonProcessingException e) {
      log.error(e.toString());
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are forbidden to make such a move");
    }
  }

  // TODO: 11/23/2021 implement pass the trash
  @PostMapping("/pass-trash")
  public GameDto passTheTrash(@AuthenticationPrincipal UserDetails userDetails,
                              @RequestBody @Valid List<Card> cards) {
    return null;
  }
}
