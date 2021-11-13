package edu.gmu.server.dto;

import lombok.Data;

@Data
public class RegisterDto {

  private String username;
  private String password;
  private String firstName;
  private String lastName;
  private String nickname;
  private String email;
}
