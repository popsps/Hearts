package edu.gmu.server.validation;

import edu.gmu.server.annotation.ValidRegisterDto;
import edu.gmu.server.dto.RegisterDto;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Slf4j
public class RegisterDtoValidator implements ConstraintValidator<ValidRegisterDto, RegisterDto> {
  @Override
  public void initialize(ValidRegisterDto constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(RegisterDto registerDto, ConstraintValidatorContext constraintValidatorContext) {
    return isPasswordValid(registerDto, constraintValidatorContext);
  }

  private boolean isPasswordValid(RegisterDto registerDto, ConstraintValidatorContext constraintValidatorContext) {
    constraintValidatorContext.disableDefaultConstraintViolation();
    String password = registerDto.getPassword();
    if (password == null || password.equals("")) {
      log.error("Input validation; Missing password field");
      constraintValidatorContext.buildConstraintViolationWithTemplate("Missing password field")
        .addConstraintViolation();
      return false;
    }
    if (!password.matches("^[a-zA-Z\\d@$!%*#?&]{8,30}$")) {
      log.error("Input validation; Invalid input characters for password field");
      constraintValidatorContext.buildConstraintViolationWithTemplate("Invalid input characters for password field")
        .addConstraintViolation();

      return false;
    }
    if (!password.matches("^.*[a-z].*$")) {
      log.error("Input validation; Password missing lowercase letter");
      constraintValidatorContext.buildConstraintViolationWithTemplate("Password missing lowercase letter")
        .addConstraintViolation();

      return false;
    }
    if (!password.matches("^.*[A-Z].*$")) {
      log.error("Input validation; Password missing uppercase letter");
      constraintValidatorContext.buildConstraintViolationWithTemplate("Password missing uppercase letter")
        .addConstraintViolation();

      return false;
    }
    if (!password.matches("^.*\\d.*$")) {
      log.error("Input validation; Password missing digit");
      constraintValidatorContext.buildConstraintViolationWithTemplate("Password missing digit")
        .addConstraintViolation();

      return false;
    }
    if (!password.matches("^.*[@$!%*#?&].*$")) {
      log.error("Input validation; Password missing special character letter");
      constraintValidatorContext.buildConstraintViolationWithTemplate("Password missing special character letter")
        .addConstraintViolation();
      return false;
    }
    return true;
  }
}
