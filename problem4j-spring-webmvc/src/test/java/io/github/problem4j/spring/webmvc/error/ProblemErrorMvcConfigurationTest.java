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
package io.github.problem4j.spring.webmvc.error;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.problem4j.spring.webmvc.ProblemMvcProperties;
import io.github.problem4j.spring.webmvc.app.MvcTestApp;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.error.ErrorController;

class ProblemErrorMvcConfigurationTest {

  @SpringBootTest(classes = {MvcTestApp.class})
  @Nested
  class WithEnabled {

    @Autowired(required = false)
    private ProblemErrorMvcConfiguration problemErrorMvcConfiguration;

    @Autowired private ErrorController errorController;

    @Autowired private ProblemMvcProperties properties;

    @Test
    void contextLoads() {
      assertThat(problemErrorMvcConfiguration).isNotNull();
      assertThat(errorController).isInstanceOf(ProblemErrorController.class);
      assertThat(properties.getErrorController().isEnabled()).isTrue();
    }
  }

  @SpringBootTest(
      classes = {MvcTestApp.class},
      properties = {"problem4j.webmvc.error-controller.enabled=false"})
  @Nested
  class WithDisabled {

    @Autowired(required = false)
    private ProblemErrorMvcConfiguration problemErrorMvcConfiguration;

    @Autowired private ErrorController errorController;

    @Autowired private ProblemMvcProperties properties;

    @Test
    void contextLoads() {
      assertThat(problemErrorMvcConfiguration).isNull();
      assertThat(errorController).isNotInstanceOf(ProblemErrorController.class);
      assertThat(properties.getErrorController().isEnabled()).isFalse();
    }
  }
}
