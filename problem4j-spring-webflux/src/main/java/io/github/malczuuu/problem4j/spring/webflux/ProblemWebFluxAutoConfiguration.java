package io.github.malczuuu.problem4j.spring.webflux;

import io.github.malczuuu.problem4j.spring.web.ProblemConfiguration;
import io.github.malczuuu.problem4j.spring.web.ProblemProperties;
import io.github.malczuuu.problem4j.spring.web.ProblemResolverStore;
import io.github.malczuuu.problem4j.spring.web.annotation.ProblemMappingProcessor;
import io.github.malczuuu.problem4j.spring.web.resolver.ConstraintViolationResolver;
import io.github.malczuuu.problem4j.spring.webflux.error.ProblemErrorWebFluxConfiguration;
import io.github.malczuuu.problem4j.spring.webflux.resolver.ProblemResolverWebFluxConfiguration;
import io.github.malczuuu.problem4j.spring.webflux.tracing.TraceIdWebFluxFilter;
import jakarta.validation.ConstraintViolationException;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration;
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
 * io.github.malczuuu.problem4j.core.Problem} responses from Spring WebFlux controllers. It
 * includes:
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
@AutoConfigureBefore({ErrorWebFluxAutoConfiguration.class, WebFluxAutoConfiguration.class})
@Import({
  ProblemErrorWebFluxConfiguration.class,
  ProblemResolverWebFluxConfiguration.class,
  ProblemConfiguration.class
})
public class ProblemWebFluxAutoConfiguration {

  @Order(Ordered.LOWEST_PRECEDENCE - 10)
  @ConditionalOnMissingBean(ResponseEntityExceptionHandler.class)
  @Bean
  public ResponseEntityExceptionHandler responseEntityExceptionHandler(
      ProblemResolverStore problemResolverStore) {
    return new ProblemEnhancedWebFluxHandler(problemResolverStore);
  }

  @Order(Ordered.LOWEST_PRECEDENCE)
  @ConditionalOnMissingBean(ExceptionWebFluxAdvice.class)
  @Bean
  public ExceptionWebFluxAdvice exceptionWebFluxAdvice(
      ProblemMappingProcessor problemMappingProcessor, ProblemResolverStore problemResolverStore) {
    return new ExceptionWebFluxAdvice(problemMappingProcessor, problemResolverStore);
  }

  @Order(Ordered.LOWEST_PRECEDENCE - 10)
  @ConditionalOnMissingBean(ProblemExceptionWebFluxAdvice.class)
  @Bean
  public ProblemExceptionWebFluxAdvice problemExceptionWebFluxAdvice() {
    return new ProblemExceptionWebFluxAdvice();
  }

  @ConditionalOnClass(ConstraintViolationException.class)
  @Configuration(proxyBeanMethods = false)
  public static class ConstraintViolationExceptionAdviceConfiguration {

    @Order(Ordered.LOWEST_PRECEDENCE - 10)
    @ConditionalOnMissingBean(ConstraintViolationExceptionWebFluxAdvice.class)
    @Bean
    public ConstraintViolationExceptionWebFluxAdvice constraintViolationExceptionWebFluxAdvice(
        ConstraintViolationResolver constraintViolationResolver) {
      return new ConstraintViolationExceptionWebFluxAdvice(constraintViolationResolver);
    }
  }

  @ConditionalOnProperty(name = "problem4j.tracing-header-name")
  @ConditionalOnMissingBean(TraceIdWebFluxFilter.class)
  @Bean
  public TraceIdWebFluxFilter traceIdWebFluxFilter(ProblemProperties problemProperties) {
    return new TraceIdWebFluxFilter(
        problemProperties.getTracingHeaderName(), problemProperties.getInstanceOverride());
  }

  @ConditionalOnClass(DecodingException.class)
  @Configuration(proxyBeanMethods = false)
  public static class DecodingExceptionAdviceConfiguration {

    @Order(Ordered.LOWEST_PRECEDENCE - 10)
    @ConditionalOnMissingBean(DecodingExceptionWebFluxAdvice.class)
    @Bean
    public DecodingExceptionWebFluxAdvice decodingExceptionWebFluxAdvice() {
      return new DecodingExceptionWebFluxAdvice();
    }
  }
}
