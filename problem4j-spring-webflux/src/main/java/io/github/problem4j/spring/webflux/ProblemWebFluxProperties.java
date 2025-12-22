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
package io.github.problem4j.spring.webflux;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Configuration properties for Problem Details WebFlux integration.
 *
 * <p>These properties can be set under the {@code problem4j.webflux.*} prefix.
 */
@ConfigurationProperties(prefix = "problem4j.webflux")
public class ProblemWebFluxProperties {

  private final boolean enabled;

  private final ExceptionAdvice exceptionAdvice;
  private final ProblemExceptionAdvice problemExceptionAdvice;
  private final ProblemContextFilter problemContextFilter;
  private final ExceptionHandler exceptionHandler;
  private final ErrorWebExceptionHandler errorWebExceptionHandler;

  /**
   * Creates a new instance.
   *
   * @param enabled whether Problem4J integration with WebFlux is enabled
   * @param exceptionAdvice configuration for {@link ExceptionWebFluxAdvice}
   * @param problemExceptionAdvice configuration for {@link ProblemExceptionWebFluxAdvice}
   * @param problemContextFilter configuration for {@code ProblemContextWebFluxFilter}
   * @param exceptionHandler configuration for {@link ProblemEnhancedWebFluxHandler}
   * @param errorWebExceptionHandler configuration for {@code ProblemErrorWebExceptionHandler}
   * @see io.github.problem4j.spring.webflux.context.ProblemContextWebFluxFilter
   * @see io.github.problem4j.spring.webflux.error.ProblemErrorWebExceptionHandler
   */
  public ProblemWebFluxProperties(
      @DefaultValue("true") boolean enabled,
      ExceptionAdvice exceptionAdvice,
      ProblemExceptionAdvice problemExceptionAdvice,
      ProblemContextFilter problemContextFilter,
      ExceptionHandler exceptionHandler,
      ErrorWebExceptionHandler errorWebExceptionHandler) {
    this.enabled = enabled;
    this.exceptionAdvice =
        exceptionAdvice != null ? exceptionAdvice : ExceptionAdvice.createDefault();
    this.problemExceptionAdvice =
        problemExceptionAdvice != null
            ? problemExceptionAdvice
            : ProblemExceptionAdvice.createDefault();
    this.problemContextFilter =
        problemContextFilter != null ? problemContextFilter : ProblemContextFilter.createDefault();
    this.exceptionHandler =
        exceptionHandler != null ? exceptionHandler : ExceptionHandler.createDefault();
    this.errorWebExceptionHandler =
        errorWebExceptionHandler != null
            ? errorWebExceptionHandler
            : ErrorWebExceptionHandler.createDefault();
  }

  /**
   * Indicates whether Problem4J integration with WebFlux is currently enabled.
   *
   * @return {@code true} if problem WebFlux is enabled; {@code false} otherwise
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * Returns configuration for {@link ExceptionWebFluxAdvice}, which handles general exceptions and
   * converts them to {@code Problem} responses.
   *
   * @return the configuration for exception advice
   */
  public ExceptionAdvice getExceptionAdvice() {
    return exceptionAdvice;
  }

  /**
   * Returns configuration for {@link ProblemExceptionWebFluxAdvice}, which handles exceptions of
   * type {@code ProblemException} and converts them to Problem responses.
   *
   * @return the configuration for problem exception advice
   */
  public ProblemExceptionAdvice getProblemExceptionAdvice() {
    return problemExceptionAdvice;
  }

  /**
   * Returns configuration for {@code ProblemContextWebFluxFilter}, which enriches request handling
   * with {@code ProblemContext}.
   *
   * @return the configuration for the {@code ProblemContextWebFluxFilter}
   * @see io.github.problem4j.core.ProblemContext
   * @see io.github.problem4j.spring.webflux.context.ProblemContextWebFluxFilter
   */
  public ProblemContextFilter getProblemContextFilter() {
    return problemContextFilter;
  }

  /**
   * Returns configuration for {@link ProblemEnhancedWebFluxHandler} replacement, which allows
   * Problem4J to take control of exception handling normally performed by Spring’s {@code
   * ResponseEntityExceptionHandler}.
   *
   * @return the configuration for the overwritten exception handler
   * @see org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler
   */
  public ExceptionHandler getExceptionHandler() {
    return exceptionHandler;
  }

  /**
   * Returns configuration for {@code ProblemErrorWebExceptionHandler} replacement, which allows
   * Problem4J to take control of exception handling normally performed by Spring’s {@code
   * ErrorWebExceptionHandler}.
   *
   * @return the configuration for the overwritten error handler
   * @see org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
   * @see io.github.problem4j.spring.webflux.error.ProblemErrorWebExceptionHandler
   */
  public ErrorWebExceptionHandler getErrorWebExceptionHandler() {
    return errorWebExceptionHandler;
  }

  /**
   * Configuration group for {@link ExceptionWebFluxAdvice}.
   *
   * <p>Controlled by the property {@code problem4j.webflux.exception-advice.enabled}.
   */
  public static class ExceptionAdvice {

    public static final boolean DEFAULT_ENABLED = true;
    public static final String DEFAULT_ENABLED_VALUE = "true";

    private static ExceptionAdvice createDefault() {
      return new ExceptionAdvice(DEFAULT_ENABLED);
    }

    private final boolean enabled;

    /**
     * Creates a new configuration group.
     *
     * @param enabled whether the {@link ExceptionWebFluxAdvice} bean should be created
     */
    public ExceptionAdvice(@DefaultValue(DEFAULT_ENABLED_VALUE) boolean enabled) {
      this.enabled = enabled;
    }

    /**
     * Returns whether {@link ExceptionWebFluxAdvice} should be registered.
     *
     * @return {@code true} if exception advice is enabled, otherwise {@code false}
     */
    public boolean isEnabled() {
      return enabled;
    }
  }

  /**
   * Configuration group for {@link ProblemExceptionWebFluxAdvice}.
   *
   * <p>Controlled by the property {@code problem4j.webflux.problem-exception-advice.enabled}.
   */
  public static class ProblemExceptionAdvice {

    public static final boolean DEFAULT_ENABLED = true;
    public static final String DEFAULT_ENABLED_VALUE = "true";

    private static ProblemExceptionAdvice createDefault() {
      return new ProblemExceptionAdvice(DEFAULT_ENABLED);
    }

    private final boolean enabled;

    /**
     * Creates a new configuration group.
     *
     * @param enabled whether the {@link ProblemExceptionWebFluxAdvice} bean should be created
     */
    public ProblemExceptionAdvice(@DefaultValue(DEFAULT_ENABLED_VALUE) boolean enabled) {
      this.enabled = enabled;
    }

    /**
     * Returns whether {@link ProblemExceptionWebFluxAdvice} should be registered.
     *
     * @return {@code true} if the ProblemException advice is enabled, otherwise {@code false}
     */
    public boolean isEnabled() {
      return enabled;
    }
  }

  /**
   * Configuration group for {@code ProblemContextWebFluxFilter}.
   *
   * <p>Controlled by the property {@code problem4j.webflux.problem-context-filter.enabled}.
   *
   * @see io.github.problem4j.spring.webflux.context.ProblemContextWebFluxFilter
   */
  public static class ProblemContextFilter {

    public static final boolean DEFAULT_ENABLED = true;
    public static final String DEFAULT_ENABLED_VALUE = "true";

    private static ProblemContextFilter createDefault() {
      return new ProblemContextFilter(DEFAULT_ENABLED);
    }

    private final boolean enabled;

    /**
     * Creates a new configuration group.
     *
     * @param enabled whether the {@code ProblemContextWebFluxFilter} bean should be created
     * @see io.github.problem4j.spring.webflux.context.ProblemContextWebFluxFilter
     */
    public ProblemContextFilter(@DefaultValue(DEFAULT_ENABLED_VALUE) boolean enabled) {
      this.enabled = enabled;
    }

    /**
     * Returns whether {@code ProblemContextWebFluxFilter} should be registered.
     *
     * @return {@code true} if the context filter is enabled, otherwise {@code false}
     * @see io.github.problem4j.spring.webflux.context.ProblemContextWebFluxFilter
     */
    public boolean isEnabled() {
      return enabled;
    }
  }

  /**
   * Configuration group for {@link ProblemEnhancedWebFluxHandler} override.
   *
   * <p>Controlled by the property {@code problem4j.webflux.exception-handler.enabled}.
   */
  public static class ExceptionHandler {

    public static final boolean DEFAULT_ENABLED = true;
    public static final String DEFAULT_ENABLED_VALUE = "true";

    private static ExceptionHandler createDefault() {
      return new ExceptionHandler(DEFAULT_ENABLED);
    }

    private final boolean enabled;

    /**
     * Creates a new configuration group.
     *
     * @param enabled whether the {@code ResponseEntityExceptionHandler} should be replaced with
     *     {@link ProblemEnhancedWebFluxHandler}
     * @see org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler
     */
    public ExceptionHandler(@DefaultValue(DEFAULT_ENABLED_VALUE) boolean enabled) {
      this.enabled = enabled;
    }

    /**
     * Returns whether {@link ProblemEnhancedWebFluxHandler} should be registered.
     *
     * @return {@code true} if the overwritten exception handler is enabled, otherwise {@code false}
     */
    public boolean isEnabled() {
      return enabled;
    }
  }

  /**
   * Configuration group for {@code ProblemErrorWebExceptionHandler} override.
   *
   * <p>Controlled by the property {@code problem4j.webflux.error-web-exception-handler.enabled}.
   *
   * @see org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
   * @see io.github.problem4j.spring.webflux.error.ProblemErrorWebExceptionHandler
   * @see io.github.problem4j.spring.webflux.error.ProblemErrorWebFluxConfiguration
   */
  public static class ErrorWebExceptionHandler {

    public static final boolean DEFAULT_ENABLED = true;
    public static final String DEFAULT_ENABLED_VALUE = "true";

    private static ErrorWebExceptionHandler createDefault() {
      return new ErrorWebExceptionHandler(DEFAULT_ENABLED);
    }

    private final boolean enabled;

    /**
     * Creates a new configuration group.
     *
     * @param enabled whether the {@code ErrorWebExceptionHandler} should be replaced with {@code
     *     ProblemErrorWebExceptionHandler}
     * @see org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
     * @see io.github.problem4j.spring.webflux.error.ProblemErrorWebExceptionHandler
     */
    public ErrorWebExceptionHandler(@DefaultValue(DEFAULT_ENABLED_VALUE) boolean enabled) {
      this.enabled = enabled;
    }

    /**
     * Returns whether {@code ProblemErrorWebExceptionHandler} should be registered.
     *
     * @return {@code true} if the overwritten exception handler is enabled, otherwise {@code false}
     * @see io.github.problem4j.spring.webflux.error.ProblemErrorWebExceptionHandler
     */
    public boolean isEnabled() {
      return enabled;
    }
  }
}
