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
package io.github.problem4j.spring.web.context;

import java.util.UUID;

/**
 * Utility class providing constants and helper methods for tracing support within the Problem4J.
 */
public final class ContextSupport {

  /** Request attribute key used to store a trace identifier. */
  public static final String TRACE_ID =
      "io.github.malczuuu.problem4j.spring.web.context.ProblemContext.traceId";

  /**
   * Request attribute key used to store the {@link ProblemContext} object associated with the
   * current request. It allows sharing contextual information (such as trace identifiers or
   * additional diagnostic data) between components involved in problem handling.
   */
  public static final String PROBLEM_CONTEXT =
      "io.github.malczuuu.problem4j.spring.web.context.ProblemContext";

  /**
   * Generates a random trace identifier in {@code urn:uuid:<uuid>} format.
   *
   * @return generated trace identifier
   */
  public static String getRandomTraceId() {
    return "urn:uuid:" + UUID.randomUUID();
  }

  private ContextSupport() {}
}
