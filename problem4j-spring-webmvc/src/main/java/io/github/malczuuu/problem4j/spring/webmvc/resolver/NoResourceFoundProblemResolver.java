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
package io.github.malczuuu.problem4j.spring.webmvc.resolver;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.format.ProblemFormat;
import io.github.malczuuu.problem4j.spring.web.resolver.AbstractProblemResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Handles {@link NoResourceFoundException} thrown when a requested static resource cannot be found
 * in application.
 *
 * <p>This typically occurs when the client requests a URL that is mapped to static resources (e.g.,
 * files under {@code /static} or {@code /public}) but no matching resource exists.
 *
 * <p>The handler is responsible for returning an appropriate HTTP 404 (Not Found) response to
 * indicate that the requested resource is not available.
 */
public class NoResourceFoundProblemResolver extends AbstractProblemResolver {

  public NoResourceFoundProblemResolver(ProblemFormat problemFormat) {
    super(NoResourceFoundException.class, problemFormat);
  }

  /**
   * Returns a {@link ProblemBuilder} with {@link ProblemStatus#NOT_FOUND} (HTTP 404) indicating the
   * requested static resource could not be located. Other parameters ({@code context}, {@code
   * headers}, {@code status}) are ignored because the exception semantics unambiguously map to 404.
   *
   * @param context problem context (unused)
   * @param ex the triggering {@link NoResourceFoundException}
   * @param headers HTTP headers (unused)
   * @param status suggested status from caller (ignored; 404 enforced)
   * @return builder pre-populated with NOT_FOUND status
   */
  @Override
  public ProblemBuilder resolveBuilder(
      ProblemContext context, Exception ex, HttpHeaders headers, HttpStatusCode status) {
    return Problem.builder().status(ProblemStatus.NOT_FOUND);
  }
}
