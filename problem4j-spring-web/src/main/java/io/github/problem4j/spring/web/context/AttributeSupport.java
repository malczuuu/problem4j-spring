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

/**
 * Utility class providing constants and helper methods for tracing support within the Problem4J.
 */
public final class AttributeSupport {

  /** Request attribute key used to store a trace identifier. */
  public static final String TRACE_ID_ATTRIBUTE = "io.github.problem4j.spring.web.traceId";

  /**
   * Request attribute key used to store the object associated with the current request. It allows
   * sharing contextual information (such as trace identifiers or additional diagnostic data)
   * between components involved in problem handling.
   */
  public static final String PROBLEM_CONTEXT_ATTRIBUTE = "io.github.problem4j.spring.web.traceId";

  private AttributeSupport() {}
}
