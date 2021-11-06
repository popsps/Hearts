package edu.gmu.server.repository;

import edu.gmu.server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);

  @Query(value = "FROM User u WHERE " +
    "u.username = :usernameOrEmail " +
    "OR " +
    "u.email = :usernameOrEmail")
  Optional<User> findByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);
}
