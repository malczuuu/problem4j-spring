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
package io.github.problem4j.spring.web.resolver;

import io.github.problem4j.core.ProblemBuilder;
import io.github.problem4j.core.ProblemStatus;
import io.github.problem4j.core.ProblemContext;
import io.github.problem4j.spring.web.format.ProblemFormat;
import io.github.problem4j.spring.web.internal.ViolationResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.validation.method.MethodValidationException;

/**
 * Handles {@link MethodValidationException} thrown when method-level Bean Validation fails.
 *
 * <p>This exception is raised for methods annotated with {@code @Validated} or containing
 * {@code @Constraint}-annotated parameters or return values that do not satisfy declared validation
 * rules.
 *
 * <p>When method validation adaptation is enabled (e.g. via {@code @EnableMethodValidation}),
 * Spring intercepts method invocations, delegates to a Bean Validation provider, and wraps any
 * resulting {@code ConstraintViolationException} in a {@link MethodValidationException}.
 *
 * <p>This allows framework components and exception handlers to deal with a consistent,
 * Spring-specific exception type instead of the raw Jakarta exception.
 *
 * <p>Always resolves to a problem with status {@link ProblemStatus#BAD_REQUEST} and an {@code
 * errors} extension populated via {@link ViolationResolver} (one entry per violated parameter /
 * return value).
 *
 * @see jakarta.validation.ConstraintViolationException
 */
public class MethodValidationProblemResolver extends AbstractProblemResolver {

  private final ViolationResolver violationResolver;

  public MethodValidationProblemResolver(ProblemFormat problemFormat) {
    super(MethodValidationException.class, problemFormat);
    violationResolver = new ViolationResolver(problemFormat);
  }

  /**
   * Converts the {@link MethodValidationException} into a {@link ProblemBuilder} with status {@code
   * BAD_REQUEST} and an {@code errors} extension describing each parameter or return value
   * violation. Other parameters ({@code context}, {@code headers}, {@code status}) are ignored for
   * status selection; 400 is enforced.
   *
   * @param context problem context (unused)
   * @param ex the thrown {@link MethodValidationException}
   * @param headers HTTP headers (unused)
   * @param status suggested status (ignored; BAD_REQUEST enforced)
   * @return builder pre-populated with validation details and BAD_REQUEST status
   * @see ProblemStatus#BAD_REQUEST
   */
  @Override
  public ProblemBuilder resolveBuilder(
      ProblemContext context, Exception ex, HttpHeaders headers, HttpStatusCode status) {
    MethodValidationException e = (MethodValidationException) ex;
    return violationResolver.from(e).status(ProblemStatus.BAD_REQUEST);
  }
}
