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

/**
 * Thrown when processing an annotated exception into a Problem fails. {@code @RestControllerAdvice}
 * or any other handlers can catch this and return a safe {@code 500 Problem}. No other exception is
 * supposed to be thrown from {@link ProblemMappingProcessor}.
 *
 * @deprecated migrated to {@code io.github.problem4j:problem4j-spring-web} namespace.
 */
@Deprecated(since = "2.0.7")
public class ProblemProcessingException extends RuntimeException {

  /** Creates a new exception with no detail message and no cause. */
  public ProblemProcessingException() {
    super();
  }

  /**
   * Creates a new exception with the specified detail message.
   *
   * @param message human-readable explanation of the failure
   */
  public ProblemProcessingException(String message) {
    super(message);
  }

  /**
   * Creates a new exception wrapping the given cause.
   *
   * @param cause underlying cause (may be {@code null})
   */
  public ProblemProcessingException(Throwable cause) {
    super(cause);
  }

  /**
   * Creates a new exception with the specified detail message and cause.
   *
   * @param message human-readable explanation
   * @param cause underlying cause (may be {@code null})
   */
  public ProblemProcessingException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Advanced constructor allowing full control over suppression and stack trace writability.
   * Typically used only internally or in tests.
   *
   * @param message detail message
   * @param cause underlying cause (may be {@code null})
   * @param enableSuppression whether suppression is enabled or disabled
   * @param writableStackTrace whether the stack trace should be writable
   */
  protected ProblemProcessingException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
