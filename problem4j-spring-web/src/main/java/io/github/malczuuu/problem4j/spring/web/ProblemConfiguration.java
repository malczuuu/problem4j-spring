package io.github.malczuuu.problem4j.spring.web;

import io.github.malczuuu.problem4j.jackson.ProblemModule;
import io.github.malczuuu.problem4j.spring.web.annotation.ProblemMappingProcessor;
import io.github.malczuuu.problem4j.spring.web.annotation.SimpleProblemMappingProcessor;
import io.github.malczuuu.problem4j.spring.web.format.DefaultProblemFormat;
import io.github.malczuuu.problem4j.spring.web.format.ProblemFormat;
import io.github.malczuuu.problem4j.spring.web.mapping.ExceptionMapping;
import io.github.malczuuu.problem4j.spring.web.mapping.ExceptionMappingConfiguration;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@Import({ExceptionMappingConfiguration.class})
@EnableConfigurationProperties({ProblemProperties.class})
public class ProblemConfiguration {

  /**
   * Provides a {@link ProblemModule} if none is defined.
   *
   * @return a new {@link ProblemModule}
   */
  @ConditionalOnMissingBean(ProblemModule.class)
  @Bean
  public ProblemModule problemModule() {
    return new ProblemModule();
  }

  /**
   * Provides a {@link ProblemMappingProcessor} if none is defined.
   *
   * @return a new {@link SimpleProblemMappingProcessor}
   */
  @ConditionalOnMissingBean(ProblemMappingProcessor.class)
  @Bean
  public ProblemMappingProcessor problemMappingProcessor() {
    return new SimpleProblemMappingProcessor();
  }

  /**
   * Provides a {@link ProblemFormat} based on {@link ProblemProperties} if none is defined.
   *
   * @param properties the configuration properties
   * @return a new {@link DefaultProblemFormat}
   */
  @ConditionalOnMissingBean(ProblemFormat.class)
  @Bean
  public ProblemFormat problemFormat(ProblemProperties properties) {
    return new DefaultProblemFormat(properties.getDetailFormat());
  }

  /**
   * Provides a {@link ExceptionMappingStore} that aggregates all {@link ExceptionMapping}
   * implementations.
   *
   * @param exceptionMappings all available {@link ExceptionMapping} declared as components
   * @return a new {@link CachingExceptionMappingStore}
   */
  @ConditionalOnMissingBean(ExceptionMappingStore.class)
  @Bean
  public ExceptionMappingStore exceptionMappingStore(List<ExceptionMapping> exceptionMappings) {
    return new CachingExceptionMappingStore(exceptionMappings);
  }
}
