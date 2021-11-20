package edu.gmu.server.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
  @JsonIgnore
  private Long userId;
  @Column(name = "win")
  private Integer win = 0;
  @Column(name = "lost")
  private Integer lost = 0;
  @Column(name = "average_placement")
  private Double averagePlacement = 0D;
  @Column(name = "points_taken_per_game")
  private Double pointsTakenPerGame = 0D;

  @MapsId
  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
  @JsonBackReference
  private User user;

}
