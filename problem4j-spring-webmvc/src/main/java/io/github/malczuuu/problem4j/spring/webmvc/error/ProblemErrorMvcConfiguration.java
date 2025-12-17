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
package io.github.malczuuu.problem4j.spring.webmvc.error;

import io.github.malczuuu.problem4j.spring.web.processor.ProblemPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures MVC error handling to return {@code application/problem+json} responses according to
 * RFC 7807.
 *
 * <p>This setup replaces Spring Boot’s default error controller {@code ErrorMvcAutoConfiguration}
 * with {@link ProblemErrorController}, which renders {@code Problem} objects instead of HTML or
 * plain JSON errors.
 *
 * @see org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration
 */
@ConditionalOnProperty(name = "problem4j.webmvc.error-controller.enabled", matchIfMissing = true)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(ErrorController.class)
@Configuration(proxyBeanMethods = false)
public class ProblemErrorMvcConfiguration {

  /**
   * Registers a default {@link ErrorAttributes} bean if none exists.
   *
   * <p>Used to expose error details to the {@link ProblemErrorController}.
   *
   * @return a default {@link DefaultErrorAttributes} instance
   */
  @ConditionalOnMissingBean(ErrorAttributes.class)
  @Bean
  public ErrorAttributes errorAttributes() {
    return new DefaultErrorAttributes();
  }

  /**
   * Registers a custom {@link ErrorController} that renders Problem JSON responses.
   *
   * <p>Replaces the default error controller when no other implementation is present.
   *
   * @param errorAttributes provides error information for requests
   * @return a new {@link ProblemErrorController} instance
   * @see org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration
   */
  @ConditionalOnMissingBean(ErrorController.class)
  @Bean
  public ErrorController errorController(
      ProblemPostProcessor problemPostProcessor, ErrorAttributes errorAttributes) {
    return new ProblemErrorController(problemPostProcessor, errorAttributes);
  }
}
