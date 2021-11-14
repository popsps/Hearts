package edu.gmu.server.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Blob;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "USER")
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  @Getter
  @Setter
  private Long id;

  @Column(name = "username", unique = true, nullable = false)
  @Getter
  @Setter
  private String username;

  @Column(name = "password", nullable = false)
  @Getter
  @Setter
  @JsonIgnore()
  private String password;


  @Column(name = "first_name")
  @Getter
  @Setter
  private String firstName;

  @Column(name = "last_name")
  @Getter
  @Setter
  private String lastName;

  @Column(name = "nickname")
  @Getter
  @Setter
  private String nickname;

  @Column(name = "email", unique = true, nullable = false)
  @Getter
  @Setter
  private String email;

  @Column(name = "time_created")
  @Getter
  private LocalDateTime createTime;
  @Column(name = "last_accessed")
  @Getter
  private LocalDateTime lastAccessed;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "role_id", referencedColumnName = "id", nullable = false)
  private Role role;

  @Getter
  @Setter
  @OneToOne(mappedBy = "user")
  @PrimaryKeyJoinColumn
  private Stats stats;


  @Column(name = "profile_picture")
  @Lob
  @Getter
  @Setter
  @JsonIgnore()
  private Blob profilePicture;

  @Column(name = "profile_picture_name")
  @Getter
  @Setter
  @JsonIgnore()
  private String profilePictureName;

  public String getRole() {
    if (this.role != null)
      return role.getName();
    else
      return null;
  }

  public void setRole(String name) {
    this.role = new Role(name);
  }

  public void setCreateTime(String createTime) {
    this.createTime = LocalDateTime.parse(createTime, DateTimeFormatter.ISO_DATE_TIME);
  }

  public void setLastAccessed(String lastAccessed) {
    this.lastAccessed = LocalDateTime.parse(lastAccessed, DateTimeFormatter.ISO_DATE_TIME);
  }

}

