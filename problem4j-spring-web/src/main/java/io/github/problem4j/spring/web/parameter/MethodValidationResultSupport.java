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
package io.github.problem4j.spring.web.parameter;

import io.github.problem4j.core.ProblemBuilder;
import java.util.List;
import org.springframework.validation.method.MethodValidationResult;

/** Support for converting Spring {@link MethodValidationResult}s into {@link ProblemBuilder}s. */
public interface MethodValidationResultSupport {

  /**
   * Builds a {@link Violation}s list from a {@link MethodValidationResult} produced by method /
   * parameter validation (e.g. {@code @Validated} on a controller). Each parameter violation is
   * mapped to a {@code Violation} whose name is the resolved method parameter name and message is
   * the constraint message.
   *
   * @param result aggregated method validation result (must not be {@code null})
   * @return list of violations extracted from the validation result
   */
  List<Violation> fetchViolations(MethodValidationResult result);
}
