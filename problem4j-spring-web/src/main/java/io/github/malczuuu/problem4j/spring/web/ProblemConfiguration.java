package io.github.malczuuu.problem4j.spring.web;

import io.github.malczuuu.problem4j.jackson.ProblemModule;
import io.github.malczuuu.problem4j.spring.web.annotation.ProblemMappingProcessor;
import io.github.malczuuu.problem4j.spring.web.annotation.SimpleProblemMappingProcessor;
import io.github.malczuuu.problem4j.spring.web.format.DetailFormat;
import io.github.malczuuu.problem4j.spring.web.format.JacksonPropertyNameFormat;
import io.github.malczuuu.problem4j.spring.web.format.PropertyNameFormat;
import io.github.malczuuu.problem4j.spring.web.format.SimpleDetailFormat;
import io.github.malczuuu.problem4j.spring.web.mapping.ExceptionMapping;
import io.github.malczuuu.problem4j.spring.web.mapping.ExceptionMappingConfiguration;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.JacksonProperties;
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
   * Provides a {@link DetailFormat} based on {@link ProblemProperties} if none is defined.
   *
   * @param properties the configuration properties
   * @return a new {@link SimpleDetailFormat}
   */
  @ConditionalOnMissingBean(DetailFormat.class)
  @Bean
  public DetailFormat detailFormat(ProblemProperties properties) {
    return new SimpleDetailFormat(properties.getDetailFormat());
  }

  /**
   * Provides a {@link PropertyNameFormat} based on {@link JacksonProperties}.
   *
   * @param jacksonProperties the Jackson configuration properties
   * @return a new {@link JacksonPropertyNameFormat}
   */
  @ConditionalOnMissingBean(PropertyNameFormat.class)
  @Bean
  public PropertyNameFormat propertyNameFormat(JacksonProperties jacksonProperties) {
    return new JacksonPropertyNameFormat(jacksonProperties.getPropertyNamingStrategy());
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
