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
package io.github.malczuuu.problem4j.spring.webmvc;

import static io.github.malczuuu.problem4j.spring.web.context.ContextSupport.PROBLEM_CONTEXT;
import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.resolveStatus;
import static io.github.malczuuu.problem4j.spring.webmvc.MvcAdviceSupport.logAdviceException;
import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.ProblemResolverStore;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.processor.ProblemPostProcessor;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Handles Spring framework exceptions using registered {@code ProblemResolver}s.
 *
 * <p>This class extends {@link ResponseEntityExceptionHandler} and overrides {@code
 * handleExceptionInternal} to replace the response body with a {@link Problem} object.
 *
 * <p>Behavior:
 *
 * <ul>
 *   <li>Delegates exception-to-problem mapping to {@link ProblemResolverStore}.
 *   <li>Sets content type to {@code application/problem+json}.
 *   <li>Falls back to {@link ProblemStatus#INTERNAL_SERVER_ERROR} if mapping fails.
 * </ul>
 *
 * @see #handleExceptionInternal
 * @see io.github.malczuuu.problem4j.spring.web.resolver.ProblemResolver
 * @deprecated migrated to {@code io.github.problem4j:problem4j-spring-webmvc} namespace.
 */
@RestControllerAdvice
@Deprecated(since = "1.1.8")
public class ProblemEnhancedMvcHandler extends ResponseEntityExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(ProblemEnhancedMvcHandler.class);

  private final ProblemResolverStore problemResolverStore;
  private final ProblemPostProcessor problemPostProcessor;

  private final List<AdviceMvcInspector> adviceMvcInspectors;

  public ProblemEnhancedMvcHandler(
      ProblemResolverStore problemResolverStore,
      ProblemPostProcessor problemPostProcessor,
      List<AdviceMvcInspector> adviceMvcInspectors) {
    this.problemResolverStore = problemResolverStore;
    this.problemPostProcessor = problemPostProcessor;
    this.adviceMvcInspectors = adviceMvcInspectors;
  }

  /**
   * <b>Note:</b> Although {@link HttpHeaders#writableHttpHeaders(HttpHeaders)} is deprecated, it is
   * used here for backward compatibility with older Spring Framework versions.
   *
   * <p>The deprecation alternative provided by Spring is not available in versions {@code 6.0.*}
   * and {@code 6.1.*} (Spring Framework versions, not Spring Boot). Therefore, this method is
   * retained to ensure compatibility across those versions.
   */
  @Override
  protected ResponseEntity<Object> handleExceptionInternal(
      Exception ex, Object body, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    ProblemContext context = (ProblemContext) request.getAttribute(PROBLEM_CONTEXT, SCOPE_REQUEST);
    if (context == null) {
      context = ProblemContext.empty();
    }

    headers = headers != null ? HttpHeaders.writableHttpHeaders(headers) : new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

    Problem problem;
    try {
      problem = getBuilderForOverridingBody(context, ex, headers, status).build();
      problem = problemPostProcessor.process(context, problem);
    } catch (Exception e) {
      logAdviceException(log, ex, request, e);
      problem = Problem.builder().status(ProblemStatus.INTERNAL_SERVER_ERROR).build();
    }

    status = resolveStatus(problem);

    for (AdviceMvcInspector inspector : adviceMvcInspectors) {
      inspector.inspect(context, problem, ex, headers, status, request);
    }

    return super.handleExceptionInternal(ex, problem, headers, status, request);
  }

  /**
   * Returns a {@link ProblemBuilder} for the given exception, using a resolver if available, or a
   * fallback otherwise.
   *
   * @param context the problem context
   * @param ex the exception to resolve
   * @param headers the HTTP headers
   * @param status the HTTP status code
   * @return a {@link ProblemBuilder} for the exception
   */
  protected ProblemBuilder getBuilderForOverridingBody(
      ProblemContext context, Exception ex, HttpHeaders headers, HttpStatusCode status) {
    return problemResolverStore
        .findResolver(ex.getClass())
        .map(resolver -> resolver.resolveBuilder(context, ex, headers, status))
        .orElseGet(() -> fallbackProblem(status));
  }

  /**
   * Returns a fallback {@link ProblemBuilder} with the given status.
   *
   * @param status the HTTP status code
   * @return a fallback {@link ProblemBuilder}
   */
  protected ProblemBuilder fallbackProblem(HttpStatusCode status) {
    return Problem.builder().status(resolveStatus(status));
  }
}
