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

import io.github.problem4j.core.Problem;
import io.github.problem4j.core.ProblemBuilder;
import io.github.problem4j.core.ProblemContext;
import io.github.problem4j.core.ProblemStatus;
import io.github.problem4j.spring.web.ProblemFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.converter.HttpMessageNotReadableException;

/**
 * Handles {@link HttpMessageNotReadableException} thrown when an HTTP request body cannot be read
 * or parsed.
 *
 * <p>This typically occurs for requests with malformed JSON, XML, or other payloads that cannot be
 * converted by the configured {@code HttpMessageConverter}s.
 *
 * <p>The handler is responsible for returning an appropriate HTTP 400 (Bad Request) response to
 * indicate that the request body is invalid or unreadable.
 *
 * @see org.springframework.http.converter.HttpMessageConverter
 */
public class HttpMessageNotReadableProblemResolver extends AbstractProblemResolver {

  public HttpMessageNotReadableProblemResolver(ProblemFormat problemFormat) {
    super(HttpMessageNotReadableException.class, problemFormat);
  }

  /**
   * Returns a {@link ProblemBuilder} with {@link ProblemStatus#BAD_REQUEST} (HTTP 400). Other
   * parameters ({@code context}, {@code headers}, {@code status}) are ignored because a malformed
   * or unreadable request body always maps to a client error.
   *
   * @param context problem context (unused)
   * @param ex the triggering {@link HttpMessageNotReadableException}
   * @param headers HTTP headers (unused)
   * @param status suggested status from caller (ignored; 400 enforced)
   * @return builder pre-populated with 400 status
   */
  @Override
  public ProblemBuilder resolveBuilder(
      ProblemContext context, Exception ex, HttpHeaders headers, HttpStatusCode status) {
    return Problem.builder().status(ProblemStatus.BAD_REQUEST);
  }
}
