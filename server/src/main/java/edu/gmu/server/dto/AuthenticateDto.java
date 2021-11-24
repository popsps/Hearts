package edu.gmu.server.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class AuthenticateDto {
  @Size(min = 3, max = 30, message = "Invalid size for username")
  @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9]{2,29}$", message = "Invalid username provided")
  private String username;
  @Size(min = 5, max = 50, message = "Invalid size for email")
  @Email(regexp = "^[a-zA-Z0-9]+@[a-z0-9!]+\\.[a-zA-Z0-9]+$", message = "We do not allow this email address")
  private String email;
  @NotNull(message = "password must be provided")
  @Size(min = 8, max = 30, message = "Invalid size for password")
  private String password;
}
