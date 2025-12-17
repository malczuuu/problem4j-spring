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

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to map a {@link Throwable} to an RFC 7807 {@code Problem}.
 *
 * <p>This annotation allows you to declaratively specify how a specific exception should be
 * converted into a Problem response. All string values support dynamic interpolation of
 * placeholders based on the exception's fields and context.
 *
 * <h3>Interpolation</h3>
 *
 * <ul>
 *   <li>{@code {message}} -> the exception's {@link Throwable#getMessage()}
 *   <li>{@code {context.traceId}} -> the traceId from {@code ProblemContext#getTraceId()} (special
 *       shorthand)
 *   <li>{@code {fieldName}} -> value of any field (private or public) in the exception class
 *       hierarchy
 *   <li>Any placeholder that resolves to null or an empty string is ignored in the final output
 * </ul>
 *
 * <h3>Inheritance</h3>
 *
 * <p>This annotation is {@link Inherited}, so subclasses of an annotated exception automatically
 * inherit the mapping unless explicitly overridden with a new annotation.
 *
 * <h3>Extensions</h3>
 *
 * <p>Use {@link #extensions()} to expose additional fields as Problem extensions. Each name is
 * resolved using the same rules as placeholders (fields in the class hierarchy). Null or empty
 * values are automatically omitted from the final Problem.
 *
 * <h3>Defaulting behavior</h3>
 *
 * <ul>
 *   <li>If {@link #type()} is empty, the processor may apply a default type (e.g., {@code
 *       about:blank}).
 *   <li>If {@link #title()} is empty, the processor may assign the standard HTTP reason phrase
 *       corresponding to the status code.
 *   <li>Status code {@code 0} is interpreted as "unspecified".
 * </ul>
 *
 * <h3>Example usage</h3>
 *
 * <pre>{@code
 * @ProblemMapping(
 *     type = "https://example.org/errors/validation",
 *     title = "Validation Failed",
 *     status = 400,
 *     detail = "Invalid input for user {userId}, trace {context.traceId}",
 *     extensions = {"userId", "fieldName"}
 * )
 * public class ValidationException extends RuntimeException {
 *     private final String userId;
 *     private final String fieldName;
 *
 *     public ValidationException(String userId, String fieldName) {
 *         super("Validation failed for user " + userId);
 *         this.userId = userId;
 *         this.fieldName = fieldName;
 *     }
 * }
 * }</pre>
 *
 * <p>This annotation provides a simple and consistent way to map exceptions to RFC 7807 Problems,
 * with support for dynamic data inclusion, null/empty-safe interpolation, and subclass inheritance.
 *
 * @see io.github.malczuuu.problem4j.core.Problem
 * @see io.github.malczuuu.problem4j.spring.web.context.ProblemContext#getTraceId()
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ProblemMapping {

  /**
   * Interpolated type URI for the problem.
   *
   * <p>Supports placeholders of the form {@code {name}}:
   *
   * <ul>
   *   <li>{@code {message}} -> exception message
   *   <li>{@code {context.traceId}} -> trace ID from {@code ProblemContext}
   *   <li>{@code {fieldName}} -> value of any field in the exception class hierarchy
   * </ul>
   *
   * <p>If empty, a default type (e.g., {@code about:blank}) may be applied by the processor. Null
   * or empty placeholders are ignored.
   */
  String type() default "";

  /**
   * Interpolated title of the problem.
   *
   * <p>Supports placeholders the same way as {@link #type()}.
   *
   * <p>If empty, the processor may assign the standard HTTP reason phrase corresponding to the
   * {@link #status()}. Null or empty placeholders are ignored.
   */
  String title() default "";

  /**
   * HTTP status code for the problem.
   *
   * <p>{@code 0} means "unspecified"; in that case, the processor may apply a default. Used to
   * determine the response status and may influence default title assignment.
   */
  int status() default 0;

  /**
   * Interpolated detailed description of the problem.
   *
   * <p>Supports placeholders the same way as {@link #type()}.
   *
   * <p>Null or empty placeholder values are ignored in the resulting string.
   */
  String detail() default "";

  /**
   * Interpolated instance URI identifying this occurrence of the problem.
   *
   * <p>Supports placeholders the same way as {@link #type()}.
   *
   * <p>If invalid URI or placeholder resolves to null/empty, the processor ignores it. Useful for
   * linking to logs or trace-specific URLs.
   */
  String instance() default "";

  /**
   * Names of fields in the exception class to expose as Problem extensions.
   *
   * <p>Each name is resolved using the same rules as placeholders. Null or empty values are
   * omitted.
   *
   * <p>This allows exposing additional context-specific data for clients, beyond the standard RFC
   * 7807 fields.
   */
  String[] extensions() default {};
}
