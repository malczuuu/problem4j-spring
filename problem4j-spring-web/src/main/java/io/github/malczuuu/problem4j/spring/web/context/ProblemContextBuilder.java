package io.github.malczuuu.problem4j.spring.web.context;

import org.springframework.lang.Nullable;

/** Builder for creating {@link ProblemContext} instances. */
public interface ProblemContextBuilder {

  /**
   * Sets the trace identifier for this context.
   *
   * @param traceId trace identifier object, may be {@code null}
   * @return this builder
   */
  ProblemContextBuilder traceId(@Nullable Object traceId);

  /**
   * Sets the trace identifier for this context.
   *
   * @param traceId trace identifier string, may be {@code null}
   * @return this builder
   */
  ProblemContextBuilder traceId(@Nullable String traceId);

  /**
   * Builds a new {@link ProblemContext} instance.
   *
   * @return new problem context
   */
  ProblemContext build();
}
