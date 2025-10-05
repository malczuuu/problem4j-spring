package io.github.malczuuu.problem4j.spring.webmvc.mapping;

import io.github.malczuuu.problem4j.spring.web.format.ProblemFormat;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Each nested configuration class is annotated with {@link ConditionalOnClass} to ensure that only
 * mappings for classes present on the classpath are created. This design allows the library to
 * remain compatible previous versions.
 *
 * @see io.github.malczuuu.problem4j.spring.web.mapping.ExceptionMappingConfiguration
 */
@Configuration(proxyBeanMethods = false)
public class ExceptionMappingMvcConfiguration {

  @ConditionalOnClass(NoHandlerFoundException.class)
  @Configuration(proxyBeanMethods = false)
  public static class NoHandlerFoundConfiguration {

    @Bean
    NoHandlerFoundMapping noHandlerFoundMapping(ProblemFormat problemFormat) {
      return new NoHandlerFoundMapping(problemFormat);
    }
  }

  @ConditionalOnClass(NoResourceFoundException.class)
  @Configuration(proxyBeanMethods = false)
  public static class NoResourceFoundConfiguration {

    @Bean
    NoResourceFoundMapping noResourceFoundMapping(ProblemFormat problemFormat) {
      return new NoResourceFoundMapping(problemFormat);
    }
  }
}
