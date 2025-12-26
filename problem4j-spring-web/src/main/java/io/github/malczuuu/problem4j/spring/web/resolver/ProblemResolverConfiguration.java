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

import io.github.malczuuu.problem4j.spring.web.format.ProblemFormat;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.server.MissingRequestValueException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerErrorException;
import org.springframework.web.server.ServerWebInputException;

/**
 * Spring configuration for registering {@link ProblemResolver} beans for {@code spring-web}
 * library. Modules {@code problem4j-spring-webflux} and {@code problem4j-spring-webmvc} provide
 * additional {@link Configuration} classes with more resolvers, that originate from other Spring
 * libraries.
 *
 * <p>Each nested configuration class is annotated with {@link ConditionalOnClass} to ensure that
 * only resolvers for classes present on the classpath are created. This design allows the library
 * to remain compatible previous versions.
 *
 * @deprecated migrated to {@code io.github.problem4j:problem4j-spring-web} namespace.
 */
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.ANY)
@Configuration(proxyBeanMethods = false)
@Deprecated(since = "2.0.7")
public class ProblemResolverConfiguration {

  /**
   * @deprecated migrated to {@code io.github.problem4j:problem4j-spring-web} namespace.
   */
  @ConditionalOnClass(BindException.class)
  @Configuration(proxyBeanMethods = false)
  @Deprecated
  public static class BindProblemConfiguration {
    @ConditionalOnMissingBean(BindProblemResolver.class)
    @Bean
    public BindProblemResolver bindProblemResolver(ProblemFormat problemFormat) {
      return new BindProblemResolver(problemFormat);
    }
  }

  /**
   * @deprecated migrated to {@code io.github.problem4j:problem4j-spring-web} namespace.
   */
  @ConditionalOnClass(ConstraintViolationException.class)
  @Configuration(proxyBeanMethods = false)
  @Deprecated
  public static class ConstraintViolationProblemConfiguration {
    @ConditionalOnMissingBean(ConstraintViolationProblemResolver.class)
    @Bean
    public ConstraintViolationProblemResolver constraintViolationProblemResolver(
        ProblemFormat problemFormat) {
      return new ConstraintViolationProblemResolver(problemFormat);
    }
  }

  /**
   * @deprecated migrated to {@code io.github.problem4j:problem4j-spring-web} namespace.
   */
  @ConditionalOnClass(DecodingException.class)
  @Configuration(proxyBeanMethods = false)
  @Deprecated
  public static class DecodingProblemConfiguration {
    @ConditionalOnMissingBean(DecodingProblemResolver.class)
    @Bean
    public DecodingProblemResolver decodingProblemResolver(ProblemFormat problemFormat) {
      return new DecodingProblemResolver(problemFormat);
    }
  }

  /**
   * @deprecated migrated to {@code io.github.problem4j:problem4j-spring-web} namespace.
   */
  @ConditionalOnClass(ErrorResponseException.class)
  @Configuration(proxyBeanMethods = false)
  @Deprecated
  public static class ErrorResponseProblemConfiguration {
    @ConditionalOnMissingBean(ErrorResponseProblemResolver.class)
    @Bean
    public ErrorResponseProblemResolver errorResponseProblemResolver(ProblemFormat problemFormat) {
      return new ErrorResponseProblemResolver(problemFormat);
    }
  }

  /**
   * @deprecated migrated to {@code io.github.problem4j:problem4j-spring-web} namespace.
   */
  @ConditionalOnClass(HandlerMethodValidationException.class)
  @Configuration(proxyBeanMethods = false)
  @Deprecated
  public static class HandlerMethodValidationProblemConfiguration {
    @ConditionalOnMissingBean(HandlerMethodValidationProblemResolver.class)
    @Bean
    public HandlerMethodValidationProblemResolver handlerMethodValidationProblemResolver(
        ProblemFormat problemFormat) {
      return new HandlerMethodValidationProblemResolver(problemFormat);
    }
  }

  /**
   * @deprecated migrated to {@code io.github.problem4j:problem4j-spring-web} namespace.
   */
  @ConditionalOnClass(HttpMediaTypeNotAcceptableException.class)
  @Configuration(proxyBeanMethods = false)
  @Deprecated
  public static class HttpMediaTypeNotAcceptableProblemConfiguration {
    @ConditionalOnMissingBean(HttpMediaTypeNotAcceptableProblemResolver.class)
    @Bean
    public HttpMediaTypeNotAcceptableProblemResolver httpMediaTypeNotAcceptableProblemResolver(
        ProblemFormat problemFormat) {
      return new HttpMediaTypeNotAcceptableProblemResolver(problemFormat);
    }
  }

  /**
   * @deprecated migrated to {@code io.github.problem4j:problem4j-spring-web} namespace.
   */
  @ConditionalOnClass(HttpMediaTypeNotSupportedException.class)
  @Configuration(proxyBeanMethods = false)
  @Deprecated
  public static class HttpMediaTypeNotSupportedProblemConfiguration {
    @ConditionalOnMissingBean(HttpMediaTypeNotSupportedProblemResolver.class)
    @Bean
    public HttpMediaTypeNotSupportedProblemResolver httpMediaTypeNotSupportedProblemResolver(
        ProblemFormat problemFormat) {
      return new HttpMediaTypeNotSupportedProblemResolver(problemFormat);
    }
  }

  /**
   * @deprecated migrated to {@code io.github.problem4j:problem4j-spring-web} namespace.
   */
  @ConditionalOnClass(HttpMessageNotReadableException.class)
  @Configuration(proxyBeanMethods = false)
  @Deprecated
  public static class HttpMessageNotReadableProblemConfiguration {
    @ConditionalOnMissingBean(HttpMessageNotReadableProblemResolver.class)
    @Bean
    public HttpMessageNotReadableProblemResolver httpMessageNotReadableProblemResolver(
        ProblemFormat problemFormat) {
      return new HttpMessageNotReadableProblemResolver(problemFormat);
    }
  }

  /**
   * @deprecated migrated to {@code io.github.problem4j:problem4j-spring-web} namespace.
   */
  @ConditionalOnClass(HttpRequestMethodNotSupportedException.class)
  @Configuration(proxyBeanMethods = false)
  @Deprecated
  public static class HttpRequestMethodNotSupportedProblemConfiguration {
    @ConditionalOnMissingBean(HttpRequestMethodNotSupportedProblemResolver.class)
    @Bean
    public HttpRequestMethodNotSupportedProblemResolver
        httpRequestMethodNotSupportedProblemResolver(ProblemFormat problemFormat) {
      return new HttpRequestMethodNotSupportedProblemResolver(problemFormat);
    }
  }

  /**
   * @deprecated migrated to {@code io.github.problem4j:problem4j-spring-web} namespace.
   */
  @ConditionalOnClass(MaxUploadSizeExceededException.class)
  @Configuration(proxyBeanMethods = false)
  @Deprecated
  public static class MaxUploadSizeExceededProblemConfiguration {
    @ConditionalOnMissingBean(MaxUploadSizeExceededProblemResolver.class)
    @Bean
    public MaxUploadSizeExceededProblemResolver maxUploadSizeExceededProblemResolver(
        ProblemFormat problemFormat) {
      return new MaxUploadSizeExceededProblemResolver(problemFormat);
    }
  }

  /**
   * @deprecated migrated to {@code io.github.problem4j:problem4j-spring-web} namespace.
   */
  @ConditionalOnClass(MethodValidationException.class)
  @Configuration(proxyBeanMethods = false)
  @Deprecated
  public static class MethodValidationProblemConfiguration {
    @ConditionalOnMissingBean(MethodValidationProblemResolver.class)
    @Bean
    public MethodValidationProblemResolver methodValidationProblemResolver(
        ProblemFormat problemFormat) {
      return new MethodValidationProblemResolver(problemFormat);
    }
  }

  /**
   * @deprecated migrated to {@code io.github.problem4j:problem4j-spring-web} namespace.
   */
  @ConditionalOnClass(MissingRequestValueException.class)
  @Configuration(proxyBeanMethods = false)
  @Deprecated
  public static class MissingRequestValueProblemConfiguration {
    @ConditionalOnMissingBean(MissingRequestValueProblemResolver.class)
    @Bean
    public MissingRequestValueProblemResolver missingRequestValueProblemResolver(
        ProblemFormat problemFormat) {
      return new MissingRequestValueProblemResolver(problemFormat);
    }
  }

  /**
   * @deprecated migrated to {@code io.github.problem4j:problem4j-spring-web} namespace.
   */
  @ConditionalOnClass(MissingServletRequestPartException.class)
  @Configuration(proxyBeanMethods = false)
  @Deprecated
  public static class MissingServletRequestPartProblemConfiguration {
    @ConditionalOnMissingBean(MissingServletRequestPartProblemResolver.class)
    @Bean
    public MissingServletRequestPartProblemResolver missingServletRequestPartProblemResolver(
        ProblemFormat problemFormat) {
      return new MissingServletRequestPartProblemResolver(problemFormat);
    }
  }

  /**
   * @deprecated migrated to {@code io.github.problem4j:problem4j-spring-web} namespace.
   */
  @ConditionalOnClass(MultipartException.class)
  @Configuration(proxyBeanMethods = false)
  @Deprecated
  public static class MultipartProblemConfiguration {
    @ConditionalOnMissingBean(MultipartProblemResolver.class)
    @Bean
    public MultipartProblemResolver multipartProblemResolver(ProblemFormat problemFormat) {
      return new MultipartProblemResolver(problemFormat);
    }
  }

  /**
   * @deprecated migrated to {@code io.github.problem4j:problem4j-spring-web} namespace.
   */
  @ConditionalOnClass(ResponseStatusException.class)
  @Configuration(proxyBeanMethods = false)
  @Deprecated
  public static class ResponseStatusProblemConfiguration {
    @ConditionalOnMissingBean(ResponseStatusProblemResolver.class)
    @Bean
    public ResponseStatusProblemResolver responseStatusProblemResolver(
        ProblemFormat problemFormat) {
      return new ResponseStatusProblemResolver(problemFormat);
    }
  }

  /**
   * @deprecated migrated to {@code io.github.problem4j:problem4j-spring-web} namespace.
   */
  @ConditionalOnClass(ServerErrorException.class)
  @Configuration(proxyBeanMethods = false)
  @Deprecated
  public static class ServerErrorProblemConfiguration {
    @ConditionalOnMissingBean(ServerErrorProblemResolver.class)
    @Bean
    public ServerErrorProblemResolver serverErrorProblemResolver(ProblemFormat problemFormat) {
      return new ServerErrorProblemResolver(problemFormat);
    }
  }

  /**
   * @deprecated migrated to {@code io.github.problem4j:problem4j-spring-web} namespace.
   */
  @ConditionalOnClass(ServerWebInputException.class)
  @Configuration(proxyBeanMethods = false)
  @Deprecated
  public static class ServerWebInputProblemConfiguration {
    @ConditionalOnMissingBean(ServerWebInputProblemResolver.class)
    @Bean
    public ServerWebInputProblemResolver serverWebInputProblemResolver(
        ProblemFormat problemFormat) {
      return new ServerWebInputProblemResolver(problemFormat);
    }
  }

  /**
   * @deprecated migrated to {@code io.github.problem4j:problem4j-spring-web} namespace.
   */
  @ConditionalOnClass(ServletRequestBindingException.class)
  @Configuration(proxyBeanMethods = false)
  @Deprecated
  public static class ServletRequestBindingProblemConfiguration {
    @ConditionalOnMissingBean(ServletRequestBindingProblemResolver.class)
    @Bean
    public ServletRequestBindingProblemResolver servletRequestBindingProblemResolver(
        ProblemFormat problemFormat) {
      return new ServletRequestBindingProblemResolver(problemFormat);
    }
  }

  /**
   * @deprecated migrated to {@code io.github.problem4j:problem4j-spring-web} namespace.
   */
  @ConditionalOnClass(TypeMismatchException.class)
  @Configuration(proxyBeanMethods = false)
  @Deprecated
  public static class TypeMismatchProblemConfiguration {
    @ConditionalOnMissingBean(TypeMismatchProblemResolver.class)
    @Bean
    public TypeMismatchProblemResolver typeMismatchProblemResolver(ProblemFormat problemFormat) {
      return new TypeMismatchProblemResolver(problemFormat);
    }
  }

  /**
   * @deprecated migrated to {@code io.github.problem4j:problem4j-spring-web} namespace.
   */
  @ConditionalOnClass(WebExchangeBindException.class)
  @Configuration(proxyBeanMethods = false)
  @Deprecated
  public static class WebExchangeBindProblemConfiguration {
    @ConditionalOnMissingBean(WebExchangeBindProblemResolver.class)
    @Bean
    public WebExchangeBindProblemResolver webExchangeBindProblemResolver(
        ProblemFormat problemFormat) {
      return new WebExchangeBindProblemResolver(problemFormat);
    }
  }
}
