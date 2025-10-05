package io.github.malczuuu.problem4j.spring.web.mapping;

import io.github.malczuuu.problem4j.spring.web.format.ProblemFormat;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.server.MissingRequestValueException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerErrorException;
import org.springframework.web.server.ServerWebInputException;

/**
 * Spring configuration for registering {@link ExceptionMapping} beans for {@code spring-web}
 * library. Modules {@code problem4j-spring-webflux} and {@code problem4j-spring-webmvc} provide
 * additional {@code @Configuration} classes with more mappings, that don't originate from {@code
 * spring-web}.
 *
 * <p>Each nested configuration class is annotated with {@link ConditionalOnClass} to ensure that
 * only mappings for classes present on the classpath are created. This design allows the library to
 * remain compatible previous versions.
 */
@Configuration(proxyBeanMethods = false)
public class ExceptionMappingConfiguration {

  @ConditionalOnClass(ConstraintViolationException.class)
  @Configuration(proxyBeanMethods = false)
  public static class ConstraintViolationConfiguration {
    @Bean
    public ConstraintViolationMapping constraintViolationMapping(ProblemFormat problemFormat) {
      return new ConstraintViolationMapping(problemFormat);
    }
  }

  @ConditionalOnClass(ErrorResponseException.class)
  @Configuration(proxyBeanMethods = false)
  public static class ErrorResponseConfiguration {
    @Bean
    public ErrorResponseMapping errorResponseMapping(ProblemFormat problemFormat) {
      return new ErrorResponseMapping(problemFormat);
    }
  }

  @ConditionalOnClass(HandlerMethodValidationException.class)
  @Configuration(proxyBeanMethods = false)
  public static class HandlerMethodValidationConfiguration {
    @Bean
    public HandlerMethodValidationMapping handlerMethodValidationMapping(
        ProblemFormat problemFormat) {
      return new HandlerMethodValidationMapping(problemFormat);
    }
  }

  @ConditionalOnClass(HttpMediaTypeNotAcceptableException.class)
  @Configuration(proxyBeanMethods = false)
  public static class HttpMediaTypeNotAcceptableConfiguration {
    @Bean
    public HttpMediaTypeNotAcceptableMapping httpMediaTypeNotAcceptableMapping(
        ProblemFormat problemFormat) {
      return new HttpMediaTypeNotAcceptableMapping(problemFormat);
    }
  }

  @ConditionalOnClass(HttpMediaTypeNotSupportedException.class)
  @Configuration(proxyBeanMethods = false)
  public static class HttpMediaTypeNotSupportedConfiguration {
    @Bean
    public HttpMediaTypeNotSupportedMapping httpMediaTypeNotSupportedMapping(
        ProblemFormat problemFormat) {
      return new HttpMediaTypeNotSupportedMapping(problemFormat);
    }
  }

  @ConditionalOnClass(HttpMessageNotReadableException.class)
  @Configuration(proxyBeanMethods = false)
  public static class HttpMessageNotReadableConfiguration {
    @Bean
    public HttpMessageNotReadableMapping httpMessageNotReadableMapping(
        ProblemFormat problemFormat) {
      return new HttpMessageNotReadableMapping(problemFormat);
    }
  }

  @ConditionalOnClass(HttpRequestMethodNotSupportedException.class)
  @Configuration(proxyBeanMethods = false)
  public static class HttpRequestMethodNotSupportedConfiguration {
    @Bean
    public HttpRequestMethodNotSupportedMapping httpRequestMethodNotSupportedMapping(
        ProblemFormat problemFormat) {
      return new HttpRequestMethodNotSupportedMapping(problemFormat);
    }
  }

  @ConditionalOnClass(MaxUploadSizeExceededException.class)
  @Configuration(proxyBeanMethods = false)
  public static class MaxUploadSizeExceededConfiguration {
    @Bean
    public MaxUploadSizeExceededMapping maxUploadSizeExceededMapping(ProblemFormat problemFormat) {
      return new MaxUploadSizeExceededMapping(problemFormat);
    }
  }

  @ConditionalOnClass(MethodArgumentNotValidException.class)
  @Configuration(proxyBeanMethods = false)
  public static class MethodArgumentNotValidConfiguration {
    @Bean
    public MethodArgumentNotValidMapping methodArgumentNotValidMapping(
        ProblemFormat problemFormat) {
      return new MethodArgumentNotValidMapping(problemFormat);
    }
  }

  @ConditionalOnClass(MethodValidationException.class)
  @Configuration(proxyBeanMethods = false)
  public static class MethodValidationConfiguration {
    @Bean
    public MethodValidationMapping methodValidationMapping(ProblemFormat problemFormat) {
      return new MethodValidationMapping(problemFormat);
    }
  }

  @ConditionalOnClass(MissingRequestValueException.class)
  @Configuration(proxyBeanMethods = false)
  public static class MissingRequestValueConfiguration {
    @Bean
    public MissingRequestValueMapping missingRequestValueMapping(ProblemFormat problemFormat) {
      return new MissingRequestValueMapping(problemFormat);
    }
  }

  @ConditionalOnClass(MissingServletRequestPartException.class)
  @Configuration(proxyBeanMethods = false)
  public static class MissingServletRequestPartConfiguration {
    @Bean
    public MissingServletRequestPartMapping missingServletRequestPartMapping(
        ProblemFormat problemFormat) {
      return new MissingServletRequestPartMapping(problemFormat);
    }
  }

  @ConditionalOnClass(ResponseStatusException.class)
  @Configuration(proxyBeanMethods = false)
  public static class ResponseStatusConfiguration {
    @Bean
    public ResponseStatusMapping responseStatusMapping(ProblemFormat problemFormat) {
      return new ResponseStatusMapping(problemFormat);
    }
  }

  @ConditionalOnClass(ServerErrorException.class)
  @Configuration(proxyBeanMethods = false)
  public static class ServerErrorConfiguration {
    @Bean
    public ServerErrorMapping serverErrorMapping(ProblemFormat problemFormat) {
      return new ServerErrorMapping(problemFormat);
    }
  }

  @ConditionalOnClass(ServerWebInputException.class)
  @Configuration(proxyBeanMethods = false)
  public static class ServerWebInputConfiguration {
    @Bean
    public ServerWebInputMapping serverWebInputMapping(ProblemFormat problemFormat) {
      return new ServerWebInputMapping(problemFormat);
    }
  }

  @ConditionalOnClass(ServletRequestBindingException.class)
  @Configuration(proxyBeanMethods = false)
  public static class ServletRequestBindingConfiguration {
    @Bean
    public ServletRequestBindingMapping servletRequestBindingMapping(ProblemFormat problemFormat) {
      return new ServletRequestBindingMapping(problemFormat);
    }
  }

  @ConditionalOnClass(TypeMismatchException.class)
  @Configuration(proxyBeanMethods = false)
  public static class TypeMismatchConfiguration {
    @Bean
    public TypeMismatchMapping typeMismatchMapping(ProblemFormat problemFormat) {
      return new TypeMismatchMapping(problemFormat);
    }
  }

  @ConditionalOnClass(WebExchangeBindException.class)
  @Configuration(proxyBeanMethods = false)
  public static class WebExchangeBindConfiguration {
    @Bean
    public WebExchangeBindMapping webExchangeBindMapping(ProblemFormat problemFormat) {
      return new WebExchangeBindMapping(problemFormat);
    }
  }
}
