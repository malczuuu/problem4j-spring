package io.github.malczuuu.problem4j.spring.web.context;

/**
 * Context passed for problem processing.
 *
 * <p>Used by components such as:
 *
 * <ul>
 *   <li>{@link io.github.malczuuu.problem4j.spring.web.annotation.ProblemMappingProcessor}
 *   <li>{@link io.github.malczuuu.problem4j.spring.web.resolver.ProblemResolver}
 * </ul>
 *
 * Implementations provide access to values used for message interpolation or metadata enrichment.
 */
public interface ProblemContext {

  /**
   * Creates a new {@link ProblemContextBuilder}.
   *
   * @return new builder instance
   */
  static ProblemContextBuilder builder() {
    return new ProblemContextBuilderImpl();
  }

  /**
   * Creates an empty {@link ProblemContext}.
   *
   * @return empty problem context
   */
  static ProblemContext empty() {
    return ProblemContextImpl.EMPTY;
  }

  /**
   * Creates a {@link ProblemContext} containing only a trace identifier.
   *
   * @param traceId trace identifier object, may be {@code null}
   * @return new problem context
   */
  static ProblemContext ofTraceId(Object traceId) {
    return builder().traceId(traceId).build();
  }

  /**
   * Creates a {@link ProblemContext} containing only a trace identifier.
   *
   * @param traceId trace identifier string, may be {@code null}
   * @return new problem context
   */
  static ProblemContext ofTraceId(String traceId) {
    return builder().traceId(traceId).build();
  }

  /**
   * Returns the trace identifier associated with this context.
   *
   * @return trace identifier, or {@code null} if not set
   */
  String getTraceId();
}
