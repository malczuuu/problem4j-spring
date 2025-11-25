package io.github.malczuuu.problem4j.spring.webmvc.resolver;

import io.github.malczuuu.problem4j.spring.web.format.ProblemFormat;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Each nested configuration class is annotated with {@link ConditionalOnClass} to ensure that only
 * resolvers for classes present on the classpath are created. This design allows the library to
 * remain compatible previous versions.
 *
 * @see io.github.malczuuu.problem4j.spring.web.resolver.ProblemResolverConfiguration
 */
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Configuration(proxyBeanMethods = false)
public class ProblemResolverMvcConfiguration {

  @ConditionalOnClass(NoHandlerFoundException.class)
  @Configuration(proxyBeanMethods = false)
  public static class NoHandlerFoundProblemConfiguration {
    @ConditionalOnMissingBean(NoHandlerFoundProblemResolver.class)
    @Bean
    public NoHandlerFoundProblemResolver noHandlerFoundProblemResolver(
        ProblemFormat problemFormat) {
      return new NoHandlerFoundProblemResolver(problemFormat);
    }
  }

  @ConditionalOnClass(NoResourceFoundException.class)
  @Configuration(proxyBeanMethods = false)
  public static class NoResourceFoundProblemConfiguration {
    @ConditionalOnMissingBean(NoResourceFoundProblemResolver.class)
    @Bean
    public NoResourceFoundProblemResolver noResourceFoundProblemResolver(
        ProblemFormat problemFormat) {
      return new NoResourceFoundProblemResolver(problemFormat);
    }
  }
}
