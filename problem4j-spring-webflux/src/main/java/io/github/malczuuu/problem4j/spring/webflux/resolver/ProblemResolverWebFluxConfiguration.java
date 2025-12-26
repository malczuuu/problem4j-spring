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
 * @deprecated migrated to {@code io.github.problem4j:problem4j-spring-webflux} namespace.
 */
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@Configuration(proxyBeanMethods = false)
@Deprecated(since = "1.1.7")
public class ProblemResolverWebFluxConfiguration {}
