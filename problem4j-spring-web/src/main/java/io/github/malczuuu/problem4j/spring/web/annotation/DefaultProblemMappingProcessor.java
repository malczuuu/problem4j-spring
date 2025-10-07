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
 *               <li>{@code {traceId}} -> {@link ProblemContext#getTraceId()} (special shorthand)
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
 */
public class DefaultProblemMappingProcessor implements ProblemMappingProcessor {

  /**
   * Convert {@link Throwable} -> {@link ProblemBuilder} according to its {@link ProblemMapping}
   * annotation.
   *
   * @param t {@link Throwable} to convert (must not be {@code null})
   * @param context optional {@link ProblemContext} (allows {@code null} value)
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
   * @param t {@link Throwable} to check (allows {@code null} value)
   * @return {@code true} if the exception class has a {@link ProblemMapping} annotation, {@code
   *     false} otherwise
   */
  @Override
  public boolean isMappingCandidate(Throwable t) {
    return t != null && t.getClass().isAnnotationPresent(ProblemMapping.class);
  }

  private ProblemMapping findAnnotation(Class<?> clazz) {
    if (clazz == null) {
      return null;
    }
    return clazz.getAnnotation(ProblemMapping.class);
  }

  private void applyTypeOnBuilder(
      ProblemBuilder builder, ProblemMapping mapping, Throwable t, ProblemContext context) {
    String rawType = mapping.type() == null ? "" : mapping.type().trim();
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

  private void applyTitleOnBuilder(
      ProblemBuilder builder, ProblemMapping mapping, Throwable t, ProblemContext context) {
    String titleRaw = mapping.title() == null ? "" : mapping.title();
    if (!titleRaw.trim().isEmpty()) {
      String titleInterpolated = interpolate(titleRaw, t, context);
      if (StringUtils.hasLength(titleInterpolated)) {
        builder.title(titleInterpolated);
      }
    }
  }

  private void applyStatusOnBuilder(ProblemMapping mapping, ProblemBuilder builder) {
    if (mapping.status() > 0) {
      builder.status(mapping.status());
    }
  }

  private void applyDetailOnBuilder(
      ProblemBuilder builder, ProblemMapping mapping, Throwable t, ProblemContext context) {
    String detailRaw = mapping.detail() == null ? "" : mapping.detail();
    if (!detailRaw.trim().isEmpty()) {
      String detailInterpolated = interpolate(detailRaw, t, context);
      if (StringUtils.hasLength(detailInterpolated)) {
        builder.detail(detailInterpolated);
      }
    }
  }

  private void applyInstanceOnBuilder(
      ProblemBuilder builder, ProblemMapping mapping, Throwable t, ProblemContext context) {
    String rawInstance = mapping.instance() == null ? "" : mapping.instance().trim();
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

  private void applyExtensionsOnBuilder(
      ProblemBuilder builder, ProblemMapping mapping, Throwable t) {
    String[] extensions = mapping.extensions();
    if (extensions != null) {
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
  }

  /**
   * Interpolate placeholders of form {name}. Special forms: - {message} - {context.key} - {traceId}
   * (shorthand for {context.traceId}) - other names: first look for instance field (including
   * private/superclass), then static fields, then getter methods.
   *
   * <p>If a placeholder resolves to null — it's replaced by empty string.
   */
  private String interpolate(String template, Throwable t, ProblemContext context) {
    if (template == null) {
      return null;
    }
    Matcher m = PLACEHOLDER.matcher(template);

    StringBuilder sb = new StringBuilder();
    while (m.find()) {
      String key = m.group(1);
      String replacement;

      if (MESSAGE_LABEL.equals(key)) {
        replacement = t.getMessage() == null ? "" : String.valueOf(t.getMessage());
      } else if (TRACE_ID_LABEL.equals(key)) {
        replacement = (context == null || context.getTraceId() == null) ? "" : context.getTraceId();
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

  /**
   * Resolve a value for a placeholder name from the throwable or context. Priority: 1) instance
   * field (including private) search up class hierarchy 2) static field (including private) 3)
   * public getter method: getX() or isX() 4) context.get(name)
   */
  private Object resolvePlaceholderSource(Throwable t, String name) {
    if (t == null || !StringUtils.hasLength(name)) {
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
      } catch (Throwable ignored) {
        return null;
      }
      search = search.getSuperclass();
    }
    return null;
  }
}
