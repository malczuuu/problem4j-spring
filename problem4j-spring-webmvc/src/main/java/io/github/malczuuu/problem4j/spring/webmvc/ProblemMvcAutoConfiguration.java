package io.github.malczuuu.problem4j.spring.webmvc;

import io.github.malczuuu.problem4j.spring.web.ExceptionMappingStore;
import io.github.malczuuu.problem4j.spring.web.ProblemConfiguration;
import io.github.malczuuu.problem4j.spring.web.ProblemProperties;
import io.github.malczuuu.problem4j.spring.web.annotation.ProblemMappingProcessor;
import io.github.malczuuu.problem4j.spring.web.mapping.ConstraintViolationMapping;
import io.github.malczuuu.problem4j.spring.webmvc.error.ProblemErrorMvcConfiguration;
import io.github.malczuuu.problem4j.spring.webmvc.mapping.ExceptionMappingMvcConfiguration;
import io.github.malczuuu.problem4j.spring.webmvc.tracing.TraceIdMvcFilter;
import jakarta.validation.ConstraintViolationException;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Spring Boot autoconfiguration for problem-based exception handling in {@code spring-webflux}
 * applications.
 *
 * <p>This class wires all necessary beans for producing standardized {@link
 * io.github.malczuuu.problem4j.core.Problem} responses from Spring MVC controllers. It includes:
 *
 * <p>Beans are conditional:
 *
 * <ul>
 *   <li>{@link ConditionalOnMissingBean} ensures user-defined beans override defaults.
 *   <li>{@link ConditionalOnClass} ensures compatibility with optional framework classes.
 * </ul>
 */
@ConditionalOnClass(ResponseEntityExceptionHandler.class)
@AutoConfiguration
@AutoConfigureBefore({ErrorMvcAutoConfiguration.class, WebMvcAutoConfiguration.class})
@Import({
  ProblemErrorMvcConfiguration.class,
  ExceptionMappingMvcConfiguration.class,
  ProblemConfiguration.class
})
public class ProblemMvcAutoConfiguration {

  @Order(Ordered.LOWEST_PRECEDENCE - 10)
  @ConditionalOnMissingBean(ResponseEntityExceptionHandler.class)
  @Bean
  public ResponseEntityExceptionHandler responseEntityExceptionHandler(
      ExceptionMappingStore exceptionMappingStore, ProblemProperties problemProperties) {
    return new ProblemEnhancedMvcHandler(exceptionMappingStore);
  }

  @Order(Ordered.LOWEST_PRECEDENCE)
  @ConditionalOnMissingBean(ExceptionMvcAdvice.class)
  @Bean
  public ExceptionMvcAdvice exceptionAdvice(
      ProblemMappingProcessor problemMappingProcessor, ProblemProperties problemProperties) {
    return new ExceptionMvcAdvice(problemMappingProcessor);
  }

  @Order(Ordered.LOWEST_PRECEDENCE - 10)
  @ConditionalOnMissingBean(ProblemExceptionMvcAdvice.class)
  @Bean
  public ProblemExceptionMvcAdvice problemExceptionAdvice() {
    return new ProblemExceptionMvcAdvice();
  }

  @ConditionalOnProperty(name = "problem4j.tracing-header-name")
  @ConditionalOnMissingBean(TraceIdMvcFilter.class)
  @Bean
  public TraceIdMvcFilter traceIdMvcFilter(ProblemProperties properties) {
    return new TraceIdMvcFilter(
        properties.getTracingHeaderName(), properties.getInstanceOverride());
  }

  @ConditionalOnClass(ConstraintViolationException.class)
  @Configuration(proxyBeanMethods = false)
  public static class ConstraintViolationExceptionAdviceConfiguration {

    @Order(Ordered.LOWEST_PRECEDENCE - 10)
    @ConditionalOnMissingBean(ConstraintViolationExceptionMvcAdvice.class)
    @Bean
    public ConstraintViolationExceptionMvcAdvice constraintViolationExceptionWebMvcAdvice(
        ConstraintViolationMapping constraintViolationMapping,
        ProblemProperties problemProperties) {
      return new ConstraintViolationExceptionMvcAdvice(constraintViolationMapping);
    }
  }
}
