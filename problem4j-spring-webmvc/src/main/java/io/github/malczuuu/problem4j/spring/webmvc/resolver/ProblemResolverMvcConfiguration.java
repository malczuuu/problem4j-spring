package io.github.malczuuu.problem4j.spring.webmvc.resolver;

import io.github.malczuuu.problem4j.spring.web.format.ProblemFormat;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
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
@Configuration(proxyBeanMethods = false)
public class ProblemResolverMvcConfiguration {

  @ConditionalOnClass(NoHandlerFoundException.class)
  @Configuration(proxyBeanMethods = false)
  public static class NoHandlerFoundConfiguration {
    @Bean
    public NoHandlerFoundResolver noHandlerFoundResolver(ProblemFormat problemFormat) {
      return new NoHandlerFoundResolver(problemFormat);
    }
  }

  @ConditionalOnClass(NoResourceFoundException.class)
  @Configuration(proxyBeanMethods = false)
  public static class NoResourceFoundConfiguration {
    @Bean
    public NoResourceFoundResolver noResourceFoundResolver(ProblemFormat problemFormat) {
      return new NoResourceFoundResolver(problemFormat);
    }
  }
}
