package edu.gmu.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.gmu.server.entity.Game;
import edu.gmu.server.model.UserStatus;
import edu.gmu.server.service.GameService;
import edu.gmu.server.service.PlayService;
import edu.gmu.server.service.PoolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;


@Slf4j
@RestController
@RequestMapping("/api/games")
public class GameController {
  private final GameService gameService;
  private final PoolService poolService;
  private final PlayService playService;

  @Autowired
  public GameController(GameService gameService, PoolService poolService, PlayService playService) {
    this.gameService = gameService;
    this.poolService = poolService;
    this.playService = playService;
  }

  //  @GetMapping
//  public Page<Game> getAllGames(@RequestParam(defaultValue = "0", required = false) int page,
//                                @RequestParam(defaultValue = "20", required = false) int limit,
//                                @RequestParam(required = false) Map<String, String> filters) {
//    Page<Game> games = this.gameService.getAllGames(page, limit, filters);
//    return games;
//  }
  @GetMapping
  public List<Game> getAllGames() {
    return this.gameService.getAllGames();
  }

  @PostMapping("/join")
  public UserStatus joinGame(@AuthenticationPrincipal UserDetails userDetails) {
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
    }
  }

  @PostMapping("/disconnect")
  public UserStatus disconnect(@AuthenticationPrincipal UserDetails currentUser) {
    try {
      this.playService.handleDisconnection(currentUser);
    } catch (JsonProcessingException e) {
      log.error("{}", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong processing your request");
    }
    return this.poolService.disconnect(currentUser);
  }
}
