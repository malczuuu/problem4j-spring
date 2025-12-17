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
package io.github.malczuuu.problem4j.spring.web.resolver;

import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.KIND_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.PROPERTY_EXTENSION;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.TYPE_MISMATCH_DETAIL;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.format.ProblemFormat;
import java.util.Locale;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Handles {@link TypeMismatchException} thrown when a request parameter, path variable, or property
 * cannot be converted to the required type.
 *
 * <p>This typically occurs when the client sends a value that cannot be converted to the expected
 * Java type, for example passing a string where an integer is required.
 *
 * <p>The handler is responsible for returning an appropriate HTTP 400 (Bad Request) response to
 * indicate that the provided input has an invalid type.
 */
public class TypeMismatchProblemResolver extends AbstractProblemResolver {

  public TypeMismatchProblemResolver(ProblemFormat problemFormat) {
    super(TypeMismatchException.class, problemFormat);
  }

  /**
   * Resolves a {@link TypeMismatchException} (also {@link MethodArgumentTypeMismatchException})
   * into a {@link ProblemBuilder} with status {@link ProblemStatus#BAD_REQUEST}, a standardized
   * detail ({@code ProblemSupport#TYPE_MISMATCH_DETAIL}), and optional extensions:
   *
   * <ul>
   *   <li>{@code property} ({@code ProblemSupport#PROPERTY_EXTENSION}) - name of the parameter /
   *       property that failed conversion
   *   <li>{@code kind} ({@code ProblemSupport#KIND_EXTENSION}) - required target type in lowercase
   *       simple form
   * </ul>
   *
   * <p>Older Spring versions may not populate {@code propertyName} for {@code
   * MethodArgumentTypeMismatchException}; in that case this resolver falls back to {@code
   * MethodArgumentTypeMismatchException#getName()}.
   *
   * @param context problem context (unused)
   * @param ex the triggering type mismatch exception
   * @param headers HTTP headers (unused)
   * @param status suggested status (ignored; BAD_REQUEST enforced)
   * @return builder populated with status, detail and relevant extensions
   * @see io.github.malczuuu.problem4j.spring.web.util.ProblemSupport
   */
  @Override
  public ProblemBuilder resolveBuilder(
      ProblemContext context, Exception ex, HttpHeaders headers, HttpStatusCode status) {
    ProblemBuilder builder =
        Problem.builder()
            .status(ProblemStatus.BAD_REQUEST)
            .detail(formatDetail(TYPE_MISMATCH_DETAIL));

    TypeMismatchException ex1 = (TypeMismatchException) ex;

    String property = ex1.getPropertyName();
    String kind =
        ex1.getRequiredType() != null
            ? ex1.getRequiredType().getSimpleName().toLowerCase(Locale.ROOT)
            : null;

    // could happen in some early 3.0.x versions of Spring Boot, cannot add tests for it as newer
    // versions assign it to propertyName in constructor
    if (property == null && ex instanceof MethodArgumentTypeMismatchException ex2) {
      property = ex2.getName();
    }

    if (property != null) {
      builder = builder.extension(PROPERTY_EXTENSION, property);
    }
    if (kind != null) {
      builder = builder.extension(KIND_EXTENSION, kind);
    }
    return builder;
  }
}
