package io.github.malczuuu.problem4j.spring.web.mapping;

import io.github.malczuuu.problem4j.spring.web.format.DetailFormat;
import io.github.malczuuu.problem4j.spring.web.format.PropertyNameFormat;
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
 *
 * <p>Some mappings optionally use {@link DetailFormat} or {@link PropertyNameFormat} to produce
 * consistent, detailed error responses.
 */
@Configuration(proxyBeanMethods = false)
public class ExceptionMappingConfiguration {

  @ConditionalOnClass(ConstraintViolationException.class)
  @Configuration(proxyBeanMethods = false)
  public static class ConstraintViolationConfiguration {
    @Bean
    public ConstraintViolationMapping constraintViolationMapping(
        DetailFormat detailFormat, PropertyNameFormat propertyNameFormat) {
      return new ConstraintViolationMapping(detailFormat, propertyNameFormat);
    }
  }

  @ConditionalOnClass(ErrorResponseException.class)
  @Configuration(proxyBeanMethods = false)
  public static class ErrorResponseConfiguration {
    @Bean
    public ErrorResponseMapping errorResponseMapping() {
      return new ErrorResponseMapping();
    }
  }

  @ConditionalOnClass(HandlerMethodValidationException.class)
  @Configuration(proxyBeanMethods = false)
  public static class HandlerMethodValidationConfiguration {
    @Bean
    public HandlerMethodValidationMapping handlerMethodValidationMapping() {
      return new HandlerMethodValidationMapping();
    }
  }

  @ConditionalOnClass(HttpMediaTypeNotAcceptableException.class)
  @Configuration(proxyBeanMethods = false)
  public static class HttpMediaTypeNotAcceptableConfiguration {
    @Bean
    public HttpMediaTypeNotAcceptableMapping httpMediaTypeNotAcceptableMapping() {
      return new HttpMediaTypeNotAcceptableMapping();
    }
  }

  @ConditionalOnClass(HttpMediaTypeNotSupportedException.class)
  @Configuration(proxyBeanMethods = false)
  public static class HttpMediaTypeNotSupportedConfiguration {
    @Bean
    public HttpMediaTypeNotSupportedMapping httpMediaTypeNotSupportedMapping() {
      return new HttpMediaTypeNotSupportedMapping();
    }
  }

  @ConditionalOnClass(HttpMessageNotReadableException.class)
  @Configuration(proxyBeanMethods = false)
  public static class HttpMessageNotReadableConfiguration {
    @Bean
    public HttpMessageNotReadableMapping httpMessageNotReadableMapping() {
      return new HttpMessageNotReadableMapping();
    }
  }

  @ConditionalOnClass(HttpRequestMethodNotSupportedException.class)
  @Configuration(proxyBeanMethods = false)
  public static class HttpRequestMethodNotSupportedConfiguration {
    @Bean
    public HttpRequestMethodNotSupportedMapping httpRequestMethodNotSupportedMapping() {
      return new HttpRequestMethodNotSupportedMapping();
    }
  }

  @ConditionalOnClass(MaxUploadSizeExceededException.class)
  @Configuration(proxyBeanMethods = false)
  public static class MaxUploadSizeExceededConfiguration {
    @Bean
    public MaxUploadSizeExceededMapping maxUploadSizeExceededMapping(DetailFormat detailFormat) {
      return new MaxUploadSizeExceededMapping(detailFormat);
    }
  }

  @ConditionalOnClass(MethodArgumentNotValidException.class)
  @Configuration(proxyBeanMethods = false)
  public static class MethodArgumentNotValidConfiguration {
    @Bean
    public MethodArgumentNotValidMapping methodArgumentNotValidMapping(
        DetailFormat detailFormat, PropertyNameFormat propertyNameFormat) {
      return new MethodArgumentNotValidMapping(detailFormat, propertyNameFormat);
    }
  }

  @ConditionalOnClass(MethodValidationException.class)
  @Configuration(proxyBeanMethods = false)
  public static class MethodValidationConfiguration {
    @Bean
    public MethodValidationMapping methodValidationMapping(DetailFormat detailFormat) {
      return new MethodValidationMapping(detailFormat);
    }
  }

  @ConditionalOnClass(MissingRequestValueException.class)
  @Configuration(proxyBeanMethods = false)
  public static class MissingRequestValueConfiguration {
    @Bean
    public MissingRequestValueMapping missingRequestValueMapping(DetailFormat detailFormat) {
      return new MissingRequestValueMapping(detailFormat);
    }
  }

  @ConditionalOnClass(MissingServletRequestPartException.class)
  @Configuration(proxyBeanMethods = false)
  public static class MissingServletRequestPartConfiguration {
    @Bean
    public MissingServletRequestPartMapping missingServletRequestPartMapping(
        DetailFormat detailFormat) {
      return new MissingServletRequestPartMapping(detailFormat);
    }
  }

  @ConditionalOnClass(ResponseStatusException.class)
  @Configuration(proxyBeanMethods = false)
  public static class ResponseStatusConfiguration {
    @Bean
    public ResponseStatusMapping responseStatusMapping() {
      return new ResponseStatusMapping();
    }
  }

  @ConditionalOnClass(ServerErrorException.class)
  @Configuration(proxyBeanMethods = false)
  public static class ServerErrorConfiguration {
    @Bean
    public ServerErrorMapping serverErrorMapping(DetailFormat detailFormat) {
      return new ServerErrorMapping(detailFormat);
    }
  }

  @ConditionalOnClass(ServerWebInputException.class)
  @Configuration(proxyBeanMethods = false)
  public static class ServerWebInputConfiguration {
    @Bean
    public ServerWebInputMapping serverWebInputMapping(DetailFormat detailFormat) {
      return new ServerWebInputMapping(detailFormat);
    }
  }

  @ConditionalOnClass(ServletRequestBindingException.class)
  @Configuration(proxyBeanMethods = false)
  public static class ServletRequestBindingConfiguration {
    @Bean
    public ServletRequestBindingMapping servletRequestBindingMapping(DetailFormat detailFormat) {
      return new ServletRequestBindingMapping(detailFormat);
    }
  }

  @ConditionalOnClass(TypeMismatchException.class)
  @Configuration(proxyBeanMethods = false)
  public static class TypeMismatchConfiguration {
    @Bean
    public TypeMismatchMapping typeMismatchMapping(DetailFormat detailFormat) {
      return new TypeMismatchMapping(detailFormat);
    }
  }

  @ConditionalOnClass(WebExchangeBindException.class)
  @Configuration(proxyBeanMethods = false)
  public static class WebExchangeBindConfiguration {
    @Bean
    public WebExchangeBindMapping webExchangeBindMapping(
        DetailFormat detailFormat, PropertyNameFormat propertyNameFormat) {
      return new WebExchangeBindMapping(detailFormat, propertyNameFormat);
    }
  }
}
