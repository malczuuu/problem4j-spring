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
import org.springframework.validation.BindException;

/**
 * Due to {@link BindException} being subclassed by {@code MethodArgumentNotValidException}, this
 * implementation also covers that exceptions.
 *
 * <p>Quite obvious message, but worth to note that the only reason {@code BindResolver} is kept is
 * due to backwards compatibility as {@code problem4j} doesn't use any fields from that subclass at
 * the moment.
 *
 * <p>These exceptions indicate that incoming request parameters or bodies could not be bound to
 * target objects or did not pass validation constraints.
 *
 * <ul>
 *   <li>{@code BindException} - thrown for binding or validation errors on form or query
 *       parameters.
 *   <li>{@code MethodArgumentNotValidException} - thrown for validation failures on
 *       {@code @RequestBody} or {@code @ModelAttribute} method arguments.
 * </ul>
 *
 * @see org.springframework.web.bind.MethodArgumentNotValidException
 */
public class BindProblemResolver extends AbstractProblemResolver {

  private final ViolationResolver violationResolver;

  public BindProblemResolver(ProblemFormat problemFormat) {
    super(BindException.class, problemFormat);
    violationResolver = new ViolationResolver(problemFormat);
  }

  /**
   * Resolves a {@link BindException} (or subclass) to a {@link ProblemBuilder} with {@code
   * ProblemStatus#BAD_REQUEST} and an {@code errors} extension listing field/global validation
   * violations produced by the underlying {@link BindException#getBindingResult()}.
   *
   * @param context problem context (ignored for binding violations)
   * @param ex the binding / validation exception (must be a {@link BindException})
   * @param headers HTTP response headers (unused here but part of the SPI)
   * @param status suggested HTTP status from caller (ignored; BAD_REQUEST is enforced)
   * @return builder pre-populated with validation detail and violations
   */
  @Override
  public ProblemBuilder resolveBuilder(
      ProblemContext context, Exception ex, HttpHeaders headers, HttpStatusCode status) {
    BindException e = (BindException) ex;
    return violationResolver.from(e.getBindingResult()).status(ProblemStatus.BAD_REQUEST);
  }
}
