package io.github.malczuuu.problem4j.spring.webflux.resolver;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;

/**
 * Each nested configuration class is annotated with {@link ConditionalOnClass} to ensure that only
 * mappings for classes present on the classpath are created. This design allows the library to
 * remain compatible previous versions.
 *
 * @see io.github.malczuuu.problem4j.spring.web.resolver.ProblemResolverConfiguration
 */
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@Configuration(proxyBeanMethods = false)
public class ProblemResolverWebFluxConfiguration {}
