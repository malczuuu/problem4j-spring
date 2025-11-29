package io.github.malczuuu.problem4j.spring.web;

import com.fasterxml.jackson.databind.Module;
import io.github.malczuuu.problem4j.jackson.ProblemModule;
import io.github.malczuuu.problem4j.spring.web.annotation.DefaultProblemMappingProcessor;
import io.github.malczuuu.problem4j.spring.web.annotation.ProblemMappingProcessor;
import io.github.malczuuu.problem4j.spring.web.format.DefaultProblemFormat;
import io.github.malczuuu.problem4j.spring.web.format.ProblemFormat;
import io.github.malczuuu.problem4j.spring.web.format.ProblemJsonMapperBuilderCustomizer;
import io.github.malczuuu.problem4j.spring.web.format.ProblemXmlMapperBuilderCustomizer;
import io.github.malczuuu.problem4j.spring.web.processor.OverridingProblemPostProcessor;
import io.github.malczuuu.problem4j.spring.web.processor.ProblemPostProcessor;
import io.github.malczuuu.problem4j.spring.web.resolver.ProblemResolver;
import io.github.malczuuu.problem4j.spring.web.resolver.ProblemResolverConfiguration;
import java.util.List;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.boot.jackson.autoconfigure.XmlMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.dataformat.xml.XmlMapper;

@AutoConfiguration
@EnableConfigurationProperties({ProblemProperties.class})
@ConditionalOnProperty(name = "problem4j.enabled", matchIfMissing = true)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.ANY)
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
   * @return {@link DefaultProblemResolverStore}, wrapped in {@link CachingProblemResolverStore} if
   *     caching is enabled
   */
  @ConditionalOnMissingBean(ProblemResolverStore.class)
  @Bean
  public ProblemResolverStore problemResolverStore(
      List<ProblemResolver> problemResolvers, ProblemProperties properties) {
    ProblemResolverStore problemResolverStore = new DefaultProblemResolverStore(problemResolvers);

    if (properties.getResolverCaching().isEnabled()) {
      problemResolverStore =
          new CachingProblemResolverStore(
              problemResolverStore, properties.getResolverCaching().getMaxCacheSize());
    }

    return problemResolverStore;
  }

  /** Configuration for JSON support in Problem serialization. */
  @ConditionalOnClass({JsonMapperBuilderCustomizer.class, JsonMapper.class})
  @Configuration(proxyBeanMethods = false)
  public static class ProblemJsonMapperConfiguration {

    /**
     * Creates a {@link ProblemJsonMapperBuilderCustomizer} to add the {@code ProblemJacksonMixIn}
     * to the JSON mapper for consistent Problem serialization.
     *
     * @return a new ProblemJsonMapperBuilderCustomizer bean
     * @see io.github.malczuuu.problem4j.jackson3.ProblemJacksonMixIn
     */
    @ConditionalOnMissingBean(ProblemJsonMapperBuilderCustomizer.class)
    @Bean
    public ProblemJsonMapperBuilderCustomizer problemJsonMapperBuilderCustomizer() {
      return new ProblemJsonMapperBuilderCustomizer();
    }
  }

  /** Configuration for XML support in Problem serialization. */
  @ConditionalOnClass({XmlMapperBuilderCustomizer.class, XmlMapper.class})
  @Configuration(proxyBeanMethods = false)
  public static class ProblemXmlMapperConfiguration {

    /**
     * Creates a {@link ProblemXmlMapperBuilderCustomizer} to add the {@code ProblemJacksonMixIn} to
     * the XML mapper for consistent Problem serialization.
     *
     * @return a new ProblemJsonMapperBuilderCustomizer bean
     * @see io.github.malczuuu.problem4j.jackson3.ProblemJacksonMixIn
     */
    @ConditionalOnMissingBean(ProblemXmlMapperBuilderCustomizer.class)
    @Bean
    public ProblemXmlMapperBuilderCustomizer problemXmlMapperBuilderCustomizer() {
      return new ProblemXmlMapperBuilderCustomizer();
    }
  }

  /**
   * If Jackson2 is present on the classpath, configures a {@link ProblemModule} bean. Note that
   * Spring Boot 4 does not include Jackson2 by default. To make it work, add {@code
   * spring-boot-jackson2} dependency manually.
   *
   * @see <a
   *     href="https://github.com/spring-projects/spring-boot/blob/v4.0.0/module/spring-boot-jackson2/src/main/java/org/springframework/boot/jackson2/autoconfigure/Jackson2AutoConfiguration.java#L86">
   *     <code>Jackson2AutoConfiguration</code></a>
   * @deprecated since 2.0.0 as Spring Boot team plans to remove Jackson 2 legacy support in 4.2.0
   */
  @ConditionalOnClass({ProblemModule.class, Module.class})
  @Configuration(proxyBeanMethods = false)
  @Deprecated(since = "2.0.0", forRemoval = true)
  public static class ProblemJackson2ModuleConfiguration {

    /**
     * Provides a {@link ProblemModule} if none is defined.
     *
     * @return a new {@link ProblemModule}
     */
    @ConditionalOnMissingBean(ProblemModule.class)
    @Bean
    public ProblemModule problemJackson2Module() {
      return new ProblemModule();
    }
  }
}
