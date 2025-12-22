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
package io.github.problem4j.spring.webmvc;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.problem4j.spring.web.DefaultProblemResolverStore;
import io.github.problem4j.spring.web.annotation.DefaultProblemMappingProcessor;
import io.github.problem4j.spring.web.processor.IdentityProblemPostProcessor;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.ServletWebRequest;

class ExceptionMvcAdviceTest {

  private ExceptionMvcAdvice advice;

  private AtomicInteger hits;

  @BeforeEach
  void beforeEach() {
    hits = new AtomicInteger(0);
    advice =
        new ExceptionMvcAdvice(
            new DefaultProblemMappingProcessor(),
            new DefaultProblemResolverStore(List.of()),
            new IdentityProblemPostProcessor(),
            List.of((context, problem, ex, headers, status, exchange) -> hits.incrementAndGet()));
  }

  @Test
  void whileHandlingException_shouldHitInspector() {
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/test");
    MockHttpServletResponse response = new MockHttpServletResponse();

    advice.handleException(
        new ConstraintViolationException("message", Set.of()),
        new ServletWebRequest(request, response));

    assertThat(hits.get()).isEqualTo(1);
  }
}
