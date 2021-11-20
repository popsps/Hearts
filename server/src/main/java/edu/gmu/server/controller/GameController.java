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

  @Autowired
  public GameController(GameService gameService) {
    this.gameService = gameService;
  }

  @GetMapping
  public Page<Game> getAllGames(@RequestParam(defaultValue = "0", required = false) int page,
                                @RequestParam(defaultValue = "20", required = false) int limit,
                                @RequestParam(required = false) Map<String, String> filters) {
    Page<Game> games = this.gameService.getAllGames(page, limit, filters);
    return games;
  }

  @PostMapping("/join")
  public String joinGame(@AuthenticationPrincipal UserDetails userDetails) {
    try {
      if (this.gameService.joinNewGame(userDetails))
        return "You have successfully placed in the joining pool";
      else
        return "We could not process your request";
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

  // TODO: 11/19/2021 depreciated using heartbeat
  @GetMapping("/join/me")
  public GameDto getJoinStatus(@AuthenticationPrincipal UserDetails userDetails) {
    return this.gameService.getJoinStatus(userDetails)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No Game is ready"));
  }

  @PostMapping("/disconnect")
  public String disconnect(@AuthenticationPrincipal UserDetails currentUser) {
    if (this.gameService.disconnect(currentUser)) {
      return "You have successfully disconnected";
    } else {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "You are not in a game or in search for one");
    }
  }

}
