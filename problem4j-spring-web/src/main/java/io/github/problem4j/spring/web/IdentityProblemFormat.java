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
package io.github.problem4j.spring.web;

/** Convenience implementation for {@link ProblemFormat} which doesn't transform input data. */
public class IdentityProblemFormat implements ProblemFormat {

  /**
   * Returns the input detail unchanged (identity formatting).
   *
   * @param detail original detail text (may be {@code null})
   * @return the same {@code detail} value
   */
  @Override
  public String formatDetail(String detail) {
    return detail;
  }
}
