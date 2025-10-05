package io.github.malczuuu.problem4j.spring.webflux.mapping;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

/**
 * Each nested configuration class is annotated with {@link ConditionalOnClass} to ensure that only
 * mappings for classes present on the classpath are created. This design allows the library to
 * remain compatible previous versions.
 *
 * @see io.github.malczuuu.problem4j.spring.web.mapping.ExceptionMappingConfiguration
 */
@Configuration(proxyBeanMethods = false)
public class ExceptionMappingWebFluxConfiguration {}
