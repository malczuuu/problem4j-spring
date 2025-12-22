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
package io.github.problem4j.spring.webmvc.app.problem;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemException;

public class ExtendedException extends ProblemException {

  public ExtendedException(String value1, Long value2, boolean value3) {
    super(
        Problem.builder()
            .type("https://example.org/extended/" + value1)
            .title("Extended Exception")
            .status(418)
            .detail("value2:" + value2)
            .instance("https://example.org/extended/instance/" + value3)
            .build());
  }
}
