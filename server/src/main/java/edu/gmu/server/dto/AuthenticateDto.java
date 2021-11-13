package edu.gmu.server.dto;

import lombok.Data;

@Data
public class AuthenticateDto {
  private String username;
  private String email;
  private String password;
}
