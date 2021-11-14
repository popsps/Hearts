package edu.gmu.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.gmu.server.dto.GameDto;
import edu.gmu.server.entity.Game;
import edu.gmu.server.exception.*;
import edu.gmu.server.model.Card;
import edu.gmu.server.service.GameService;
import edu.gmu.server.service.PlayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;


@Slf4j
@RestController
@RequestMapping("/api/games")
public class GameController {
  private final GameService gameService;
  private final PlayService playService;

  @Autowired
  public GameController(GameService gameService, PlayService playService) {
    this.gameService = gameService;
    this.playService = playService;
  }

  @GetMapping
  public Page<Game> getAllGames(@RequestParam(defaultValue = "0", required = false) int page,
                                @RequestParam(defaultValue = "20", required = false) int limit,
                                @RequestParam(required = false) Map<String, String> filters) {
    Page<Game> games = this.gameService.getAllGames(page, limit, filters);
    return games;
  }

  @PostMapping("/join")
  public boolean joinGame(@AuthenticationPrincipal UserDetails userDetails) {
    try {
      return this.gameService.joinNewGame(userDetails);
    } catch (JsonProcessingException e) {
      log.error("Something went wrong {}", e.getMessage());
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
        "Something went wrong processing your request.");
    } catch (InterruptedException | ExecutionException e) {
      log.error("Something went wrong {}", e.getMessage());
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
        "Something went wrong processing your request.");
    } catch (TimeoutException e) {
      log.error("Could not find a match for {}", userDetails.getUsername());
      log.error("Timeout Exception {}: {}", e.getStackTrace(), e.getMessage());
      this.gameService.removeUserFromJoiningPool(userDetails);
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,
        "We Could not find a match for you");
    } catch (HeartsPlayerInGameException e) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN,
        "The player is already in another game");
    }
  }

  @GetMapping("/join/me")
  public GameDto getJoinStatus(@AuthenticationPrincipal UserDetails userDetails) {
    return this.gameService.getJoinStatus(userDetails)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No Game is ready"));
  }

  @GetMapping("/current/play/heartbeat")
  public GameDto heartbeat(@AuthenticationPrincipal UserDetails currentUser) {
    try {
      return this.playService.heartbeat(currentUser);
    } catch (JsonProcessingException | HeartsGameNotExistException e) {
      log.error(e.toString());
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong");
    }
  }

  @PostMapping("/current/play/card")
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
