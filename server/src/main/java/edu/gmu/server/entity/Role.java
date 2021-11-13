package edu.gmu.server.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "role")
@NoArgsConstructor
public class Role implements Serializable, GrantedAuthority {
  @Id
  @Column(name = "id")
  @JsonIgnore
  @Getter
  @Setter
  Long id;

  @Column(name = "name")
  @Getter
  @Setter
  String name;

  public Role(String name) {
    String role = name.toUpperCase();
    switch (role) {
      case "ADMIN":
        this.setId(1L);
        break;
      case "PLAYER":
        this.setId(2L);
        break;
      default:
        this.id = null;
    }
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Role)) return false;
    Role role = (Role) o;
    return Objects.equals(getName(), role.getName());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getName());
  }

  @Override
  public String getAuthority() {
    return "ROLE_" + this.getName();
  }
}
