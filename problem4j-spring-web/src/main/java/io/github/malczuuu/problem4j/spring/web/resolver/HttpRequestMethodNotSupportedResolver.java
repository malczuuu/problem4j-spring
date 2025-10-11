package io.github.malczuuu.problem4j.spring.web.resolver;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.format.ProblemFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.HttpRequestMethodNotSupportedException;

/**
 * Handles {@link HttpRequestMethodNotSupportedException} thrown when a client sends an HTTP request
 * using a method not supported by the target handler.
 *
 * <p>This typically occurs when the request uses a method (e.g., POST, GET, PUT, DELETE) that the
 * controller or endpoint does not allow.
 *
 * <p>The handler is responsible for returning an appropriate HTTP 405 (Method Not Allowed)
 * response, often including the list of supported methods in the {@code Allow} header.
 *
 * <p>Always resolves to a {@link Problem} with status {@link ProblemStatus#METHOD_NOT_ALLOWED}.
 */
public class HttpRequestMethodNotSupportedResolver extends AbstractProblemResolver {

  public HttpRequestMethodNotSupportedResolver(ProblemFormat problemFormat) {
    super(HttpRequestMethodNotSupportedException.class, problemFormat);
  }

  /**
   * Returns a {@link ProblemBuilder} with {@link ProblemStatus#METHOD_NOT_ALLOWED} (HTTP 405).
   * Other parameters ({@code context}, {@code headers}, {@code status}) are ignored because the
   * status is mandated by the semantics of {@link HttpRequestMethodNotSupportedException}.
   *
   * @param context problem context (unused)
   * @param ex the triggering {@link HttpRequestMethodNotSupportedException}
   * @param headers HTTP headers (unused)
   * @param status suggested status from caller (ignored; 405 enforced)
   * @return builder pre-populated with 405 status
   */
  @Override
  public ProblemBuilder resolveBuilder(
      ProblemContext context, Exception ex, HttpHeaders headers, HttpStatusCode status) {
    return Problem.builder().status(ProblemStatus.METHOD_NOT_ALLOWED);
  }
}
