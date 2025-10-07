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

  public AbstractProblemResolver(Class<? extends Exception> clazz) {
    this(clazz, new IdentityProblemFormat());
  }

  public AbstractProblemResolver(Class<? extends Exception> clazz, ProblemFormat problemFormat) {
    this.clazz = clazz;
    this.problemFormat = problemFormat;
  }

  @Override
  public Class<? extends Exception> getExceptionClass() {
    return clazz;
  }

  @Override
  public ProblemBuilder resolve(
      ProblemContext context, Exception ex, HttpHeaders headers, HttpStatusCode status) {
    return Problem.builder().status(ProblemStatus.INTERNAL_SERVER_ERROR);
  }

  protected String formatDetail(String detail) {
    return problemFormat.formatDetail(detail);
  }
}
