package io.github.malczuuu.problem4j.spring.web.mapping;

import io.github.malczuuu.problem4j.spring.web.format.ProblemFormat;

/**
 * Convenience base class for {@link ExceptionMapping}s. Ensures that constructor of each
 * implementation has the same signature so it wouldn't break backwards compatibility in the future.
 */
public abstract class AbstractExceptionMapping implements ExceptionMapping {

  private final Class<? extends Exception> clazz;

  private final ProblemFormat problemFormat;

  public AbstractExceptionMapping(Class<? extends Exception> clazz, ProblemFormat problemFormat) {
    this.clazz = clazz;
    this.problemFormat = problemFormat;
  }

  @Override
  public Class<? extends Exception> getExceptionClass() {
    return clazz;
  }

  protected String formatDetail(String detail) {
    return problemFormat.formatDetail(detail);
  }
}
