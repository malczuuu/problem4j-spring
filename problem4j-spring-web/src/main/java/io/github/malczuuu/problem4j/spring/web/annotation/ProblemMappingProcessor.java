package io.github.malczuuu.problem4j.spring.web.annotation;

import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import java.util.regex.Pattern;

/**
 * Converts exceptions annotated with {@link ProblemMapping} into {@link ProblemBuilder} instances,
 * which can be further extended or executed to create {@code Problem} response.
 *
 * <p>Implementations may optionally make use of a {@link ProblemContext} to provide request- or
 * application-specific data such as trace IDs.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * ProblemMappingProcessor processor = ...;
 * Throwable ex = new ValidationException("user-123", "email");
 * if (processor.isAnnotated(ex)) {
 *     Problem problem = processor.toBuilder(ex, context).build();
 * }
 * }</pre>
 *
 * <p>Implementations should return {@code null} if the exception class is not annotated with {@code
 * ProblemMapping}, and may throw {@link ProblemProcessingException} if an error occurs during
 * problem creation.
 *
 * @see io.github.malczuuu.problem4j.core.Problem
 */
public interface ProblemMappingProcessor {

  Pattern PLACEHOLDER = Pattern.compile("\\{([^}]+)}");

  String MESSAGE_LABEL = "message";
  String TRACE_ID_LABEL = "context.traceId";

  /**
   * Convert {@link Throwable} -> {@link ProblemBuilder} according to its {@link ProblemMapping}
   * annotation. Such builder can be further extended or executed to create {@code Problem}
   * response.
   *
   * @param t {@link Throwable} to convert (must not be {@code null})
   * @param context optional {@link ProblemContext} (allows {@code null} value)
   * @return a {@link ProblemBuilder} instance
   * @throws ProblemProcessingException when something goes wrong while building the Problem
   * @see io.github.malczuuu.problem4j.core.Problem
   */
  ProblemBuilder toProblemBuilder(Throwable t, ProblemContext context);

  /**
   * Checks whether the given exception class is annotated with {@link ProblemMapping}.
   *
   * @param t {@link Throwable} to check (allows {@code null} value)
   * @return {@code true} if the exception is annotated with {@link ProblemMapping}, {@code false}
   *     otherwise
   */
  boolean isMappingCandidate(Throwable t);
}
