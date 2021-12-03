package edu.gmu.server.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "USER")
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Data
public class UserInfo implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @Column(name = "id", nullable = false)
  private Long id;
  @Column(name = "username", unique = true, nullable = false)
  private String username;
  @Column(name = "nickname")
  @Getter
  @Setter
  private String nickname;
  @Getter
  @Setter
  @OneToOne(mappedBy = "user")
  @PrimaryKeyJoinColumn
  private Stats stats;
}
