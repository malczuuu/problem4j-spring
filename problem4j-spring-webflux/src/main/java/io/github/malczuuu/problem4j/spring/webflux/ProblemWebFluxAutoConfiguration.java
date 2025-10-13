package io.github.malczuuu.problem4j.spring.webflux;

import io.github.malczuuu.problem4j.spring.web.ProblemProperties;
import io.github.malczuuu.problem4j.spring.web.ProblemResolverStore;
import io.github.malczuuu.problem4j.spring.web.annotation.ProblemMappingProcessor;
import io.github.malczuuu.problem4j.spring.web.processor.ProblemPostProcessor;
import io.github.malczuuu.problem4j.spring.webflux.context.ProblemContextWebFluxFilter;
import io.github.malczuuu.problem4j.spring.webflux.error.ProblemErrorWebFluxConfiguration;
import io.github.malczuuu.problem4j.spring.webflux.resolver.ProblemResolverWebFluxConfiguration;
import java.util.List;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.server.WebFilter;

/**
 * Spring autoconfiguration for problem-based exception handling in {@code spring-webflux}
 * applications.
 *
 * <p>This class wires all necessary beans for producing standardized {@code Problem} responses from
 * Spring WebFlux controllers. It includes:
 *
 * <p>Beans are conditional:
 *
 * <ul>
 *   <li>{@link ConditionalOnMissingBean} ensures user-defined beans override defaults.
 *   <li>{@link ConditionalOnClass} ensures compatibility with optional framework classes.
 * </ul>
 */
@AutoConfiguration
@EnableConfigurationProperties({ProblemProperties.class})
@ConditionalOnProperty(prefix = "problem4j", name = "enabled", matchIfMissing = true)
@AutoConfigureBefore({ErrorWebFluxAutoConfiguration.class, WebFluxAutoConfiguration.class})
@Import({ProblemErrorWebFluxConfiguration.class, ProblemResolverWebFluxConfiguration.class})
public class ProblemWebFluxAutoConfiguration {

  @Order(Ordered.LOWEST_PRECEDENCE)
  @ConditionalOnMissingBean(ExceptionWebFluxAdvice.class)
  @Bean
  public ExceptionWebFluxAdvice exceptionWebFluxAdvice(
      ProblemMappingProcessor problemMappingProcessor,
      ProblemResolverStore problemResolverStore,
      ProblemPostProcessor problemPostProcessor,
      List<AdviceWebFluxInspector> adviceWebFluxInspectors) {
    return new ExceptionWebFluxAdvice(
        problemMappingProcessor,
        problemResolverStore,
        problemPostProcessor,
        adviceWebFluxInspectors);
  }

  @Order(Ordered.LOWEST_PRECEDENCE - 10)
  @ConditionalOnMissingBean(ProblemExceptionWebFluxAdvice.class)
  @Bean
  public ProblemExceptionWebFluxAdvice problemExceptionWebFluxAdvice(
      ProblemPostProcessor problemPostProcessor,
      List<AdviceWebFluxInspector> adviceWebFluxInspectors) {
    return new ProblemExceptionWebFluxAdvice(problemPostProcessor, adviceWebFluxInspectors);
  }

  @ConditionalOnClass(WebFilter.class)
  @Configuration(proxyBeanMethods = false)
  public static class ProblemContextWebFluxFilterConfiguration {
    @ConditionalOnMissingBean(ProblemContextWebFluxFilter.class)
    @Bean
    public ProblemContextWebFluxFilter problemContextWebFluxFilter(ProblemProperties properties) {
      return new ProblemContextWebFluxFilter(properties);
    }
  }

  @ConditionalOnClass(ResponseEntityExceptionHandler.class)
  @Configuration(proxyBeanMethods = false)
  public static class ResponseEntityExceptionHandlerConfiguration {
    @Order(Ordered.LOWEST_PRECEDENCE - 10)
    @ConditionalOnMissingBean(ResponseEntityExceptionHandler.class)
    @Bean
    public ResponseEntityExceptionHandler responseEntityExceptionHandler(
        ProblemResolverStore problemResolverStore,
        ProblemPostProcessor problemPostProcessor,
        List<AdviceWebFluxInspector> adviceWebFluxInspectors) {
      return new ProblemEnhancedWebFluxHandler(
          problemResolverStore, problemPostProcessor, adviceWebFluxInspectors);
    }
  }
}
