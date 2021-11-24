package edu.gmu.server.annotation;

import edu.gmu.server.validation.AuthenticationDtoValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {AuthenticationDtoValidator.class})
public @interface ValidAuthenticationDto {
  String message() default "Authentication input validation error";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
