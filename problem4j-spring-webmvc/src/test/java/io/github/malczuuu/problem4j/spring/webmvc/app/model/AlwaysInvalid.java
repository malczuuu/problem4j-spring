/*
 * Copyright (c) 2025 Damian Malczewski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * SPDX-License-Identifier: MIT
 */
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
