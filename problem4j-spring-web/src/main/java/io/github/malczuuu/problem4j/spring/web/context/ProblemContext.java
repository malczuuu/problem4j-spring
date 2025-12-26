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
package io.github.malczuuu.problem4j.spring.web.context;

/**
 * Context passed for problem processing.
 *
 * <p>Used by components such as:
 *
 * <ul>
 *   <li>{@link io.github.malczuuu.problem4j.spring.web.annotation.ProblemMappingProcessor}
 *   <li>{@link io.github.malczuuu.problem4j.spring.web.resolver.ProblemResolver}
 * </ul>
 *
 * Implementations provide access to values used for message interpolation or metadata enrichment.
 *
 * @deprecated migrated to {@code io.github.problem4j:problem4j-spring-web} namespace.
 */
@Deprecated(since = "2.0.7")
public interface ProblemContext {

  /**
   * Creates a new {@link ProblemContextBuilder}.
   *
   * @return new builder instance
   */
  static ProblemContextBuilder builder() {
    return new ProblemContextBuilderImpl();
  }

  /**
   * Creates an empty {@link ProblemContext}.
   *
   * @return empty problem context
   */
  static ProblemContext empty() {
    return ProblemContextImpl.EMPTY;
  }

  /**
   * Creates a {@link ProblemContext} containing only a trace identifier.
   *
   * @param traceId trace identifier object, may be {@code null}
   * @return new problem context
   */
  static ProblemContext ofTraceId(Object traceId) {
    return builder().traceId(traceId).build();
  }

  /**
   * Creates a {@link ProblemContext} containing only a trace identifier.
   *
   * @param traceId trace identifier string, may be {@code null}
   * @return new problem context
   */
  static ProblemContext ofTraceId(String traceId) {
    return builder().traceId(traceId).build();
  }

  /**
   * Returns the trace identifier associated with this context.
   *
   * @return trace identifier, or {@code null} if not set
   */
  String getTraceId();
}
