package io.github.malczuuu.problem4j.spring.web.resolver;

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
 * Spring configuration for registering {@link ProblemResolver} beans for {@code spring-web}
 * library. Modules {@code problem4j-spring-webflux} and {@code problem4j-spring-webmvc} provide
 * additional {@link Configuration} classes with more resolvers, that originate from other Spring
 * libraries.
 *
 * <p>Each nested configuration class is annotated with {@link ConditionalOnClass} to ensure that
 * only resolvers for classes present on the classpath are created. This design allows the library
 * to remain compatible previous versions.
 */
@Configuration(proxyBeanMethods = false)
public class ProblemResolverConfiguration {

  @ConditionalOnClass(ConstraintViolationException.class)
  @Configuration(proxyBeanMethods = false)
  public static class ConstraintViolationConfiguration {
    @Bean
    public ConstraintViolationResolver constraintViolationResolver(ProblemFormat problemFormat) {
      return new ConstraintViolationResolver(problemFormat);
    }
  }

  @ConditionalOnClass(ErrorResponseException.class)
  @Configuration(proxyBeanMethods = false)
  public static class ErrorResponseConfiguration {
    @Bean
    public ErrorResponseResolver errorResponseResolver(ProblemFormat problemFormat) {
      return new ErrorResponseResolver(problemFormat);
    }
  }

  @ConditionalOnClass(HandlerMethodValidationException.class)
  @Configuration(proxyBeanMethods = false)
  public static class HandlerMethodValidationConfiguration {
    @Bean
    public HandlerMethodValidationResolver handlerMethodValidationResolver(
        ProblemFormat problemFormat) {
      return new HandlerMethodValidationResolver(problemFormat);
    }
  }

  @ConditionalOnClass(HttpMediaTypeNotAcceptableException.class)
  @Configuration(proxyBeanMethods = false)
  public static class HttpMediaTypeNotAcceptableConfiguration {
    @Bean
    public HttpMediaTypeNotAcceptableResolver httpMediaTypeNotAcceptableResolver(
        ProblemFormat problemFormat) {
      return new HttpMediaTypeNotAcceptableResolver(problemFormat);
    }
  }

  @ConditionalOnClass(HttpMediaTypeNotSupportedException.class)
  @Configuration(proxyBeanMethods = false)
  public static class HttpMediaTypeNotSupportedConfiguration {
    @Bean
    public HttpMediaTypeNotSupportedResolver httpMediaTypeNotSupportedResolver(
        ProblemFormat problemFormat) {
      return new HttpMediaTypeNotSupportedResolver(problemFormat);
    }
  }

  @ConditionalOnClass(HttpMessageNotReadableException.class)
  @Configuration(proxyBeanMethods = false)
  public static class HttpMessageNotReadableConfiguration {
    @Bean
    public HttpMessageNotReadableResolver httpMessageNotReadableResolver(
        ProblemFormat problemFormat) {
      return new HttpMessageNotReadableResolver(problemFormat);
    }
  }

  @ConditionalOnClass(HttpRequestMethodNotSupportedException.class)
  @Configuration(proxyBeanMethods = false)
  public static class HttpRequestMethodNotSupportedConfiguration {
    @Bean
    public HttpRequestMethodNotSupportedResolver httpRequestMethodNotSupportedResolver(
        ProblemFormat problemFormat) {
      return new HttpRequestMethodNotSupportedResolver(problemFormat);
    }
  }

  @ConditionalOnClass(MaxUploadSizeExceededException.class)
  @Configuration(proxyBeanMethods = false)
  public static class MaxUploadSizeExceededConfiguration {
    @Bean
    public MaxUploadSizeExceededResolver maxUploadSizeExceededResolver(
        ProblemFormat problemFormat) {
      return new MaxUploadSizeExceededResolver(problemFormat);
    }
  }

  @ConditionalOnClass(MethodArgumentNotValidException.class)
  @Configuration(proxyBeanMethods = false)
  public static class MethodArgumentNotValidConfiguration {
    @Bean
    public MethodArgumentNotValidResolver methodArgumentNotValidResolver(
        ProblemFormat problemFormat) {
      return new MethodArgumentNotValidResolver(problemFormat);
    }
  }

  @ConditionalOnClass(MethodValidationException.class)
  @Configuration(proxyBeanMethods = false)
  public static class MethodValidationConfiguration {
    @Bean
    public MethodValidationResolver methodValidationResolver(ProblemFormat problemFormat) {
      return new MethodValidationResolver(problemFormat);
    }
  }

  @ConditionalOnClass(MissingRequestValueException.class)
  @Configuration(proxyBeanMethods = false)
  public static class MissingRequestValueConfiguration {
    @Bean
    public MissingRequestValueResolver missingRequestValueResolver(ProblemFormat problemFormat) {
      return new MissingRequestValueResolver(problemFormat);
    }
  }

  @ConditionalOnClass(MissingServletRequestPartException.class)
  @Configuration(proxyBeanMethods = false)
  public static class MissingServletRequestPartConfiguration {
    @Bean
    public MissingServletRequestPartResolver missingServletRequestPartResolver(
        ProblemFormat problemFormat) {
      return new MissingServletRequestPartResolver(problemFormat);
    }
  }

  @ConditionalOnClass(ResponseStatusException.class)
  @Configuration(proxyBeanMethods = false)
  public static class ResponseStatusConfiguration {
    @Bean
    public ResponseStatusResolver responseStatusResolver(ProblemFormat problemFormat) {
      return new ResponseStatusResolver(problemFormat);
    }
  }

  @ConditionalOnClass(ServerErrorException.class)
  @Configuration(proxyBeanMethods = false)
  public static class ServerErrorConfiguration {
    @Bean
    public ServerErrorResolver serverErrorResolver(ProblemFormat problemFormat) {
      return new ServerErrorResolver(problemFormat);
    }
  }

  @ConditionalOnClass(ServerWebInputException.class)
  @Configuration(proxyBeanMethods = false)
  public static class ServerWebInputConfiguration {
    @Bean
    public ServerWebInputResolver serverWebInputResolver(ProblemFormat problemFormat) {
      return new ServerWebInputResolver(problemFormat);
    }
  }

  @ConditionalOnClass(ServletRequestBindingException.class)
  @Configuration(proxyBeanMethods = false)
  public static class ServletRequestBindingConfiguration {
    @Bean
    public ServletRequestBindingResolver servletRequestBindingResolver(
        ProblemFormat problemFormat) {
      return new ServletRequestBindingResolver(problemFormat);
    }
  }

  @ConditionalOnClass(TypeMismatchException.class)
  @Configuration(proxyBeanMethods = false)
  public static class TypeMismatchConfiguration {
    @Bean
    public TypeMismatchResolver typeMismatchResolver(ProblemFormat problemFormat) {
      return new TypeMismatchResolver(problemFormat);
    }
  }

  @ConditionalOnClass(WebExchangeBindException.class)
  @Configuration(proxyBeanMethods = false)
  public static class WebExchangeBindConfiguration {
    @Bean
    public WebExchangeBindResolver webExchangeBindResolver(ProblemFormat problemFormat) {
      return new WebExchangeBindResolver(problemFormat);
    }
  }
}
