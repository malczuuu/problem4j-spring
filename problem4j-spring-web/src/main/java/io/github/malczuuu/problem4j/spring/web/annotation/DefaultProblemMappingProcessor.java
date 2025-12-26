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
package io.github.malczuuu.problem4j.spring.web.annotation;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import java.lang.reflect.Field;
import java.util.regex.Matcher;
import org.springframework.util.StringUtils;

/**
 * This processor supports dynamic interpolation of placeholders in the annotation values. The
 * algorithm is as follows:
 *
 * <ol>
 *   <li>Check if the exception class has {@link ProblemMapping}; if not, return null.
 *   <li>Create a {@link ProblemBuilder} to accumulate the problem details.
 *   <li>For each standard field ({@code type}, {@code title}, {@code status}, {@code detail},
 *       {@code instance}):
 *       <ol>
 *         <li>Read the raw annotation value.
 *         <li>Interpolate placeholders of the form {@code {name}}:
 *             <ul>
 *               <li>{@code {message}} -> {@link Throwable#getMessage()}
 *               <li>{@code {context.traceId}} -> {@link ProblemContext#getTraceId()} (special
 *                   shorthand)
 *               <li>{@code {fieldName}} -> any field in the exception class hierarchy
 *             </ul>
 *         <li>Ignore placeholders that resolve to null or empty string.
 *         <li>Assign the interpolated value to the {@link ProblemBuilder}, ignoring invalid URIs
 *             for {@code type} and {@code instance}.
 *       </ol>
 *   <li>For extensions:
 *       <ol>
 *         <li>For each field name listed in {@link ProblemMapping#extensions()}:
 *             <ul>
 *               <li>Resolve the value using the same rules as above.
 *               <li>Ignore null or empty values.
 *             </ul>
 *       </ol>
 *   <li>Build and return the {@link ProblemBuilder}.
 * </ol>
 *
 * <p>This design allows dynamic, context-aware Problem generation, supports subclass inheritance,
 * and ensures that null or empty values do not appear in the output, making Problems concise and
 * meaningful.
 *
 * @deprecated migrated to {@code io.github.problem4j:problem4j-spring-web} namespace.
 */
@Deprecated(since = "2.0.7")
public class DefaultProblemMappingProcessor implements ProblemMappingProcessor {

  /**
   * Convert {@link Throwable} -> {@link ProblemBuilder} according to its {@link ProblemMapping}
   * annotation.
   *
   * @param t {@link Throwable} to convert (may be {@code null})
   * @param context optional {@link ProblemContext} (may be {@code null})
   * @return a {@link ProblemBuilder} instance
   * @throws ProblemProcessingException when something goes wrong while building the Problem
   */
  @Override
  public ProblemBuilder toProblemBuilder(Throwable t, ProblemContext context) {
    if (t == null) {
      return Problem.builder();
    }
    ProblemMapping mapping = findAnnotation(t.getClass());
    if (mapping == null) {
      return Problem.builder();
    }

    ProblemBuilder builder = Problem.builder();

    try {
      applyTypeOnBuilder(builder, mapping, t, context);
      applyTitleOnBuilder(builder, mapping, t, context);
      applyStatusOnBuilder(mapping, builder);
      applyDetailOnBuilder(builder, mapping, t, context);
      applyInstanceOnBuilder(builder, mapping, t, context);
      applyExtensionsOnBuilder(builder, mapping, t);
      return builder;
    } catch (ProblemProcessingException e) {
      // explicit rethrow so next clause doesn't have ProblemProcessingException as a cause
      throw e;
    } catch (RuntimeException e) {
      throw new ProblemProcessingException(
          "Unexpected failure while processing @ProblemMapping", e);
    }
  }

  /**
   * Checks whether the given exception class is annotated with {@link ProblemMapping}.
   *
   * @param t {@link Throwable} to check (may be {@code null})
   * @return {@code true} if the exception class has a {@link ProblemMapping} annotation, {@code
   *     false} otherwise
   */
  @Override
  public boolean isMappingCandidate(Throwable t) {
    return t != null && t.getClass().isAnnotationPresent(ProblemMapping.class);
  }

  /** Returns the {@link ProblemMapping} annotation from the class if present, otherwise null. */
  private ProblemMapping findAnnotation(Class<?> clazz) {
    return clazz.getAnnotation(ProblemMapping.class);
  }

  /**
   * Applies the "type" value from {@link ProblemMapping#type()} after placeholder interpolation;
   * ignores invalid URIs.
   */
  private void applyTypeOnBuilder(
      ProblemBuilder builder, ProblemMapping mapping, Throwable t, ProblemContext context) {
    String rawType = StringUtils.hasLength(mapping.type()) ? mapping.type().trim() : "";
    if (StringUtils.hasLength(rawType)) {
      String typeInterpolated = interpolate(rawType, t, context);
      if (StringUtils.hasLength(typeInterpolated)) {
        try {
          builder.type(typeInterpolated);
        } catch (IllegalArgumentException e) {
          // ignored - if type is invalid let not fail
        }
      }
    }
  }

  /** Applies the interpolated title if present and non-empty. */
  private void applyTitleOnBuilder(
      ProblemBuilder builder, ProblemMapping mapping, Throwable t, ProblemContext context) {
    String titleRaw = StringUtils.hasLength(mapping.title()) ? mapping.title().trim() : "";
    if (StringUtils.hasLength(titleRaw)) {
      String titleInterpolated = interpolate(titleRaw, t, context);
      if (StringUtils.hasLength(titleInterpolated)) {
        builder.title(titleInterpolated);
      }
    }
  }

  /** Sets the HTTP status when greater than zero. */
  private void applyStatusOnBuilder(ProblemMapping mapping, ProblemBuilder builder) {
    if (mapping.status() > 0) {
      builder.status(mapping.status());
    }
  }

  /** Applies the interpolated detail text if non-empty. */
  private void applyDetailOnBuilder(
      ProblemBuilder builder, ProblemMapping mapping, Throwable t, ProblemContext context) {
    String detailRaw = StringUtils.hasLength(mapping.detail()) ? mapping.detail().trim() : "";
    if (StringUtils.hasLength(detailRaw)) {
      String detailInterpolated = interpolate(detailRaw, t, context);
      if (StringUtils.hasLength(detailInterpolated)) {
        builder.detail(detailInterpolated);
      }
    }
  }

  /** Applies the interpolated instance value; ignores invalid URIs. */
  private void applyInstanceOnBuilder(
      ProblemBuilder builder, ProblemMapping mapping, Throwable t, ProblemContext context) {
    String rawInstance = StringUtils.hasLength(mapping.instance()) ? mapping.instance().trim() : "";
    if (StringUtils.hasLength(rawInstance)) {
      String instanceInterpolated = interpolate(rawInstance, t, context);
      if (StringUtils.hasLength(instanceInterpolated)) {
        try {
          builder.instance(instanceInterpolated);
        } catch (IllegalArgumentException e) {
          // ignored - if type is invalid let not fail
        }
      }
    }
  }

  /** Adds extension fields resolved from {@link ProblemMapping#extensions()}. */
  private void applyExtensionsOnBuilder(
      ProblemBuilder builder, ProblemMapping mapping, Throwable t) {
    String[] extensions = mapping.extensions();
    for (String name : extensions) {
      if (!StringUtils.hasText(name)) {
        continue;
      }
      name = name.trim();
      Object value = resolvePlaceholderSource(t, name);
      if (value != null && !(value instanceof CharSequence str && !StringUtils.hasLength(str))) {
        builder.extension(name, value);
      }
    }
  }

  /**
   * Interpolates placeholders of the form {@code {name}}. Supported keys:
   *
   * <ul>
   *   <li>{@code message} - throwable message
   *   <li>{@code context.traceId} - trace ID from context
   *   <li>Any other token - value of a matching field in the throwable class hierarchy
   * </ul>
   *
   * <p>Missing values resolve to an empty string.
   */
  private String interpolate(String template, Throwable t, ProblemContext context) {
    Matcher m = PLACEHOLDER.matcher(template);

    StringBuilder sb = new StringBuilder();
    while (m.find()) {
      String key = m.group(1);
      String replacement;

      if (MESSAGE_LABEL.equals(key)) {
        replacement = t.getMessage() == null ? "" : String.valueOf(t.getMessage());
      } else if (TRACE_ID_LABEL.equals(key)) {
        replacement =
            (context == null || !StringUtils.hasLength(context.getTraceId()))
                ? ""
                : context.getTraceId();
      } else {
        Object v = resolvePlaceholderSource(t, key);
        replacement = v == null ? "" : String.valueOf(v);
      }
      replacement = Matcher.quoteReplacement(replacement);
      m.appendReplacement(sb, replacement);
    }
    m.appendTail(sb);
    return sb.toString();
  }

  /** Resolves a placeholder by reflective field lookup up the throwable class hierarchy. */
  private Object resolvePlaceholderSource(Throwable t, String name) {
    if (!StringUtils.hasLength(name)) {
      return null;
    }
    Class<?> search = t.getClass();
    while (search != null && search != Object.class) {
      try {
        Field f = search.getDeclaredField(name);
        f.setAccessible(true);
        return f.get(t);
      } catch (NoSuchFieldException ignored) {
        // ignored, loop will go to parent class to see if that field exists there
      } catch (Exception ignored) {
        return null;
      }
      search = search.getSuperclass();
    }
    return null;
  }
}
