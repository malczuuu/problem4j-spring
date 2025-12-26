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
 * Settings used when building a {@link ProblemContext} for incoming requests.
 *
 * <p>Provides access to infrastructure configuration such as the HTTP header name that carries a
 * trace identifier. Implementations are typically backed by external configuration (e.g. Spring
 * Boot properties) and are expected to be thread-safe.
 *
 * @deprecated migrated to {@code io.github.problem4j:problem4j-spring-web} namespace.
 */
@Deprecated(since = "1.1.7")
public interface ProblemContextSettings {

  /**
   * Returns the name of the HTTP header that contains a trace / correlation ID.
   *
   * <p>The trace ID (if present) may be injected into generated Problem responses (e.g. via
   * placeholders in instance/type override templates) and echoed back to clients to aid in log
   * correlation and diagnostics.
   *
   * @return the tracing header name, or {@code null} if tracing is disabled / not configured
   */
  String getTracingHeaderName();
}
