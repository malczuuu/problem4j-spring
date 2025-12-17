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
package io.github.malczuuu.problem4j.spring.web.resolver;

import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.ERRORS_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.VALIDATION_FAILED_DETAIL;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.format.ProblemFormat;
import io.github.malczuuu.problem4j.spring.web.model.Violation;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;

/**
 * Handles {@link ConstraintViolationException} thrown when one or more Bean Validation constraints
 * are violated.
 *
 * <p>Relates with {@code MethodValidationException} (see {@link MethodValidationProblemResolver}).
 *
 * <p>This exception indicates that method parameters, return values, or other validated elements
 * failed to satisfy declared {@code @Valid} or {@code @Constraint} annotations.
 */
public class ConstraintViolationProblemResolver extends AbstractProblemResolver {

  public ConstraintViolationProblemResolver(ProblemFormat problemFormat) {
    super(ConstraintViolationException.class, problemFormat);
  }

  /**
   * Builds a {@link ProblemBuilder} with {@link ProblemStatus#BAD_REQUEST}, a formatted {@code
   * detail}, and an {@code errors} extension listing each constraint violation (property and
   * message) extracted from the exception.
   *
   * @param context problem context (ignored)
   * @param ex the thrown {@link ConstraintViolationException}
   * @param headers HTTP headers (unused here)
   * @param status suggested status (ignored; BAD_REQUEST enforced)
   * @return populated problem builder
   */
  @Override
  public ProblemBuilder resolveBuilder(
      ProblemContext context, Exception ex, HttpHeaders headers, HttpStatusCode status) {
    ConstraintViolationException e = (ConstraintViolationException) ex;
    List<Violation> errors = extractViolations(e);

    return Problem.builder()
        .status(ProblemStatus.BAD_REQUEST)
        .detail(formatDetail(VALIDATION_FAILED_DETAIL))
        .extension(ERRORS_EXTENSION, errors);
  }

  /**
   * Converts each {@link ConstraintViolation} into a {@link Violation} capturing the leaf property
   * name and its validation message.
   */
  private List<Violation> extractViolations(ConstraintViolationException e) {
    return e.getConstraintViolations().stream()
        .map(violation -> new Violation(fetchViolationProperty(violation), violation.getMessage()))
        .toList();
  }

  /**
   * Returns the simple (leaf) property name from a violation's {@link Path}. If the path or its
   * terminal node name is absent, returns an empty string.
   */
  private String fetchViolationProperty(ConstraintViolation<?> violation) {
    if (violation.getPropertyPath() == null) {
      return "";
    }

    String lastElement = null;
    for (Path.Node node : violation.getPropertyPath()) {
      lastElement = node.getName();
    }

    return lastElement != null ? lastElement : "";
  }
}
