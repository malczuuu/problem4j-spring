package io.github.malczuuu.problem4j.spring.web.resolver;

import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.resolveStatus;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.format.ProblemFormat;
import io.github.malczuuu.problem4j.spring.web.internal.ViolationResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

/**
 * Resolves {@link HandlerMethodValidationException} (Spring's aggregated method validation errors)
 * into a {@link Problem} representation.
 *
 * <p>For 4xx statuses it produces a validation problem containing an {@code errors} extension with
 * parameter violations (via {@link ViolationResolver}). For 5xx statuses it returns only a basic
 * problem with the resolved status, avoiding leaking validation details when the server indicates
 * an internal failure.
 */
public class HandlerMethodValidationResolver extends AbstractProblemResolver {

  private final ViolationResolver violationResolver;

  public HandlerMethodValidationResolver(ProblemFormat problemFormat) {
    super(HandlerMethodValidationException.class, problemFormat);
    violationResolver = new ViolationResolver(problemFormat);
  }

  /**
   * Builds a {@link ProblemBuilder} for a {@link HandlerMethodValidationException}. If the provided
   * status is 5xx, returns a minimal problem with that status only. Otherwise, includes validation
   * violations collected by {@link ViolationResolver} and preserves the caller-provided status.
   *
   * @param context problem context (unused for method validation aggregation)
   * @param ex the thrown validation exception (must be {@link HandlerMethodValidationException})
   * @param headers HTTP headers (unused)
   * @param status suggested HTTP status from caller (controls 4xx vs 5xx branch)
   * @return builder representing validation failure (4xx) or minimal error (5xx)
   */
  @Override
  public ProblemBuilder resolveBuilder(
      ProblemContext context, Exception ex, HttpHeaders headers, HttpStatusCode status) {
    HandlerMethodValidationException e = (HandlerMethodValidationException) ex;
    if (status.is5xxServerError()) {
      return Problem.builder().status(resolveStatus(status));
    }
    return violationResolver.from(e).status(resolveStatus(status));
  }
}
