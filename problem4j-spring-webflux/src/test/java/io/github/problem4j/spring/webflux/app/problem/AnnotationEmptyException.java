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
package io.github.problem4j.spring.webflux.app.problem;

import io.github.problem4j.core.ProblemMapping;

@ProblemMapping
public class AnnotationEmptyException extends RuntimeException {

  private final String value1;
  private final Long value2;
  private final boolean value3;

  public AnnotationEmptyException(String value1, Long value2, boolean value3) {
    this.value1 = value1;
    this.value2 = value2;
    this.value3 = value3;
  }

  public String getValue1() {
    return value1;
  }

  public Long getValue2() {
    return value2;
  }

  public boolean isValue3() {
    return value3;
  }
}
