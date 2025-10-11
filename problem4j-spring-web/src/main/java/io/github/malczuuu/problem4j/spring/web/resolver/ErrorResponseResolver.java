package io.github.malczuuu.problem4j.spring.web.resolver;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.format.ProblemFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.ErrorResponseException;

/**
 * Handles {@link ErrorResponseException} thrown when a controller or framework component raises an
 * error represented by an {@code ErrorResponse}.
 *
 * <p>This exception is typically used by Spring MVC or WebFlux to signal HTTP errors such as 400,
 * 404, or 500, carrying both an {@link HttpStatusCode} and structured error details.
 *
 * <p>It may be thrown programmatically from application code or internally by Spring when request
 * processing fails and an {@code ErrorResponse} needs to be returned to the client.
 *
 * @see org.springframework.web.ErrorResponse
 */
public class ErrorResponseResolver extends AbstractProblemResolver {

  public ErrorResponseResolver(ProblemFormat problemFormat) {
    super(ErrorResponseException.class, problemFormat);
  }

  /**
   * Converts the given {@link ErrorResponseException} into a {@link ProblemBuilder} by copying its
   * body fields (type, title, detail, instance) and status code. Any additional properties present
   * in the underlying {@code ErrorResponse} are added as extensions.
   *
   * <p>The provided {@code status} parameter is ignored in favor of the status contained in the
   * exception. {@code headers} and {@code context} are currently not used but are part of the SPI.
   *
   * @param context problem context (unused)
   * @param ex the {@link ErrorResponseException} to convert
   * @param headers HTTP response headers (unused)
   * @param status suggested status from caller (ignored)
   * @return a builder representing the problem described by the exception
   */
  @Override
  public ProblemBuilder resolveBuilder(
      ProblemContext context, Exception ex, HttpHeaders headers, HttpStatusCode status) {
    ErrorResponseException e = (ErrorResponseException) ex;
    ProblemBuilder builder =
        Problem.builder()
            .type(e.getBody().getType())
            .title(e.getBody().getTitle())
            .status(e.getStatusCode().value())
            .detail(e.getBody().getDetail())
            .instance(e.getBody().getInstance());

    if (e.getBody().getProperties() != null) {
      builder = builder.extension(e.getBody().getProperties());
    }

    return builder;
  }
}
