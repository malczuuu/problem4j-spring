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
package io.github.problem4j.spring.web.resolver;

import static io.github.problem4j.spring.web.internal.MethodParameterSupport.findParameterName;
import static io.github.problem4j.spring.web.util.ProblemSupport.PROPERTY_EXTENSION;
import static io.github.problem4j.spring.web.util.ProblemSupport.resolveStatus;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.problem4j.spring.web.context.ProblemContext;
import io.github.problem4j.spring.web.format.ProblemFormat;
import java.util.Optional;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ServerWebInputException;

/**
 * Handles {@link ServerWebInputException} thrown when request data cannot be properly read or
 * converted in a WebFlux application.
 *
 * <p>This typically occurs for invalid query parameters, path variables, or request body content
 * that cannot be converted to the target method parameter type.
 *
 * <p>The handler is responsible for returning an appropriate HTTP 400 (Bad Request) response to
 * indicate that the client sent invalid or unreadable input.
 */
public class ServerWebInputProblemResolver extends AbstractProblemResolver {

  private final TypeMismatchProblemResolver typeMismatchProblemResolver;

  public ServerWebInputProblemResolver(ProblemFormat problemFormat) {
    super(ServerWebInputException.class, problemFormat);
    typeMismatchProblemResolver = new TypeMismatchProblemResolver(problemFormat);
  }

  /**
   * Resolves a {@link ServerWebInputException} into a {@link ProblemBuilder}. If the root cause is
   * a {@link TypeMismatchException}, delegates to {@code TypeMismatchResolver} and, when missing,
   * attempts to append the offending property/parameter name as the {@code
   * ProblemSupport#PROPERTY_EXTENSION}. Otherwise, returns a builder whose status reflects the
   * exception's embedded HTTP status code.
   *
   * @param context problem context (unused for this resolver)
   * @param ex the triggering {@link ServerWebInputException}
   * @param headers HTTP headers (unused)
   * @param status suggested status from caller (ignored; status derived from exception)
   * @return builder representing the invalid input condition
   * @see io.github.problem4j.spring.web.util.ProblemSupport#PROPERTY_EXTENSION
   */
  @Override
  public ProblemBuilder resolveBuilder(
      ProblemContext context, Exception ex, HttpHeaders headers, HttpStatusCode status) {
    ServerWebInputException swie = (ServerWebInputException) ex;

    if (ex.getCause() instanceof TypeMismatchException tme) {
      ProblemBuilder builder =
          typeMismatchProblemResolver.resolveBuilder(context, tme, headers, status);
      if (!builder.build().hasExtension(PROPERTY_EXTENSION)) {
        return tryAppendingPropertyFromMethodParameter(swie.getMethodParameter(), builder);
      }
      return builder;
    }

    return Problem.builder().status(resolveStatus(swie.getStatusCode()));
  }

  private ProblemBuilder tryAppendingPropertyFromMethodParameter(
      MethodParameter parameter, ProblemBuilder builder) {
    Optional<String> optionalProperty = findParameterName(parameter);
    if (optionalProperty.isPresent()) {
      builder = builder.extension(PROPERTY_EXTENSION, optionalProperty.get());
    }
    return builder;
  }
}
