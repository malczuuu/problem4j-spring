package io.github.malczuuu.problem4j.spring.webflux;

import io.github.malczuuu.problem4j.spring.web.ExceptionMappingStore;
import io.github.malczuuu.problem4j.spring.web.ProblemConfiguration;
import io.github.malczuuu.problem4j.spring.web.ProblemProperties;
import io.github.malczuuu.problem4j.spring.web.annotation.ProblemMappingProcessor;
import io.github.malczuuu.problem4j.spring.web.mapping.ConstraintViolationMapping;
import io.github.malczuuu.problem4j.spring.webflux.mapping.ExceptionMappingFluxConfiguration;
import jakarta.validation.ConstraintViolationException;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.codec.DecodingException;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

/**
 * Spring Boot autoconfiguration for problem-based exception handling in {@code spring-webflux}
 * applications.
 *
 * <p>This class wires all necessary beans for producing standardized {@link
 * io.github.malczuuu.problem4j.core.Problem} responses from Spring MVC controllers. It includes:
 *
 * <ul>
 *   <li>Exception handling beans such as {@link ProblemEnhancedFluxHandler}, {@link
 *       ProblemExceptionFluxAdvice}, and {@link ExceptionFluxAdvice}.
 * </ul>
 *
 * <p>Beans are conditional:
 *
 * <ul>
 *   <li>{@link ConditionalOnMissingBean} ensures user-defined beans override defaults.
 *   <li>{@link ConditionalOnClass} ensures compatibility with optional framework classes.
 * </ul>
 *
 * <p>The configuration also imports ({@link ProblemConfiguration}) from {@code commons} library.
 */
@ConditionalOnClass(ResponseEntityExceptionHandler.class)
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(WebFluxAutoConfiguration.class)
@Import({ExceptionMappingFluxConfiguration.class, ProblemConfiguration.class})
public class ProblemFluxAutoConfiguration {

  @Order(Ordered.LOWEST_PRECEDENCE - 10)
  @ConditionalOnMissingBean(ResponseEntityExceptionHandler.class)
  @Bean
  public ResponseEntityExceptionHandler responseEntityExceptionHandler(
      ExceptionMappingStore exceptionMappingStore, ProblemProperties problemProperties) {
    return new ProblemEnhancedFluxHandler(
        exceptionMappingStore, problemProperties.getInstanceOverride());
  }

  @Order(Ordered.LOWEST_PRECEDENCE)
  @ConditionalOnMissingBean(ExceptionFluxAdvice.class)
  @Bean
  public ExceptionFluxAdvice exceptionAdvice(
      ProblemMappingProcessor problemMappingProcessor, ProblemProperties problemProperties) {
    return new ExceptionFluxAdvice(
        problemMappingProcessor, problemProperties.getInstanceOverride());
  }

  @Order(Ordered.LOWEST_PRECEDENCE - 10)
  @ConditionalOnMissingBean(ProblemExceptionFluxAdvice.class)
  @Bean
  public ProblemExceptionFluxAdvice problemExceptionAdvice(ProblemProperties problemProperties) {
    return new ProblemExceptionFluxAdvice(problemProperties.getInstanceOverride());
  }

  @ConditionalOnClass(ConstraintViolationException.class)
  @Configuration(proxyBeanMethods = false)
  public static class ConstraintViolationExceptionAdviceConfiguration {

    @Order(Ordered.LOWEST_PRECEDENCE - 10)
    @ConditionalOnMissingBean(ConstraintViolationExceptionFluxAdvice.class)
    @Bean
    public ConstraintViolationExceptionFluxAdvice constraintViolationExceptionWebMvcAdvice(
        ConstraintViolationMapping constraintViolationMapping,
        ProblemProperties problemProperties) {
      return new ConstraintViolationExceptionFluxAdvice(
          constraintViolationMapping, problemProperties.getInstanceOverride());
    }
  }

  @ConditionalOnProperty(name = "problem4j.tracing-header-name")
  @ConditionalOnMissingBean(TraceIdFluxFilter.class)
  @Bean
  public TraceIdFluxFilter traceIdFluxFilter(ProblemProperties problemProperties) {
    return new TraceIdFluxFilter(problemProperties.getTracingHeaderName());
  }

  @ConditionalOnClass(DecodingException.class)
  @Configuration(proxyBeanMethods = false)
  public static class DecodingExceptionAdviceConfiguration {

    @Order(Ordered.LOWEST_PRECEDENCE - 10)
    @ConditionalOnMissingBean(DecodingExceptionFluxAdvice.class)
    @Bean
    public DecodingExceptionFluxAdvice decodingExceptionFluxAdvice(
        ProblemProperties problemProperties) {
      return new DecodingExceptionFluxAdvice(problemProperties.getInstanceOverride());
    }
  }
}
