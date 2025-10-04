package io.github.malczuuu.problem4j.spring.web;

/**
 * Context passed to {@code ProblemMappingProcessor.toProblem(...)}.
 *
 * <p>Implementations should provide convenient access to values used in {@code Problem}
 * interpolation.
 */
public interface ProblemContext {

  String getTraceId();
}
