package io.github.malczuuu.problem4j.spring.webmvc;

import io.github.malczuuu.problem4j.spring.web.ProblemProperties;
import io.github.malczuuu.problem4j.spring.web.ProblemResolverStore;
import io.github.malczuuu.problem4j.spring.web.annotation.ProblemMappingProcessor;
import io.github.malczuuu.problem4j.spring.web.processor.ProblemPostProcessor;
import io.github.malczuuu.problem4j.spring.webmvc.context.ProblemContextMvcFilter;
import io.github.malczuuu.problem4j.spring.webmvc.error.ProblemErrorMvcConfiguration;
import io.github.malczuuu.problem4j.spring.webmvc.resolver.ProblemResolverMvcConfiguration;
import java.util.List;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Spring Boot autoconfiguration for problem-based exception handling in {@code spring-webmvc}
 * applications.
 *
 * <p>This class wires all necessary beans for producing standardized {@code Problem} responses from
 * Spring MVC controllers. It includes:
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
@AutoConfigureBefore({ErrorMvcAutoConfiguration.class, WebMvcAutoConfiguration.class})
@Import({ProblemErrorMvcConfiguration.class, ProblemResolverMvcConfiguration.class})
public class ProblemMvcAutoConfiguration {

  @Order(Ordered.LOWEST_PRECEDENCE)
  @ConditionalOnMissingBean(ExceptionMvcAdvice.class)
  @Bean
  public ExceptionMvcAdvice exceptionMvcAdvice(
      ProblemMappingProcessor problemMappingProcessor,
      ProblemResolverStore problemResolverStore,
      ProblemPostProcessor problemPostProcessor,
      List<AdviceMvcInspector> adviceMvcInspectors) {
    return new ExceptionMvcAdvice(
        problemMappingProcessor, problemResolverStore, problemPostProcessor, adviceMvcInspectors);
  }

  @Order(Ordered.LOWEST_PRECEDENCE - 10)
  @ConditionalOnMissingBean(ProblemExceptionMvcAdvice.class)
  @Bean
  public ProblemExceptionMvcAdvice problemExceptionMvcAdvice(
      ProblemPostProcessor problemPostProcessor, List<AdviceMvcInspector> adviceMvcInspectors) {
    return new ProblemExceptionMvcAdvice(problemPostProcessor, adviceMvcInspectors);
  }

  @ConditionalOnClass(OncePerRequestFilter.class)
  @Configuration(proxyBeanMethods = false)
  public static class ProblemContextMvcFilterConfiguration {
    @ConditionalOnMissingBean(ProblemContextMvcFilter.class)
    @Bean
    public ProblemContextMvcFilter problemContextMvcFilter(ProblemProperties properties) {
      return new ProblemContextMvcFilter(properties);
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
        List<AdviceMvcInspector> adviceMvcInspectors) {
      return new ProblemEnhancedMvcHandler(
          problemResolverStore, problemPostProcessor, adviceMvcInspectors);
    }
  }
}
