package io.github.malczuuu.problem4j.spring.webmvc.app.model;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = AlwaysInvalid.AlwaysInvalidValidator.class)
@Target(TYPE)
@Retention(RUNTIME)
public @interface AlwaysInvalid {

  String message() default "always invalid";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  class AlwaysInvalidValidator implements ConstraintValidator<AlwaysInvalid, Object> {
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
      return false;
    }
  }
}
