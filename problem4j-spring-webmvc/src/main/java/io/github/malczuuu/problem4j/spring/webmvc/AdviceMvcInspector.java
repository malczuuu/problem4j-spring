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
package io.github.malczuuu.problem4j.spring.webmvc;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.context.request.WebRequest;

/**
 * Provides a hook for observing the details of a Problem response before it is returned to the
 * client.
 *
 * <p>Implementations of this interface can peek at the context of a Problem, including the original
 * exception, HTTP headers, status, and request information, without modifying the response.
 *
 * <p>Typical use cases include logging, monitoring, or auditing error responses.
 *
 * @deprecated migrated to {@code io.github.problem4j:problem4j-spring-webmvc} namespace.
 */
@Deprecated(since = "1.1.8")
public interface AdviceMvcInspector {

  /**
   * Observe the details of a Problem response before it is sent to the client.
   *
   * @param context the {@link ProblemContext} containing information about the current error
   *     handling context
   * @param problem the {@link Problem} object representing the response body
   * @param ex the original {@link Exception} that triggered the Problem
   * @param headers the HTTP headers that will be included in the response
   * @param status the HTTP status code for the response
   * @param request the current {@link WebRequest} associated with the handling
   */
  void inspect(
      ProblemContext context,
      Problem problem,
      Exception ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request);
}
