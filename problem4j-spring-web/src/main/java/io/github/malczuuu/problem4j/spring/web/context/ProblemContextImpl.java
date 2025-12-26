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

import java.util.Objects;

/**
 * @deprecated migrated to {@code io.github.problem4j:problem4j-spring-web} namespace.
 */
@Deprecated(since = "1.1.7")
class ProblemContextImpl implements ProblemContext {

  static final ProblemContext EMPTY = ProblemContext.builder().build();

  private final String traceId;

  ProblemContextImpl(String traceId) {
    this.traceId = traceId;
  }

  @Override
  public String getTraceId() {
    return traceId;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof ProblemContext that)) {
      return false;
    }
    return Objects.equals(getTraceId(), that.getTraceId());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getTraceId());
  }

  @Override
  public String toString() {
    return "ProblemContext{traceId='" + getTraceId() + "'}";
  }
}
