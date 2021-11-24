package edu.gmu.server.annotation;

import edu.gmu.server.validation.RegisterDtoValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {RegisterDtoValidator.class})
public @interface ValidRegisterDto {
  String message() default "Registration input validation error";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
