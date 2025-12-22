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
package io.github.problem4j.spring.web.processor;

import io.github.problem4j.core.Problem;
import io.github.problem4j.core.ProblemContext;

/**
 * {@link ProblemPostProcessor} implementation that overrides selected fields of a {@link Problem}
 * based on configurable templates.
 *
 * <p>This processor allows the {@code type} and {@code instance} fields to be replaced with custom
 * values defined through {@link PostProcessorSettings}. Each override can include placeholders that
 * are resolved at runtime using data from the current {@link Problem} or {@link ProblemContext}.
 *
 * <p>Supported placeholders:
 *
 * <ul>
 *   <li><b>For {@code type} override:</b>
 *       <ul>
 *         <li>{@code {problem.type}} - replaced with the original problem’s {@code type}
 *       </ul>
 *   <li><b>For {@code instance} override:</b>
 *       <ul>
 *         <li>{@code {problem.instance}} - replaced with the original problem’s {@code instance}
 *         <li>{@code {context.traceId}} - replaced with the current request’s trace identifier, if
 *             available
 *       </ul>
 * </ul>
 *
 * <p>If an override template produces the same value as the existing field, no change is applied.
 * If neither override results in a change, the original {@link Problem} instance is returned
 * unchanged.
 *
 * <p>Example configuration:
 *
 * <pre>{@code
 * problem4j.type-override=https://errors.example.com/{problem.type}
 * problem4j.instance-override=/errors/{context.traceId}
 * }</pre>
 *
 * @deprecated use {@link DefaultProblemPostProcessor}
 */
@Deprecated(since = "1.1.7", forRemoval = true)
public class OverridingProblemPostProcessor extends AbstractProblemPostProcessor {

  /** Creates a post-processor using the given override settings. */
  public OverridingProblemPostProcessor(PostProcessorSettings settings) {
    super(settings);
  }
}
