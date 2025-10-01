package io.github.malczuuu.problem4j.spring.web.mapping;

import io.github.malczuuu.problem4j.spring.web.formatting.DetailFormatting;
import io.github.malczuuu.problem4j.spring.web.formatting.FieldNameFormatting;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Spring configuration for registering {@link ExceptionMapping} beans.
 *
 * <p>This configuration class provides a modular setup where each Spring-handled exception gets its
 * own dedicated {@link ExceptionMapping} bean.
 *
 * <p>Each nested configuration class is annotated with {@link ConditionalOnClass} to ensure that
 * only mappings for classes present on the classpath are created. This design allows the library to
 * remain compatible with multiple versions of Spring Web MVC.
 *
 * <p>Some mappings optionally use {@link
 * io.github.malczuuu.problem4j.spring.web.formatting.DetailFormatting} or {@link
 * io.github.malczuuu.problem4j.spring.web.formatting.FieldNameFormatting} to produce consistent,
 * detailed error responses.
 */
@Configuration(proxyBeanMethods = false)
public class ExceptionMappingConfiguration {

  @ConditionalOnClass(ErrorResponseException.class)
  @Configuration(proxyBeanMethods = false)
  public static class AsyncRequestTimeoutConfiguration {

    @Bean
    AsyncRequestTimeoutMapping asyncRequestTimeoutMapping() {
      return new AsyncRequestTimeoutMapping();
    }
  }

  @ConditionalOnClass(ConversionNotSupportedException.class)
  @Configuration(proxyBeanMethods = false)
  public static class ConversionNotSupportedConfiguration {

    @Bean
    ConversionNotSupportedMapping conversionNotSupportedMapping() {
      return new ConversionNotSupportedMapping();
    }
  }

  @ConditionalOnClass(ErrorResponseException.class)
  @Configuration(proxyBeanMethods = false)
  public static class ErrorResponseConfiguration {

    @Bean
    ErrorResponseMapping errorResponseMapping() {
      return new ErrorResponseMapping();
    }
  }

  @ConditionalOnClass(HandlerMethodValidationException.class)
  @Configuration(proxyBeanMethods = false)
  public static class HandlerMethodValidationConfiguration {

    @Bean
    HandlerMethodValidationMapping handlerMethodValidationMapping() {
      return new HandlerMethodValidationMapping();
    }
  }

  @ConditionalOnClass(HttpMediaTypeNotAcceptableException.class)
  @Configuration(proxyBeanMethods = false)
  public static class HttpMediaTypeNotAcceptableConfiguration {

    @Bean
    HttpMediaTypeNotAcceptableMapping httpMediaTypeNotAcceptableMapping() {
      return new HttpMediaTypeNotAcceptableMapping();
    }
  }

  @ConditionalOnClass(HttpMediaTypeNotSupportedException.class)
  @Configuration(proxyBeanMethods = false)
  public static class HttpMediaTypeNotSupportedConfiguration {

    @Bean
    HttpMediaTypeNotSupportedMapping httpMediaTypeNotSupportedMapping() {
      return new HttpMediaTypeNotSupportedMapping();
    }
  }

  @ConditionalOnClass(HttpMessageNotReadableException.class)
  @Configuration(proxyBeanMethods = false)
  public static class HttpMessageNotReadableConfiguration {

    @Bean
    HttpMessageNotReadableMapping httpMessageNotReadableMapping() {
      return new HttpMessageNotReadableMapping();
    }
  }

  @ConditionalOnClass(HttpMessageNotWritableException.class)
  @Configuration(proxyBeanMethods = false)
  public static class HttpMessageNotWritableConfiguration {

    @Bean
    HttpMessageNotWritableMapping httpMessageNotWritableMapping() {
      return new HttpMessageNotWritableMapping();
    }
  }

  @ConditionalOnClass(HttpRequestMethodNotSupportedException.class)
  @Configuration(proxyBeanMethods = false)
  public static class HttpRequestMethodNotSupportedConfiguration {

    @Bean
    HttpRequestMethodNotSupportedMapping httpRequestMethodNotSupportedMapping() {
      return new HttpRequestMethodNotSupportedMapping();
    }
  }

  @ConditionalOnClass(MaxUploadSizeExceededException.class)
  @Configuration(proxyBeanMethods = false)
  public static class MaxUploadSizeExceededConfiguration {

    @Bean
    MaxUploadSizeExceededMapping maxUploadSizeExceededMapping(DetailFormatting detailFormatting) {
      return new MaxUploadSizeExceededMapping(detailFormatting);
    }
  }

  @ConditionalOnClass(MethodArgumentNotValidException.class)
  @Configuration(proxyBeanMethods = false)
  public static class MethodArgumentNotValidConfiguration {

    @Bean
    MethodArgumentNotValidMapping methodArgumentNotValidMapping(
        DetailFormatting detailFormatting, FieldNameFormatting fieldNameFormatting) {
      return new MethodArgumentNotValidMapping(detailFormatting, fieldNameFormatting);
    }
  }

  @ConditionalOnClass(MethodValidationException.class)
  @Configuration(proxyBeanMethods = false)
  public static class MethodValidationConfiguration {

    @Bean
    MethodValidationMapping methodValidationMapping() {
      return new MethodValidationMapping();
    }
  }

  @ConditionalOnClass(MissingPathVariableException.class)
  @Configuration(proxyBeanMethods = false)
  public static class MissingPathVariableConfiguration {

    @Bean
    MissingPathVariableMapping missingPathVariableMapping(DetailFormatting detailFormatting) {
      return new MissingPathVariableMapping(detailFormatting);
    }
  }

  @ConditionalOnClass(MissingServletRequestParameterException.class)
  @Configuration(proxyBeanMethods = false)
  public static class MissingServletRequestParameterConfiguration {

    @Bean
    MissingServletRequestParameterMapping missingServletRequestParameterMapping(
        DetailFormatting detailFormatting) {
      return new MissingServletRequestParameterMapping(detailFormatting);
    }
  }

  @ConditionalOnClass(MissingServletRequestPartException.class)
  @Configuration(proxyBeanMethods = false)
  public static class MissingServletRequestPartConfiguration {

    @Bean
    MissingServletRequestPartMapping missingServletRequestPartMapping(
        DetailFormatting detailFormatting) {
      return new MissingServletRequestPartMapping(detailFormatting);
    }
  }

  @ConditionalOnClass(NoHandlerFoundException.class)
  @Configuration(proxyBeanMethods = false)
  public static class NoHandlerFoundConfiguration {

    @Bean
    NoHandlerFoundMapping noHandlerFoundMapping() {
      return new NoHandlerFoundMapping();
    }
  }

  @ConditionalOnClass(NoResourceFoundException.class)
  @Configuration(proxyBeanMethods = false)
  public static class NoResourceFoundConfiguration {

    @Bean
    NoResourceFoundMapping noResourceFoundMapping() {
      return new NoResourceFoundMapping();
    }
  }

  @ConditionalOnClass(ServletRequestBindingException.class)
  @Configuration(proxyBeanMethods = false)
  public static class ServletRequestBindingConfiguration {

    @Bean
    ServletRequestBindingMapping servletRequestBindingMapping() {
      return new ServletRequestBindingMapping();
    }
  }

  @ConditionalOnClass(TypeMismatchException.class)
  @Configuration(proxyBeanMethods = false)
  public static class TypeMismatchConfiguration {

    @Bean
    TypeMismatchMapping typeMismatchMapping(DetailFormatting detailFormatting) {
      return new TypeMismatchMapping(detailFormatting);
    }
  }
}
