package io.github.malczuuu.problem4j.spring.web;

import io.github.malczuuu.problem4j.jackson.ProblemModule;
import io.github.malczuuu.problem4j.spring.web.annotation.DefaultProblemMappingProcessor;
import io.github.malczuuu.problem4j.spring.web.annotation.ProblemMappingProcessor;
import io.github.malczuuu.problem4j.spring.web.format.DefaultProblemFormat;
import io.github.malczuuu.problem4j.spring.web.format.ProblemFormat;
import io.github.malczuuu.problem4j.spring.web.resolver.ProblemResolver;
import io.github.malczuuu.problem4j.spring.web.resolver.ProblemResolverConfiguration;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@Import({ProblemResolverConfiguration.class})
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
   * @return a new {@link
   *     io.github.malczuuu.problem4j.spring.web.annotation.DefaultProblemMappingProcessor}
   */
  @ConditionalOnMissingBean(ProblemMappingProcessor.class)
  @Bean
  public ProblemMappingProcessor problemMappingProcessor() {
    return new DefaultProblemMappingProcessor();
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
   * Provides a {@link ProblemResolverStore} that aggregates all {@link ProblemResolver}
   * implementations.
   *
   * @param problemResolvers all available {@link ProblemResolver} declared as components
   * @return {@link HashMapProblemResolverStore}, possibly wrapped in {@link
   *     CachingProblemResolverStore} if caching is enabled
   */
  @ConditionalOnMissingBean(ProblemResolverStore.class)
  @Bean
  public ProblemResolverStore problemResolverStore(
      List<ProblemResolver> problemResolvers, ProblemProperties properties) {
    ProblemResolverStore problemResolverStore = new HashMapProblemResolverStore(problemResolvers);

    if (properties.getCaching().isEnabled()) {
      problemResolverStore =
          new CachingProblemResolverStore(
              problemResolverStore, properties.getCaching().getMaxCacheSize());
    }

    return problemResolverStore;
  }
}
