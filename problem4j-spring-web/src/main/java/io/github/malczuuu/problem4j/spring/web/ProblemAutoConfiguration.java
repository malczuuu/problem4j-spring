package io.github.malczuuu.problem4j.spring.web;

import com.fasterxml.jackson.databind.module.SimpleModule;
import io.github.malczuuu.problem4j.jackson.ProblemModule;
import io.github.malczuuu.problem4j.spring.web.annotation.DefaultProblemMappingProcessor;
import io.github.malczuuu.problem4j.spring.web.annotation.ProblemMappingProcessor;
import io.github.malczuuu.problem4j.spring.web.format.DefaultProblemFormat;
import io.github.malczuuu.problem4j.spring.web.format.ProblemFormat;
import io.github.malczuuu.problem4j.spring.web.processor.OverridingProblemPostProcessor;
import io.github.malczuuu.problem4j.spring.web.processor.ProblemPostProcessor;
import io.github.malczuuu.problem4j.spring.web.resolver.ProblemResolver;
import io.github.malczuuu.problem4j.spring.web.resolver.ProblemResolverConfiguration;
import java.util.List;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@EnableConfigurationProperties({ProblemProperties.class})
@ConditionalOnProperty(name = "problem4j.enabled", matchIfMissing = true)
@Import({ProblemResolverConfiguration.class})
public class ProblemAutoConfiguration {

  /**
   * Provides a {@link ProblemMappingProcessor} if none is defined.
   *
   * @return a new {@link DefaultProblemMappingProcessor}
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
   * Provides a {@link ProblemPostProcessor} that applies post-processing rules to {@code Problem}
   * instances before they are returned in HTTP responses.
   *
   * <p>The default implementation, {@link OverridingProblemPostProcessor}, supports configurable
   * overrides for problem fields such as {@code type} and {@code instance}, based on the properties
   * defined in {@link ProblemProperties}. These overrides may include runtime placeholders such as:
   *
   * <ul>
   *   <li>{@code {problem.type}} - replaced with the original problem’s type URI
   *   <li>{@code {problem.instance}} - replaced with the original problem’s instance URI
   *   <li>{@code {context.traceId}} - replaced with the current trace identifier, if available
   * </ul>
   *
   * <p>This allows enriching or normalizing problem responses without modifying the original
   * exception mapping logic.
   *
   * @param properties the configuration properties containing override templates and settings
   * @return a new {@link OverridingProblemPostProcessor} instance
   * @see io.github.malczuuu.problem4j.core.Problem
   */
  @ConditionalOnMissingBean(ProblemPostProcessor.class)
  @Bean
  public ProblemPostProcessor problemPostProcessor(ProblemProperties properties) {
    return new OverridingProblemPostProcessor(properties);
  }

  /**
   * Provides a {@link ProblemResolverStore} that aggregates all {@link ProblemResolver}
   * implementations.
   *
   * @param problemResolvers all available {@link ProblemResolver} declared as components
   * @return {@link HashMapProblemResolverStore}, wrapped in {@link CachingProblemResolverStore} if
   *     caching is enabled
   */
  @ConditionalOnMissingBean(ProblemResolverStore.class)
  @Bean
  public ProblemResolverStore problemResolverStore(
      List<ProblemResolver> problemResolvers, ProblemProperties properties) {
    ProblemResolverStore problemResolverStore = new HashMapProblemResolverStore(problemResolvers);

    if (properties.getResolverCaching().isEnabled()) {
      problemResolverStore =
          new CachingProblemResolverStore(
              problemResolverStore, properties.getResolverCaching().getMaxCacheSize());
    }

    return problemResolverStore;
  }

  @ConditionalOnClass({ProblemModule.class, SimpleModule.class})
  @Configuration(proxyBeanMethods = false)
  public static class ProblemModuleConfiguration {

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
  }
}
