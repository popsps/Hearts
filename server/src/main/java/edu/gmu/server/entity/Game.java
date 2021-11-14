package edu.gmu.server.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.gmu.server.dto.UserDto;
import edu.gmu.server.exception.HeartsGameIsFullException;
import edu.gmu.server.model.Player;
import edu.gmu.server.model.Status;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "GAME")
public class Game implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "status", nullable = false)
  private Status status;

  @Column(name = "logs", nullable = false)
  private String logs;

  @Column(name = "session_created", nullable = true)
  private LocalDateTime sessionCreated;
  @Column(name = "session_ended", nullable = true)
  private LocalDateTime sessionEnded;

  @ManyToMany
  @JoinTable(
    name = "user_has_game",
    joinColumns = @JoinColumn(name = "game_id"),
    inverseJoinColumns = @JoinColumn(name = "user_id"))
  private Set<UserDto> users;

  public List<String> getLogs() {
    ObjectMapper objectMapper = new ObjectMapper();
    List<String> logList = new ArrayList<>();
    try {
      logList = objectMapper.readValue(logs, new ArrayList<String>().getClass());
    } catch (JsonProcessingException e) {
      log.error("Failed to parse json logs. This could happen when there are no logs", e.getMessage());
    }
    return logList;
  }
}
