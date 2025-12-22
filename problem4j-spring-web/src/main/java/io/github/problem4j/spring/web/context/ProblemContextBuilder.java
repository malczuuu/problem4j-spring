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

/** Builder for creating {@link ProblemContext} instances. */
public interface ProblemContextBuilder {

  /**
   * Sets the trace identifier for this context.
   *
   * @param traceId trace identifier object, may be {@code null}
   * @return this builder
   */
  ProblemContextBuilder traceId(Object traceId);

  /**
   * Sets the trace identifier for this context.
   *
   * @param traceId trace identifier string, may be {@code null}
   * @return this builder
   */
  ProblemContextBuilder traceId(String traceId);

  /**
   * Builds a new {@link ProblemContext} instance.
   *
   * @return new problem context
   */
  ProblemContext build();
}
