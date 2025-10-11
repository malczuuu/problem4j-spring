package io.github.malczuuu.problem4j.spring.web.resolver;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.format.IdentityProblemFormat;
import io.github.malczuuu.problem4j.spring.web.format.ProblemFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;

/** Convenience base class for {@link ProblemResolver}-s. */
public abstract class AbstractProblemResolver implements ProblemResolver {

  private final Class<? extends Exception> clazz;

  private final ProblemFormat problemFormat;

  /**
   * Creates a resolver for the given exception type using {@link IdentityProblemFormat} (no detail
   * transformation).
   *
   * @param clazz exception subtype this resolver is responsible for
   */
  public AbstractProblemResolver(Class<? extends Exception> clazz) {
    this(clazz, new IdentityProblemFormat());
  }

  /**
   * Creates a resolver for the given exception type with a custom {@link ProblemFormat} applied to
   * any detail text via {@link #formatDetail(String)}.
   *
   * @param clazz exception subtype this resolver is responsible for
   * @param problemFormat formatting strategy for detail (must not be {@code null})
   */
  public AbstractProblemResolver(Class<? extends Exception> clazz, ProblemFormat problemFormat) {
    this.clazz = clazz;
    this.problemFormat = problemFormat;
  }

  /** Returns the configured exception class this resolver supports. */
  @Override
  public Class<? extends Exception> getExceptionClass() {
    return clazz;
  }

  /**
   * Default implementation returns a builder with status {@code INTERNAL_SERVER_ERROR}. Subclasses
   * should override to populate fields like type, title, detail, and extensions.
   *
   * @see ProblemResolver#resolveBuilder(ProblemContext, Exception, HttpHeaders, HttpStatusCode)
   * @see ProblemStatus#INTERNAL_SERVER_ERROR
   */
  @Override
  public ProblemBuilder resolveBuilder(
      ProblemContext context, Exception ex, HttpHeaders headers, HttpStatusCode status) {
    return Problem.builder().status(ProblemStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Applies the configured {@link ProblemFormat} to a detail string (may return unchanged value if
   * using {@link IdentityProblemFormat}).
   *
   * @param detail original detail (nullable)
   * @return formatted detail (never null if input not null)
   */
  protected String formatDetail(String detail) {
    return problemFormat.formatDetail(detail);
  }
}
