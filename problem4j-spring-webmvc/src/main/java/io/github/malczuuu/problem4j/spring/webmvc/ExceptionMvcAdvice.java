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
import io.github.malczuuu.problem4j.spring.web.annotation.ProblemMappingProcessor;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.processor.ProblemPostProcessor;
import io.github.malczuuu.problem4j.spring.web.resolver.ProblemResolver;
import io.github.malczuuu.problem4j.spring.web.util.ProblemSupport;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

/**
 * Fallback exception handler for uncaught {@link Exception}s in Spring REST controllers.
 *
 * <p>This class uses {@link RestControllerAdvice} to intercept any exceptions not handled by more
 * specific exception handlers. It converts them into a standardized {@link Problem} response with:
 *
 * <ul>
 *   <li>HTTP status: {@link HttpStatus#INTERNAL_SERVER_ERROR}
 *   <li>Response body: a {@link Problem} object containing the status code and reason phrase
 *   <li>Content type: {@code application/problem+json}
 * </ul>
 *
 * <p>Intended as a **generic fallback**, it ensures that unexpected exceptions still produce a
 * consistent {@code Problem} response. For more specific exception handling, use {@code
 * ProblemEnhancedMvcHandler}, {@code ProblemExceptionMvcAdvice}.
 *
 * @deprecated migrated to {@code io.github.problem4j:problem4j-spring-webmvc} namespace.
 */
@RestControllerAdvice
@Deprecated(since = "1.1.8")
public class ExceptionMvcAdvice {

  private static final Logger log = LoggerFactory.getLogger(ExceptionMvcAdvice.class);

  private final ProblemMappingProcessor problemMappingProcessor;
  private final ProblemResolverStore problemResolverStore;
  private final ProblemPostProcessor problemPostProcessor;

  private final List<AdviceMvcInspector> adviceMvcInspectors;

  public ExceptionMvcAdvice(
      ProblemMappingProcessor problemMappingProcessor,
      ProblemResolverStore problemResolverStore,
      ProblemPostProcessor problemPostProcessor,
      List<AdviceMvcInspector> adviceMvcInspectors) {
    this.problemMappingProcessor = problemMappingProcessor;
    this.problemResolverStore = problemResolverStore;
    this.problemPostProcessor = problemPostProcessor;
    this.adviceMvcInspectors = adviceMvcInspectors;
  }

  /**
   * Generic fallback handler that converts any uncaught exception into a {@code Problem} response.
   * Chooses a resolver, @ProblemMapping, @ResponseStatus, or defaults to INTERNAL_SERVER_ERROR.
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleException(Exception ex, WebRequest request) {
    ProblemContext context = (ProblemContext) request.getAttribute(PROBLEM_CONTEXT, SCOPE_REQUEST);
    if (context == null) {
      context = ProblemContext.empty();
    }

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

    Problem problem;
    try {
      problem = getProblemBuilder(ex, context, headers).build();
      problem = problemPostProcessor.process(context, problem);
    } catch (Exception e) {
      logAdviceException(log, ex, request, e);
      problem = Problem.builder().status(ProblemStatus.INTERNAL_SERVER_ERROR).build();
    }

    HttpStatus status = ProblemSupport.resolveStatus(problem);

    for (AdviceMvcInspector inspector : adviceMvcInspectors) {
      inspector.inspect(context, problem, ex, headers, status, request);
    }

    return new ResponseEntity<>(problem, headers, status);
  }

  private ProblemBuilder getProblemBuilder(
      Exception ex, ProblemContext context, HttpHeaders headers) {
    ProblemBuilder builder;
    if (problemMappingProcessor.isMappingCandidate(ex)) {
      builder = problemMappingProcessor.toProblemBuilder(ex, context);
    } else {
      Optional<ProblemResolver> optionalResolver = problemResolverStore.findResolver(ex.getClass());

      if (optionalResolver.isPresent()) {
        builder =
            optionalResolver
                .get()
                .resolveBuilder(context, ex, headers, HttpStatus.INTERNAL_SERVER_ERROR);
      } else {
        ResponseStatus responseStatus =
            AnnotatedElementUtils.findMergedAnnotation(ex.getClass(), ResponseStatus.class);
        if (responseStatus != null) {
          builder = Problem.builder().status(resolveStatus(responseStatus.code()));
          if (StringUtils.hasLength(responseStatus.reason())) {
            builder = builder.detail(responseStatus.reason());
          }
        } else {
          builder = Problem.builder().status(ProblemStatus.INTERNAL_SERVER_ERROR);
        }
      }
    }
    return builder;
  }
}
