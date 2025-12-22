/*
 * Copyright (c) 2025 Damian Malczewski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * SPDX-License-Identifier: MIT
 */
package io.github.problem4j.spring.web;

import com.fasterxml.jackson.databind.module.SimpleModule;
import io.github.malczuuu.problem4j.jackson.ProblemModule;
import io.github.problem4j.spring.web.annotation.DefaultProblemMappingProcessor;
import io.github.problem4j.spring.web.annotation.ProblemMappingProcessor;
import io.github.problem4j.spring.web.format.DefaultProblemFormat;
import io.github.problem4j.spring.web.format.ProblemFormat;
import io.github.problem4j.spring.web.processor.DefaultProblemPostProcessor;
import io.github.problem4j.spring.web.processor.ProblemPostProcessor;
import io.github.problem4j.spring.web.resolver.ProblemResolver;
import io.github.problem4j.spring.web.resolver.ProblemResolverConfiguration;
import java.util.List;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

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
   * <p>The default implementation, {@link DefaultProblemPostProcessor}, supports configurable
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
   * @return a new {@link DefaultProblemPostProcessor} instance
   * @see io.github.malczuuu.problem4j.core.Problem
   */
  @ConditionalOnMissingBean(ProblemPostProcessor.class)
  @Bean
  public ProblemPostProcessor problemPostProcessor(ProblemProperties properties) {
    return new DefaultProblemPostProcessor(properties);
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
