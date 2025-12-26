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
package io.github.malczuuu.problem4j.spring.web.annotation;

import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import java.util.regex.Pattern;

/**
 * Converts exceptions annotated with {@link ProblemMapping} into {@link ProblemBuilder} instances,
 * which can be further extended or executed to create {@code Problem} response.
 *
 * <p>Implementations may optionally make use of a {@link ProblemContext} to provide request- or
 * application-specific data such as trace IDs.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * ProblemMappingProcessor processor = ...;
 * Throwable ex = new ValidationException("user-123", "email");
 * if (processor.isAnnotated(ex)) {
 *     Problem problem = processor.toBuilder(ex, context).build();
 * }
 * }</pre>
 *
 * <p>Implementations should return {@code null} if the exception class is not annotated with {@code
 * ProblemMapping}, and may throw {@link ProblemProcessingException} if an error occurs during
 * problem creation.
 *
 * @see io.github.malczuuu.problem4j.core.Problem
 * @deprecated migrated to {@code io.github.problem4j:problem4j-spring-web} namespace.
 */
@Deprecated(since = "2.0.7")
public interface ProblemMappingProcessor {

  Pattern PLACEHOLDER = Pattern.compile("\\{([^}]+)}");

  String MESSAGE_LABEL = "message";
  String TRACE_ID_LABEL = "context.traceId";

  /**
   * Convert {@link Throwable} -> {@link ProblemBuilder} according to its {@link ProblemMapping}
   * annotation. Such builder can be further extended or executed to create {@code Problem}
   * response.
   *
   * @param t {@link Throwable} to convert (may be {@code null})
   * @param context optional {@link ProblemContext} (may be {@code null})
   * @return a {@link ProblemBuilder} instance
   * @throws ProblemProcessingException when something goes wrong while building the Problem
   * @see io.github.malczuuu.problem4j.core.Problem
   */
  ProblemBuilder toProblemBuilder(Throwable t, ProblemContext context);

  /**
   * Checks whether the given exception class is annotated with {@link ProblemMapping}.
   *
   * @param t {@link Throwable} to check (can be {@code null})
   * @return {@code true} if the exception is annotated with {@link ProblemMapping}, {@code false}
   *     otherwise
   */
  boolean isMappingCandidate(Throwable t);
}
