package io.github.malczuuu.problem4j.spring.web.resolver;

import static io.github.malczuuu.problem4j.spring.web.util.ProblemSupport.resolveStatus;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.format.ProblemFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

/**
 * Handles {@link ResponseStatusException} thrown to signal a specific HTTP status and optional
 * reason from application code or framework components.
 *
 * <p>This exception can be thrown directly in controllers, services, or other layers to indicate
 * errors such as 404 (Not Found), 403 (Forbidden), or 500 (Internal Server Error) without relying
 * on checked exceptions or custom error types.
 *
 * <p>The handler is responsible for translating the exception into the corresponding HTTP response
 * with the specified status code, reason, and any additional details.
 */
public class ResponseStatusProblemResolver extends AbstractProblemResolver {

  public ResponseStatusProblemResolver(ProblemFormat problemFormat) {
    super(ResponseStatusException.class, problemFormat);
  }

  /**
   * Builds a {@link ProblemBuilder} reflecting {@link ResponseStatusException} and the HTTP status
   * carried by it. Ignores provided {@code status}, {@code headers}, and {@code context}; the
   * resolver always uses {@link ResponseStatusException#getStatusCode()}.
   *
   * <p>The exception's reason/message is intentionally not propagated here (can be added by a
   * custom subclass if desired) to avoid leaking internal details unless explicitly configured.
   *
   * @param context problem context (unused)
   * @param ex the {@link ResponseStatusException} to convert
   * @param headers HTTP headers (unused)
   * @param status suggested status from caller (ignored)
   * @return builder pre-populated with the exception's status code
   */
  @Override
  public ProblemBuilder resolveBuilder(
      ProblemContext context, Exception ex, HttpHeaders headers, HttpStatusCode status) {
    ResponseStatusException e = (ResponseStatusException) ex;
    return Problem.builder().status(resolveStatus(e.getStatusCode()));
  }
}
