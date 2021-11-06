package edu.gmu.server.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "STATS")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stats implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "user_id", nullable = false)
  private Long userId;
  @Column(name = "win")
  private Integer win = 0;
  @Column(name = "lost")
  private Integer lost = 0;
  @Column(name = "average_placement")
  private Integer averagePlacement = 0;
  @Column(name = "points_taken_per_game")
  private Double pointsTakenPerGame = 0D;
  @Column(name = "points_given_per_game")
  private Double pointsGivenPerGame = 0D;
  @Column(name = "points_taken_per_hand")
  private Double pointsTakenPerHand = 0D;
  @Column(name = "points_given_per_hand")
  private Double pointsGivenPerHand = 0D;
  @Column(name = "take_the_queen")
  private Double takeTheQueen = 0D;
  @Column(name = "give_the_queen")
  private Double giveTheQueen = 0D;

  @MapsId
  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
  @JsonBackReference
  private User user;

}
