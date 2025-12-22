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

class ProblemContextBuilderImpl implements ProblemContextBuilder {

  private String traceId;

  @Override
  public ProblemContextBuilderImpl traceId(Object traceId) {
    if (traceId != null) {
      this.traceId = traceId.toString();
    }
    return this;
  }

  @Override
  public ProblemContextBuilderImpl traceId(String traceId) {
    this.traceId = traceId;
    return this;
  }

  @Override
  public ProblemContext build() {
    return new ProblemContextImpl(traceId);
  }
}
