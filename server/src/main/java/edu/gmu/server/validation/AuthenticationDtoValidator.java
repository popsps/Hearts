package edu.gmu.server.validation;

import edu.gmu.server.annotation.ValidAuthenticationDto;
import edu.gmu.server.dto.AuthenticateDto;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Slf4j
public class AuthenticationDtoValidator implements ConstraintValidator<ValidAuthenticationDto, AuthenticateDto> {
  @Override
  public void initialize(ValidAuthenticationDto constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(AuthenticateDto authenticateDto, ConstraintValidatorContext constraintValidatorContext) {
    return false;
  }

}
