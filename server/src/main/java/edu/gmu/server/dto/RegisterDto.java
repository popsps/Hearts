package edu.gmu.server.dto;

import edu.gmu.server.annotation.ValidRegisterDto;
import lombok.Data;

import javax.validation.constraints.*;

@Data
@ValidRegisterDto
public class RegisterDto {
  @NotNull(message = "username must be provided")
  @Size(min = 3, max = 30, message = "Invalid size for username")
  @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9]{2,29}$", message = "Invalid username provided")
  private String username;
  @NotNull(message = "password must be provided")
  @Size(min = 8, max = 30, message = "Invalid size for password")
  private String password;
  @Size(min = 2, max = 50, message = "Invalid size for firstname")
  @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9' ]{1,49}$", message = "Invalid firstname provided")
  private String firstName;
  @Size(min = 2, max = 50, message = "Invalid size for lastname")
  @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9' ]{1,49}$", message = "Invalid lastname provided")
  private String lastName;
  @NotNull(message = "nickname must be provided")
  @Size(min = 2, max = 60, message = "Invalid size for nickname")
  @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9 @$!%*#?&']{1,59}$", message = "Invalid nickname provided")
  private String nickname;
  @NotNull(message = "email must be provided")
  @Size(min = 5, max = 50, message = "Invalid size for email")
  @Email(regexp = "^[a-zA-Z0-9]+@[a-z0-9!']+\\.[a-zA-Z0-9]+$", message = "We do not allow this email address")
  private String email;
}
