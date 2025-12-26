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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Configuration properties for Problem Details WebMVC integration.
 *
 * <p>These properties can be set under the {@code problem4j.webmvc.*} prefix.
 *
 * @deprecated migrated to {@code io.github.problem4j:problem4j-spring-webmvc} namespace.
 */
@ConfigurationProperties(prefix = "problem4j.webmvc")
@Deprecated(since = "1.1.8")
public class ProblemMvcProperties {

  private final boolean enabled;

  private final ExceptionAdvice exceptionAdvice;
  private final ProblemExceptionAdvice problemExceptionAdvice;
  private final ProblemContextFilter problemContextFilter;
  private final ExceptionHandler exceptionHandler;
  private final ErrorController errorController;

  /**
   * Creates a new instance.
   *
   * @param enabled whether Problem4J integration with WebMVC is enabled
   * @param exceptionAdvice configuration for {@link ExceptionMvcAdvice}
   * @param problemExceptionAdvice configuration for {@link ProblemExceptionMvcAdvice}
   * @param problemContextFilter configuration for {@code ProblemContextMvcFilter}
   * @param exceptionHandler configuration for {@link ProblemEnhancedMvcHandler}
   * @param errorController configuration for {@code ProblemErrorController}
   * @see io.github.malczuuu.problem4j.spring.webmvc.context.ProblemContextMvcFilter
   * @see io.github.malczuuu.problem4j.spring.webmvc.error.ProblemErrorController
   */
  public ProblemMvcProperties(
      @DefaultValue("true") boolean enabled,
      ExceptionAdvice exceptionAdvice,
      ProblemExceptionAdvice problemExceptionAdvice,
      ProblemContextFilter problemContextFilter,
      ExceptionHandler exceptionHandler,
      ErrorController errorController) {
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
    this.errorController =
        errorController != null ? errorController : ErrorController.createDefault();
  }

  /**
   * Indicates whether Problem4J integration with WebMVC is currently enabled.
   *
   * @return {@code true} if problem WebFlux is enabled; {@code false} otherwise
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * Returns configuration for {@link ExceptionMvcAdvice}, which handles general exceptions and
   * converts them to {@code Problem} responses.
   *
   * @return the configuration for exception advice
   */
  public ExceptionAdvice getExceptionAdvice() {
    return exceptionAdvice;
  }

  /**
   * Returns configuration for {@link ProblemExceptionMvcAdvice}, which handles exceptions of type
   * {@code ProblemException} and converts them to Problem responses.
   *
   * @return the configuration for problem exception advice
   */
  public ProblemExceptionAdvice getProblemExceptionAdvice() {
    return problemExceptionAdvice;
  }

  /**
   * Returns configuration for {@code ProblemContextMvcFilter}, which enriches request handling with
   * {@code ProblemContext}.
   *
   * @return the configuration for the {@code ProblemContextMvcFilter}
   * @see io.github.malczuuu.problem4j.spring.webmvc.context.ProblemContextMvcFilter
   * @see io.github.malczuuu.problem4j.spring.web.context.ProblemContext
   */
  public ProblemContextFilter getProblemContextFilter() {
    return problemContextFilter;
  }

  /**
   * Returns configuration for {@link ProblemEnhancedMvcHandler} replacement, which allows Problem4J
   * to take control of exception handling normally performed by Spring’s {@code
   * ResponseEntityExceptionHandler}.
   *
   * @return the configuration for the overwritten exception handler
   * @see org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
   */
  public ExceptionHandler getExceptionHandler() {
    return exceptionHandler;
  }

  /**
   * Returns configuration for {@code ProblemErrorController} replacement, which allows Problem4J to
   * take control of exception handling normally performed by Spring’s {@code ErrorController}.
   *
   * @return the configuration for the overwritten error handler
   * @see org.springframework.boot.web.servlet.error.ErrorController
   * @see io.github.malczuuu.problem4j.spring.webmvc.error.ProblemErrorController
   */
  public ErrorController getErrorController() {
    return errorController;
  }

  /**
   * Configuration group for {@link ExceptionMvcAdvice}.
   *
   * <p>Controlled by the property {@code problem4j.webmvc.exception-advice.enabled}.
   *
   * @deprecated migrated to {@code io.github.problem4j:problem4j-spring-webmvc} namespace.
   */
  @Deprecated
  public static class ExceptionAdvice {

    public static final boolean DEFAULT_ENABLED = true;
    public static final String DEFAULT_ENABLED_VALUE = "true";

    private static ExceptionAdvice createDefault() {
      return new ExceptionAdvice(DEFAULT_ENABLED);
    }

    private final boolean enabled;

    /**
     * Creates a new configuration group for {@link ExceptionMvcAdvice}.
     *
     * @param enabled whether the {@link ExceptionMvcAdvice} bean should be created
     */
    public ExceptionAdvice(@DefaultValue(DEFAULT_ENABLED_VALUE) boolean enabled) {
      this.enabled = enabled;
    }

    /**
     * Returns whether {@link ExceptionMvcAdvice} should be registered.
     *
     * @return {@code true} if exception advice is enabled, otherwise {@code false}
     */
    public boolean isEnabled() {
      return enabled;
    }
  }

  /**
   * Configuration group for {@link ProblemExceptionMvcAdvice}.
   *
   * <p>Controlled by the property {@code problem4j.webmvc.problem-exception-advice.enabled}.
   *
   * @deprecated migrated to {@code io.github.problem4j:problem4j-spring-webmvc} namespace.
   */
  @Deprecated
  public static class ProblemExceptionAdvice {

    public static final boolean DEFAULT_ENABLED = true;
    public static final String DEFAULT_ENABLED_VALUE = "true";

    private static ProblemExceptionAdvice createDefault() {
      return new ProblemExceptionAdvice(DEFAULT_ENABLED);
    }

    private final boolean enabled;

    /**
     * Creates a new configuration group for {@link ProblemExceptionMvcAdvice}.
     *
     * @param enabled whether the {@link ProblemExceptionMvcAdvice} bean should be created
     */
    public ProblemExceptionAdvice(@DefaultValue(DEFAULT_ENABLED_VALUE) boolean enabled) {
      this.enabled = enabled;
    }

    /**
     * Returns whether {@link ProblemExceptionMvcAdvice} should be registered.
     *
     * @return {@code true} if the ProblemException advice is enabled, otherwise {@code false}
     */
    public boolean isEnabled() {
      return enabled;
    }
  }

  /**
   * Configuration group for {@code ProblemContextMvcFilter}.
   *
   * <p>Controlled by the property {@code problem4j.webmvc.problem-context-filter.enabled}.
   *
   * @see io.github.malczuuu.problem4j.spring.webmvc.context.ProblemContextMvcFilter
   * @deprecated migrated to {@code io.github.problem4j:problem4j-spring-webmvc} namespace.
   */
  @Deprecated
  public static class ProblemContextFilter {

    public static final boolean DEFAULT_ENABLED = true;
    public static final String DEFAULT_ENABLED_VALUE = "true";

    private static ProblemContextFilter createDefault() {
      return new ProblemContextFilter(DEFAULT_ENABLED);
    }

    private final boolean enabled;

    /**
     * Creates a new configuration group for {@code ProblemContextMvcFilter}.
     *
     * @param enabled whether the {@code ProblemContextMvcFilter} bean should be created
     * @see io.github.malczuuu.problem4j.spring.webmvc.context.ProblemContextMvcFilter
     */
    public ProblemContextFilter(@DefaultValue(DEFAULT_ENABLED_VALUE) boolean enabled) {
      this.enabled = enabled;
    }

    /**
     * Returns whether {@code ProblemContextMvcFilter} should be registered.
     *
     * @return {@code true} if the context filter is enabled, otherwise {@code false}
     * @see io.github.malczuuu.problem4j.spring.webmvc.context.ProblemContextMvcFilter
     */
    public boolean isEnabled() {
      return enabled;
    }
  }

  /**
   * Configuration group for {@link ProblemEnhancedMvcHandler} override.
   *
   * <p>Controlled by the property {@code problem4j.webmvc.exception-handler.enabled}.
   *
   * @deprecated migrated to {@code io.github.problem4j:problem4j-spring-webmvc} namespace.
   */
  @Deprecated
  public static class ExceptionHandler {

    public static final boolean DEFAULT_ENABLED = true;
    public static final String DEFAULT_ENABLED_VALUE = "true";

    private static ExceptionHandler createDefault() {
      return new ExceptionHandler(DEFAULT_ENABLED);
    }

    private final boolean enabled;

    /**
     * Creates a new configuration group for {@link ProblemEnhancedMvcHandler}.
     *
     * @param enabled whether the {@code ResponseEntityExceptionHandler} should be replaced with
     *     {@link ProblemEnhancedMvcHandler}
     * @see org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
     */
    public ExceptionHandler(@DefaultValue(DEFAULT_ENABLED_VALUE) boolean enabled) {
      this.enabled = enabled;
    }

    /**
     * Returns whether {@link ProblemEnhancedMvcHandler} should be registered.
     *
     * @return {@code true} if the overwritten exception handler is enabled, otherwise {@code false}
     */
    public boolean isEnabled() {
      return enabled;
    }
  }

  /**
   * Configuration group for {@code ProblemErrorController} override.
   *
   * <p>Controlled by the property {@code problem4j.webmvc.error-web-exception-handler.enabled}.
   *
   * @see org.springframework.boot.web.servlet.error.ErrorController
   * @see io.github.malczuuu.problem4j.spring.webmvc.error.ProblemErrorController
   * @see io.github.malczuuu.problem4j.spring.webmvc.error.ProblemErrorMvcConfiguration
   * @deprecated migrated to {@code io.github.problem4j:problem4j-spring-webmvc} namespace.
   */
  @Deprecated
  public static class ErrorController {

    public static final boolean DEFAULT_ENABLED = true;
    public static final String DEFAULT_ENABLED_VALUE = "true";

    private static ErrorController createDefault() {
      return new ErrorController(DEFAULT_ENABLED);
    }

    private final boolean enabled;

    /**
     * Creates a new configuration group.
     *
     * @param enabled whether the {@code ErrorController} should be replaced with {@code
     *     ProblemErrorController}
     * @see org.springframework.boot.web.servlet.error.ErrorController
     * @see io.github.malczuuu.problem4j.spring.webmvc.error.ProblemErrorController
     */
    public ErrorController(@DefaultValue(DEFAULT_ENABLED_VALUE) boolean enabled) {
      this.enabled = enabled;
    }

    /**
     * Returns whether {@code ProblemErrorController} should be registered.
     *
     * @return {@code true} if the overwritten exception handler is enabled, otherwise {@code false}
     * @see io.github.malczuuu.problem4j.spring.webmvc.error.ProblemErrorController
     */
    public boolean isEnabled() {
      return enabled;
    }
  }
}
