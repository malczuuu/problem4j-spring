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
package io.github.problem4j.spring.webflux.error;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.problem4j.spring.webflux.ProblemWebFluxProperties;
import io.github.problem4j.spring.webflux.app.WebFluxTestApp;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;

class ProblemErrorWebFluxConfigurationTest {

  @SpringBootTest(classes = {WebFluxTestApp.class})
  @Nested
  class WithEnabled {

    @Autowired(required = false)
    private ProblemErrorWebFluxConfiguration problemErrorWebFluxConfiguration;

    @Autowired private ErrorWebExceptionHandler errorWebExceptionHandler;

    @Autowired private ProblemWebFluxProperties properties;

    @Test
    void contextLoads() {
      assertThat(problemErrorWebFluxConfiguration).isNotNull();
      assertThat(errorWebExceptionHandler).isInstanceOf(ProblemErrorWebExceptionHandler.class);
      assertThat(properties.getErrorWebExceptionHandler().isEnabled()).isTrue();
    }
  }

  @SpringBootTest(
      classes = {WebFluxTestApp.class},
      properties = {"problem4j.webflux.error-web-exception-handler.enabled=false"})
  @Nested
  class WithDisabled {

    @Autowired(required = false)
    private ProblemErrorWebFluxConfiguration problemErrorWebFluxConfiguration;

    @Autowired private ErrorWebExceptionHandler errorWebExceptionHandler;

    @Autowired private ProblemWebFluxProperties properties;

    @Test
    void contextLoads() {
      assertThat(problemErrorWebFluxConfiguration).isNull();
      assertThat(errorWebExceptionHandler).isNotInstanceOf(ProblemErrorWebExceptionHandler.class);
      assertThat(properties.getErrorWebExceptionHandler().isEnabled()).isFalse();
    }
  }
}
