package io.github.malczuuu.problem4j.spring.web.resolver;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;

/**
 * Represents a resolver from a specific {@link Exception} to a {@link ProblemBuilder}, can be
 * further extended or executed to create {@link io.github.malczuuu.problem4j.core.Problem}
 * response.
 *
 * <p>Implementations are supposed to be stateless.
 */
public interface ProblemResolver {
  /**
   * Returns the type of {@link Exception} this resolver supports.
   *
   * @return supported exception class
   */
  Class<? extends Exception> getExceptionClass();

  /**
   * Resolves the given exception into a {@link ProblemBuilder}.
   *
   * @param context problem context
   * @param ex exception to resolve
   * @param headers to be included in HTTP response
   * @param status HTTP status recommented by caller
   * @return problem builder representing the resolved problem
   */
  ProblemBuilder resolveBuilder(
      ProblemContext context, Exception ex, HttpHeaders headers, HttpStatusCode status);

  /**
   * Resolves the given exception into an immutable {@link Problem}.
   *
   * <p>This method is a convenient shortcut for callers who do not need to modify the problem.
   * Internally, it delegates to {@link #resolveBuilder(ProblemContext, Exception, HttpHeaders,
   * HttpStatusCode)} and immediately calls {@link ProblemBuilder#build()} to produce a fully
   * immutable {@link Problem}.
   *
   * <p>Use {@link #resolveBuilder(ProblemContext, Exception, HttpHeaders, HttpStatusCode)} directly
   * if you need to further customize or enrich the problem before building.
   *
   * @param context problem context
   * @param ex exception to resolve
   * @param headers HTTP headers to include in the response
   * @param status HTTP status recommended by the caller
   * @return an immutable {@link Problem} representing the resolved error
   */
  default Problem resolveProblem(
      ProblemContext context, Exception ex, HttpHeaders headers, HttpStatusCode status) {
    return resolveBuilder(context, ex, headers, status).build();
  }
}
