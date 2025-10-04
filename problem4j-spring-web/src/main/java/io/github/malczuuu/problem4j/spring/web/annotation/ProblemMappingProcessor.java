package io.github.malczuuu.problem4j.spring.web.annotation;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.spring.web.ProblemContext;
import java.util.regex.Pattern;

/**
 * Converts exceptions annotated with {@link ProblemMapping} into {@link Problem} instances.
 *
 * <p>This interface defines the contract for mapping a {@link Throwable} to a Problem according to
 * its {@link ProblemMapping} annotation. Implementations are responsible for:
 *
 * <ul>
 *   <li>Reading the annotation values from the exception class.
 *   <li>Applying any placeholder interpolation or dynamic data insertion.
 *   <li>Populating standard RFC 7807 fields ({@code type}, {@code title}, {@code status}, {@code
 *       detail}, {@code instance}) and extensions.
 * </ul>
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
 *     Problem problem = processor.toProblem(ex, context);
 * }
 * }</pre>
 *
 * <p>Implementations should return {@code null} if the exception class is not annotated with {@link
 * ProblemMapping}, and may throw {@link ProblemProcessingException} if an error occurs during
 * problem creation.
 */
public interface ProblemMappingProcessor {

  Pattern PLACEHOLDER = Pattern.compile("\\{([^}]+)}");

  String MESSAGE_LABEL = "message";
  String TRACE_ID_LABEL = "traceId";

  /**
   * Convert {@link Throwable} -> {@link Problem} according to its {@link ProblemMapping}
   * annotation.
   *
   * @param t {@link Throwable} to convert (must not be {@code null})
   * @param context optional {@link ProblemContext} (allows {@code null} value)
   * @return a {@link Problem} instance, or {@code null} if the class of {@code t} is not annotated
   * @throws ProblemProcessingException when something goes wrong while building the Problem
   */
  Problem toProblem(Throwable t, ProblemContext context);

  /**
   * Checks whether the given exception class is annotated with {@link ProblemMapping}.
   *
   * @param t {@link Throwable} to check (allows {@code null} value)
   * @return {@code true} if the exception class has a {@link ProblemMapping} annotation, {@code
   *     false} otherwise
   */
  boolean isAnnotated(Throwable t);
}
