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
package io.github.problem4j.spring.web.processor;

import io.github.problem4j.core.Problem;
import io.github.problem4j.core.ProblemContext;

/**
 * Convenience implementation for {@link ProblemPostProcessor} which doesn't transform input data.
 */
public class IdentityProblemPostProcessor implements ProblemPostProcessor {

  /**
   * Returns the given {@link Problem} unchanged.
   *
   * @param context optional problem context (ignored)
   * @param problem the problem instance to pass through (may be {@code null})
   * @return the same instance provided in {@code problem}
   */
  @Override
  public Problem process(ProblemContext context, Problem problem) {
    return problem;
  }
}
