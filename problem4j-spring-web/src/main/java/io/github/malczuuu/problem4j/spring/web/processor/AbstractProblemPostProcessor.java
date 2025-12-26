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
package io.github.malczuuu.problem4j.spring.web.processor;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.spring.web.annotation.ProblemMappingProcessor;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import java.util.Optional;
import org.springframework.util.StringUtils;

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
 * @deprecated migrated to {@code io.github.problem4j:problem4j-spring-web} namespace.
 */
@Deprecated(since = "1.1.7")
public abstract class AbstractProblemPostProcessor implements ProblemPostProcessor {

  private final PostProcessorSettings settings;

  /** Creates a post-processor using the given override settings. */
  public AbstractProblemPostProcessor(PostProcessorSettings settings) {
    this.settings = settings;
  }

  /**
   * Applies configured overrides to {@code type} and/or {@code instance}.
   *
   * <p>About {@code about:blank}: when the original type is {@code null} or {@code
   * Problem#BLANK_TYPE}, the placeholder {@code {problem.type}} resolves to an empty string
   * (templates naturally collapse). If the template is exactly {@code {problem.type}}, the original
   * {@code about:blank} value is preserved instead of becoming empty.
   */
  @Override
  public Problem process(ProblemContext context, Problem problem) {
    if (context == null) {
      context = ProblemContext.empty();
    }

    ProblemBuilder builder = null;

    // Override type only if {problem.type} is referenced and original type is valid
    builder = overrideProblemType(context, problem, builder);

    builder = overrideProblemInstance(context, problem, builder);

    return builder != null ? builder.build() : problem;
  }

  /** Applies a {@code type} override based on the configured template. */
  protected ProblemBuilder overrideProblemType(
      ProblemContext context, Problem problem, ProblemBuilder builder) {
    if (!StringUtils.hasLength(settings.getTypeOverride())) {
      return builder;
    }

    String template = settings.getTypeOverride();
    boolean requiresProblemType = template.contains("{problem.type}");
    boolean hasProblemType = problem.isTypeNonBlank();

    if (canOverride(requiresProblemType, hasProblemType)) {
      Optional<String> newTypeCandidate = overrideType(context, problem);
      if (newTypeCandidate.isPresent()) {
        if (builder == null) {
          builder = problem.toBuilder();
        }
        builder = builder.type(newTypeCandidate.get());
      }
    }
    return builder;
  }

  /** Determines whether the {@code type} can be overridden. */
  protected boolean canOverride(boolean requiresProblemType, boolean hasProblemType) {
    return !requiresProblemType || hasProblemType;
  }

  /**
   * Because this implementation does not try to resolve dynamic fields, it does not reuse the code
   * from {@code DefaultProblemMappingProcessor}. Instead, we rely on simple substring replacement
   * in form of {@link String#replace(CharSequence, CharSequence)}, and then removing all remaining
   * unknown variables.
   *
   * <p>Note that it does not override {@link Problem#BLANK_TYPE} values.
   *
   * <p>If the algorithm discovers remaining placeholders that are unresolved, overriding is aborted
   * and original value is restored.
   *
   * @see io.github.malczuuu.problem4j.spring.web.annotation.DefaultProblemMappingProcessor
   */
  protected Optional<String> overrideType(ProblemContext context, Problem problem) {
    if (!problem.isTypeNonBlank() || !StringUtils.hasLength(settings.getTypeOverride())) {
      return Optional.empty();
    }

    String template = settings.getTypeOverride();
    String original = stringOrEmpty(problem.getType());
    String resolved = template.replace("{problem.type}", original);

    if (hasRemainingUnknownPlaceholders(resolved)) {
      return Optional.empty();
    }

    return Optional.of(resolved).filter(StringUtils::hasLength);
  }

  /** Applies an {@code instance} override based on the configured template. */
  protected ProblemBuilder overrideProblemInstance(
      ProblemContext context, Problem problem, ProblemBuilder builder) {
    if (!StringUtils.hasLength(settings.getInstanceOverride())) {
      return builder;
    }

    String template = settings.getInstanceOverride();
    boolean needsProblemInstance = template.contains("{problem.instance}");
    boolean needsTraceId = template.contains("{context.traceId}");
    boolean hasProblemInstance =
        problem.getInstance() != null && StringUtils.hasLength(problem.getInstance().toString());
    boolean hasTraceId = StringUtils.hasLength(context.getTraceId());

    if (canOverride(needsProblemInstance, hasProblemInstance, needsTraceId, hasTraceId)) {
      String newInstance = overrideInstance(context, problem);
      if (!newInstance.equals(stringOrEmpty(problem.getInstance()))) {
        if (builder == null) {
          builder = problem.toBuilder();
        }
        builder.instance(newInstance);
      }
    }
    return builder;
  }

  /** Determines whether the {@code instance} can be overridden. */
  protected boolean canOverride(
      boolean needsProblemInstance,
      boolean hasProblemInstance,
      boolean needsTraceId,
      boolean hasTraceId) {
    return (!needsProblemInstance || hasProblemInstance) && (!needsTraceId || hasTraceId);
  }

  /**
   * Because this implementation does not try to resolve dynamic fields, it does not reuse the code
   * from {@code DefaultProblemMappingProcessor}. Instead, we rely on simple substring replacement
   * in form of {@link String#replace(CharSequence, CharSequence)}, and then removing all remaining
   * unknown variables.
   *
   * <p>If the algorithm discovers remaining placeholders that are unresolved, overriding is aborted
   * and original value is restored.
   *
   * @see io.github.malczuuu.problem4j.spring.web.annotation.DefaultProblemMappingProcessor
   */
  protected String overrideInstance(ProblemContext context, Problem problem) {
    if (!StringUtils.hasLength(settings.getInstanceOverride())) {
      return stringOrEmpty(problem.getInstance());
    }

    String template = settings.getInstanceOverride();
    String instanceValue = stringOrEmpty(problem.getInstance());
    String traceIdValue = stringOrEmpty(context.getTraceId());

    template = template.replace("{problem.instance}", instanceValue);
    template = template.replace("{context.traceId}", traceIdValue);

    if (hasRemainingUnknownPlaceholders(template)) {
      return problem.getInstance().toString();
    }

    return template;
  }

  /** Converts the given value to a string or returns an empty string if {@code null}. */
  protected String stringOrEmpty(Object value) {
    return value != null ? value.toString() : "";
  }

  /**
   * Because this implementation does not try to resolve dynamic fields, it does not reuse the code
   * from {@code DefaultProblemMappingProcessor}. Instead, we rely on simple substring replacement
   * in form of {@link String#replace(CharSequence, CharSequence)}, and then removing all remaining
   * unknown variables.
   *
   * @see io.github.malczuuu.problem4j.spring.web.annotation.DefaultProblemMappingProcessor
   */
  protected boolean hasRemainingUnknownPlaceholders(String value) {
    return ProblemMappingProcessor.PLACEHOLDER.matcher(value).find();
  }

  /** Returns the post-processor configuration settings. */
  protected PostProcessorSettings getSettings() {
    return settings;
  }
}
