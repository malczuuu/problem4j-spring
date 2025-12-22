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

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.problem4j.spring.web.context.ProblemContext;
import io.github.problem4j.spring.web.format.ProblemFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.multipart.MultipartException;

/**
 * Resolves {@link MultipartException}s into {@link Problem} responses with status 400
 * (BAD_REQUEST).
 *
 * <p>A {@link MultipartException} is thrown when a multipart request fails, e.g., due to exceeding
 * file size limits or parsing errors.
 */
public class MultipartProblemResolver extends AbstractProblemResolver {

  public MultipartProblemResolver(ProblemFormat problemFormat) {
    super(MultipartException.class, problemFormat);
  }

  /**
   * Resolves the given {@link MultipartException} into a {@link ProblemBuilder}.
   *
   * <p>The resulting {@link Problem} will have a {@link ProblemStatus#BAD_REQUEST} status.
   *
   * @param context the {@link ProblemContext} providing information about the current request
   * @param ex the {@link MultipartException} to be resolved
   * @param headers the {@link HttpHeaders} of the current response
   * @param status the original {@link HttpStatusCode} that would have been returned
   * @return a {@link ProblemBuilder} for building the {@link Problem} response
   */
  @Override
  public ProblemBuilder resolveBuilder(
      ProblemContext context, Exception ex, HttpHeaders headers, HttpStatusCode status) {
    return Problem.builder().status(ProblemStatus.BAD_REQUEST);
  }
}
