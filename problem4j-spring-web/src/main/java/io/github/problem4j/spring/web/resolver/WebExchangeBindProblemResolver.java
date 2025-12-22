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
import io.github.problem4j.core.ProblemContext;
import io.github.problem4j.core.ProblemStatus;
import io.github.problem4j.spring.web.format.ProblemFormat;
import io.github.problem4j.spring.web.internal.ViolationResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.support.WebExchangeBindException;

/**
 * Handles {@link WebExchangeBindException} thrown when binding and validation of request data in a
 * WebFlux application fails.
 *
 * <p>This typically occurs when request parameters, path variables, or body content cannot be bound
 * to a target object or violate validation constraints.
 *
 * <p>The handler is responsible for returning an appropriate HTTP 400 (Bad Request) response, often
 * including details about which fields failed binding or validation.
 */
public class WebExchangeBindProblemResolver extends AbstractProblemResolver {

  private final ViolationResolver violationResolver;

  public WebExchangeBindProblemResolver(ProblemFormat problemFormat) {
    super(WebExchangeBindException.class, problemFormat);
    violationResolver = new ViolationResolver(problemFormat);
  }

  /**
   * Converts the {@link WebExchangeBindException} into a {@link ProblemBuilder} with status {@code
   * ProblemStatus#BAD_REQUEST} and an {@code errors} extension listing field/global validation
   * violations extracted from its {@code BindingResult}.
   *
   * @param context problem context (unused)
   * @param ex the triggering {@link WebExchangeBindException}
   * @param headers HTTP headers (unused)
   * @param status suggested status (ignored; BAD_REQUEST enforced)
   * @return builder populated with validation detail and violations
   */
  @Override
  public ProblemBuilder resolveBuilder(
      ProblemContext context, Exception ex, HttpHeaders headers, HttpStatusCode status) {
    WebExchangeBindException e = (WebExchangeBindException) ex;
    return violationResolver.from(e.getBindingResult()).status(ProblemStatus.BAD_REQUEST);
  }
}
